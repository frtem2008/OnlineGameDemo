package Server;

import GameObjects.Block;
import GameObjects.Game;
import GameObjects.Player;
import Online.Connection;
import Online.Message;
import Online.MessagePayloadObjects.PayloadStringData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameFullData;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameTickData;
import Online.MessageType;
import Online.OnlinePlayer;
import Timer.Timer;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private static final Set<OnlinePlayer> connectedPlayers = ConcurrentHashMap.newKeySet();
    private static Game game;
    private static Timer timer;
    private static final AtomicInteger gameSendTimes = new AtomicInteger(0);

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(26780)) {
            timer = new Timer();
            System.out.println("Timer started!");
            System.out.println("Server started on port " + server.getLocalPort());
            System.out.println("Waiting for clients to connect");

            game = new Game();
            game.add(new Block(200, 300, 100, 50, Color.magenta));
            Thread gameSendingThread = new Thread(() -> {
                double lastUpdate = 0;
                final long GAME_SEND_TIMEOUT = 30;
                while (!Thread.currentThread().isInterrupted()) {
                    if (timer.getGlobalTimeMillis() - lastUpdate > GAME_SEND_TIMEOUT) {
                        lastUpdate = timer.getGlobalTimeMillis();

                        if (game.getPlayerCount() != 0) {
                            Message gameMessage = new Message(MessageType.GAME_DATA_TICK, new PayloadGameTickData(game));
                            sendMessageToAll(gameMessage);
                            // all deletion info is sent, so we are now free to clear all marked game objects
                            game.deleteMarkedGameObjects();
                            if (gameSendTimes.get() % 100 == 0) {
                                System.out.println("Game sent " + gameSendTimes.get() + " times");
                                System.out.println("Server tps: " + timer.getTps());
                            }
                            gameSendTimes.incrementAndGet();
                        }
                    }
                    timer.tick();
                    game.tick(timer.getGlobalDeltaTimeMillis());
                }
            }, "Game updating and sending thread");
            gameSendingThread.start();

            while (!Thread.currentThread().isInterrupted()) {
                Connection client = new Connection(server);
                Thread clientThread = new Thread(() -> {
                    System.out.println("OnlinePlayer connected: " + client.getIp());
                    System.out.println("Waiting for data");
                    communicationLoop(client);
                }, client.getIp() + " connection");
                clientThread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void sendMessageToAll(Message message) {
        OnlinePlayer current = null;
        try {
            for (OnlinePlayer pl : connectedPlayers) {
                current = pl;
                pl.writeMessage(message);
            }
        } catch (IOException e) {
            disconnectPlayer(current);
        }
    }

    private static OnlinePlayer login(Connection unauthorized) throws RuntimeException {
        try {
            Message msg = unauthorized.readMessage();
            if (msg.type != MessageType.LOGIN_DATA) {
                unauthorized.writeMessage(Message.ErrorMessage("LOGIN NEEDED!"));
                System.out.println("OnlinePlayer: " + unauthorized.getIp() + " failed to log in: different message type: " + msg.type);
                return null;
            }

            PayloadLoginData loginData = (PayloadLoginData) msg.payload;
            String nickname = loginData.nickname;
            double x = loginData.x, y = loginData.y;
            long w = loginData.w, h = loginData.h;
            Color color = new Color(loginData.colorRGB);

            System.out.println("OnlinePlayer: " + unauthorized.getIp() + ", nick: " + nickname + ", coords: {" + x + ", " + y + ", " + w + ", " + h + "}; color: " + color + " connected!");
            OnlinePlayer player = new OnlinePlayer(unauthorized, nickname, Thread.currentThread());
            player.player = new Player(nickname, x, y, w, h, color);
            return player;
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            closeConnection(unauthorized);
            return null;
        }
    }

    private static synchronized void disconnectPlayer(OnlinePlayer player) {
        if (player == null)
            System.out.println("Client to disconnect: wrong data(player == null)!");
        else
            try {
                if (!game.containsPlayer(player.nickname)) {
                    //player has been disconnected from another thread
                    return;
                }

                System.out.println("Player " + player + " disconnected");

                game.remove(player.player);
                connectedPlayers.remove(player);

                player.close();

                player.playerThread.interrupt();
                // not possible, because disconnect may be from game thread, and it will interrupt game sending process
                // Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("FAILED TO DISCONNECT PLAYER: " + player);
            }
    }

    private static void closeConnection(Connection connection) {
        if (connection == null) {
            System.out.println("Failed to close null collection!");
        } else {
            try {
                System.out.println("Connection: " + connection + " closed!");
                connection.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void communicationLoop(Connection client) {
        /* Login and construct a player */
        OnlinePlayer player = login(client);
        if (player == null)
            return;

        connectedPlayers.add(player);
        game.add(player.player);

        //send full game to player
        Message fullGameMessage = new Message(MessageType.GAME_DATA_FULL, new PayloadGameFullData(game));
        sendMessageToAll(fullGameMessage);

        /* Request client data each REQUEST_TIMEOUT ms, otherwise predict */
        new Thread(() -> {
            try {
                final long REQUEST_TIMEOUT = 10;
                double lastUpdate = 0;
                while (!player.playerThread.isInterrupted()) {
                    if (timer.getGlobalTimeMillis() - lastUpdate > REQUEST_TIMEOUT) {
                        lastUpdate = timer.getGlobalTimeMillis();
                        Message msg = player.readMessage();
                        handleMessage(msg, player);
                    }
                }
            } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                disconnectPlayer(player);
            }
        }).start();
    }

    private static void handleMessage(Message msg, OnlinePlayer player) throws IOException {
        switch (msg.type) {
            case INVALID -> {
                player.writeMessage(Message.ErrorMessage("INVALID MESSAGE SENT!"));
                System.out.println("OnlinePlayer: " + player.nickname + " sent invalid message with payload: " + msg.payload);
            }
            case ERROR -> {
                throw new RuntimeException(((PayloadStringData) msg.payload).str);
            }
            case INFO -> {
                String info = ((PayloadStringData) msg.payload).str;
                if (info.equals("gamedump")) {
                    System.out.println("game = " + game);
                    System.out.println("Total players: " + game.getPlayerCount());
                    System.out.println("Total game objects: " + game.getGameObjectCount());
                }
                System.out.println("Received info from player: " + player.nickname + ": " + info);
            }
            case LOGIN_DATA -> {
                player.writeMessage(Message.ErrorMessage("ALREADY LOGGED IN!"));
                System.out.println("OnlinePlayer: " + player.nickname + " sent login data while being logged in!");
            }
            case SPEED_XY -> {
                PayloadSpeedXY speedXY = (PayloadSpeedXY) msg.payload;
                player.player.setSpeedX(speedXY.speedX);
                player.player.setSpeedY(speedXY.speedY);
                if (player.player.getSpeedX() == 0 && player.player.getSpeedY() == 0) {
                    if (player.moves) {
                        player.moves = false;
                        System.out.println("Player " + player.nickname + " moved from {" + player.oldX + "; " + player.oldY + "} to " + "{" + player.player.x + "; " + player.player.y + "}");
                        player.oldX = player.player.x;
                        player.oldY = player.player.y;
                    }
                } else {
                    if (!player.moves) {
                        player.moves = true;
                        player.oldX = player.player.x;
                        player.oldY = player.player.y;
                        System.out.println("Player " + player.nickname + " moves");
                    }
                }
            }
            case GAME_DATA_TICK -> {
                player.writeMessage(Message.ErrorMessage("GAME DATA SENT!"));
                System.out.println("OnlinePlayer: " + player.nickname + " sent game data!");
            }
            default -> throw new IllegalStateException("Player: " + player + " sent unexpected data: " + msg.type);
        }
    }
}