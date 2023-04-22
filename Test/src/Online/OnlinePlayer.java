package Online;

import GameObjects.Player;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class OnlinePlayer implements Closeable {
    private final Connection connection;
    public final Thread playerThread;
    public final String nickname;
    public Player player;
    public boolean moves = false;
    public double oldX = 0, oldY = 0;

    public OnlinePlayer(Connection connection) {
        this.playerThread = null;
        this.nickname = "Invalid player @" + Thread.currentThread().getName() + ")";
        this.connection = connection;
    }

    public OnlinePlayer(Connection connection, String nickname, Thread clientThread) {
        this.connection = connection;
        this.nickname = nickname;
        this.playerThread = clientThread;
    }

    public void sendSpeed() throws IOException {
        Message msg = new Message(MessageType.SPEED_XY, new PayloadSpeedXY(player.getSpeedX(), player.getSpeedY()));
        connection.writeMessage(msg);
    }

    public void setSpeed(double speedX, double speedY) {
        player.setSpeedX(speedX);
        player.setSpeedY(speedY);
    }

    public void writeMessage(Message msg) throws IOException {
        connection.writeMessage(msg);
    }

    public Message readMessage() throws IOException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return connection.readMessage();
    }


    @Override
    public void close() throws IOException {
        if (playerThread != null)
            playerThread.interrupt();
        connection.close();
    }

    @Override
    public String toString() {
        return "OnlinePlayer{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
