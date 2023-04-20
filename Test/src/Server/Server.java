package Server;

import GameObjects.Game;
import GameObjects.Player;
import Online.Connection;
import Online.Message;
import Online.MessagePayloadObjects.PayloadStringData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameData;
import Online.MessageType;
import Online.OnlinePlayer;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Set<OnlinePlayer> connectedPlayers = ConcurrentHashMap.newKeySet();
    private static Game game;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(26780)) {
            System.out.println("Server started on port " + server.getLocalPort());
            System.out.println("Waiting for clients to connect");

            game = new Game();
            new Thread(() -> {
                long lastUpdate = 0;
                final long GAME_SEND_TIMEOUT = 30;
                int gameSentTimes = 0;
                try {
                    while (true) {
                        if (System.currentTimeMillis() - lastUpdate > GAME_SEND_TIMEOUT) {
                            lastUpdate = System.currentTimeMillis();
                            Message gameMessage = new Message(MessageType.GAME_DATA, new PayloadGameData(game));
                            // TODO: 19.04.2023 Normal client thread interruption!
                            for (OnlinePlayer pl : connectedPlayers) {
                                pl.writeMessage(gameMessage);
                            }
                            if (gameSentTimes++ % 100 == 0)
                                System.out.println("Game sent " + gameSentTimes + " times");
                        } else {
                            for (OnlinePlayer pl : connectedPlayers) {
                                if (pl.moves)
                                    pl.move();
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            while (true) {
                Connection client = new Connection(server);

                new Thread(() -> {
                    System.out.println("OnlinePlayer connected: " + client.getIp());
                    System.out.println("Waiting for data");
                    try {
                        communicationLoop(client);
                    } catch (InterruptedException e) {
                        /* Disconnect a player */
                        Thread.currentThread().interrupt();
                    } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                             InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }

                }).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static OnlinePlayer login(Connection unauthorized) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
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
    }

    private static void communicationLoop(Connection client) throws InterruptedException, IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        final long REQUEST_TIMEOUT = 10;
        OnlinePlayer player;

        /* Login and construct a player */
        player = login(client);
        if (player == null)
            return;

        connectedPlayers.add(player);
        game.add(player.player);

        /* Request client data each REQUEST_TIMEOUT ms, otherwise predict */
        new Thread(() -> {
            try {
                long lastUpdate = 0;
                while (true) {
                    if (System.currentTimeMillis() - lastUpdate > REQUEST_TIMEOUT) {
                        /* read client data */
                        lastUpdate = System.currentTimeMillis();
                        Message msg = player.readMessage();
                        handleMessage(msg, player);
                    }
                }
            } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).start();
    //System.out.println("Moving: sx:" + player.player.getSpeedX() + "; sy: " + player.player.getSpeedY());
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
            case GAME_DATA -> {
                player.writeMessage(Message.ErrorMessage("GAME DATA SENT!"));
                System.out.println("OnlinePlayer: " + player.nickname + " sent game data!");
            }
        }
    }
}