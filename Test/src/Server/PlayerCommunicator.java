package Server;

import GameObjects.Player;
import Online.Connection;
import Online.Message;
import Online.MessagePayloadObjects.PayloadStringData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameFullData;
import Online.MessageType;
import Online.OnlinePlayer;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Server.Server.disconnectPlayer;
import static Server.Server.sendMessageToAll;

public class PlayerCommunicator implements Runnable {
    private final Connection client;
    private OnlinePlayer player;

    public PlayerCommunicator(Connection client) {
        this.client = client;
        new Thread(this, "Client " + client.getIp() + " handling thread").start();
    }

    private OnlinePlayer login() throws RuntimeException {
        try {
            Message msg = client.readMessage();
            if (msg.type != MessageType.LOGIN_DATA) {
                client.writeMessage(Message.ErrorMessage("LOGIN NEEDED!"));
                Logger.getLogger("Player login").log(Level.INFO, "OnlinePlayer: " + client.getIp() + " failed to log in: different message type: " + msg.type);
                return null;
            }

            PayloadLoginData loginData = (PayloadLoginData) msg.payload;
            String nickname = loginData.nickname;
            double x = loginData.x, y = loginData.y;
            long w = loginData.w, h = loginData.h;
            Color color = new Color(loginData.colorRGB);

            Logger.getLogger("Player login").log(Level.INFO, "OnlinePlayer: " + client.getIp() + ", nick: " + nickname + ", coords: {" + x + ", " + y + ", " + w + ", " + h + "}; color: " + color + " connected!");
            OnlinePlayer player = new OnlinePlayer(client, nickname, Thread.currentThread());
            player.player = new Player(nickname, x, y, w, h, color);
            return player;
        } catch (IOException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            closeConnection();
            return null;
        }
    }


    private void communicationLoop() {
        /* Login and construct a player */
        player = login();
        if (player == null)
            return;

        Server.getConnectedPlayers().add(player);
        Server.getGame().add(player.player);

        //send full game to player
        Message fullGameMessage = new Message(MessageType.GAME_DATA_FULL, new PayloadGameFullData(Server.getGame()));
        sendMessageToAll(fullGameMessage);

        /* Request client data each REQUEST_TIMEOUT ms, otherwise predict */
        new Thread(() -> {
            try {
                final long REQUEST_TIMEOUT = 10;
                double lastUpdate = 0;
                while (!player.playerThread.isInterrupted()) {
                    if (Server.getTimer().getGlobalTimeMillis() - lastUpdate > REQUEST_TIMEOUT) {
                        lastUpdate = Server.getTimer().getGlobalTimeMillis();
                        Message msg = player.readMessage();
                        handleMessage(msg);
                    }
                }
            } catch (IOException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                disconnectPlayer(player);
            }
        }, "Player: " + player.nickname + " message handling thread").start();
    }

    private void handleMessage(Message msg) throws IOException {
        switch (msg.type) {
            case INVALID -> {
                player.writeMessage(Message.ErrorMessage("INVALID MESSAGE SENT!"));
                Logger.getLogger("Message handler").log(Level.WARNING, "OnlinePlayer: " + player.nickname + " sent invalid message with payload: " + msg.payload);
            }
            case ERROR -> {
                throw new RuntimeException(((PayloadStringData) msg.payload).str);
            }
            case INFO -> {
                String info = ((PayloadStringData) msg.payload).str;
                if (info.equals("gamedump")) {
                    Logger.getLogger("Message handler").log(Level.INFO, "game = " + Server.getGame());
                    Logger.getLogger("Message handler").log(Level.INFO, "Total players: " + Server.getGame().getPlayerCount());
                    Logger.getLogger("Message handler").log(Level.INFO, "Total game objects: " + Server.getGame().getGameObjectCount());
                }
                Logger.getLogger("Message handler").log(Level.INFO, "Received info from player: " + player.nickname + ": " + info);
            }
            case LOGIN_DATA -> {
                player.writeMessage(Message.ErrorMessage("ALREADY LOGGED IN!"));
                Logger.getLogger("Message handler").log(Level.WARNING, "OnlinePlayer: " + player.nickname + " sent login data while being logged in!");
            }
            case SPEED_XY -> {
                PayloadSpeedXY speedXY = (PayloadSpeedXY) msg.payload;
                player.player.setSpeedX(speedXY.speedX);
                player.player.setSpeedY(speedXY.speedY);
                if (player.player.getSpeedX() == 0 && player.player.getSpeedY() == 0) {
                    if (player.moves) {
                        player.moves = false;
                        Logger.getLogger("Message handler").log(Level.INFO, "Player " + player.nickname + " moved from {" + player.oldX + "; " + player.oldY + "} to " + "{" + player.player.x + "; " + player.player.y + "}");
                        player.oldX = player.player.x;
                        player.oldY = player.player.y;
                    }
                } else {
                    if (!player.moves) {
                        player.moves = true;
                        player.oldX = player.player.x;
                        player.oldY = player.player.y;
                        Logger.getLogger("Message handler").log(Level.INFO, "Player " + player.nickname + " moves");
                    }
                }
            }
            case GAME_DATA_TICK -> {
                player.writeMessage(Message.ErrorMessage("GAME DATA SENT!"));
                Logger.getLogger("Message handler").log(Level.WARNING, "Player " + player.nickname + " sent game data!");
            }
            default -> throw new IllegalStateException("Player: " + player + " sent unexpected data: " + msg.type);
        }
    }

    private void closeConnection() {
        if (client == null) {
            Logger.getLogger("Message handler").log(Level.WARNING, "Failed to close null collection!");
        } else {
            try {
                Logger.getLogger("Message handler").log(Level.INFO, "Connection: " + client + " closed!");
                client.close();
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void run() {
        Logger.getLogger("Player communicator").log(Level.INFO, "OnlinePlayer connected: " + client.getIp());
        Logger.getLogger("Player communicator").log(Level.INFO, "Waiting for data");
        communicationLoop();
    }
}
