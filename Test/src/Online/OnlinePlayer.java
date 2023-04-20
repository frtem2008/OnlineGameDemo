package Online;

import GameObjects.Player;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class OnlinePlayer implements AutoCloseable {
    private final Connection connection;
    public final Thread clientThread;
    public final String nickname;
    public Player player;
    public boolean moves = false;
    public double oldX = 0, oldY = 0;

    public OnlinePlayer(Connection connection) {
        this.clientThread = null;
        this.nickname = "Invalid player @" + Thread.currentThread().getName() + ")";
        this.connection = connection;
    }

    public OnlinePlayer(Connection connection, String nickname, Thread clientThread) {
        this.connection = connection;
        this.nickname = nickname;
        this.clientThread = clientThread;
    }

    public void move() {
        player.move(null);
    }

    public void sendSpeed() throws IOException {
        Message msg = new Message(MessageType.SPEED_XY, new PayloadSpeedXY(player.getSpeedX(), player.getSpeedY()));
        connection.writeMessage(msg);
    }

    public void setSpeed(double speedX, double speedY) throws IOException {
        player.setSpeedX(speedX);
        player.setSpeedY(speedY);
    }

    public void writeMessage(Message msg) throws IOException {
        connection.writeMessage(msg);
    }

    public Message readMessage() throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        return connection.readMessage();
    }


    @Override
    public void close() throws IOException {
        if (clientThread != null)
            clientThread.interrupt();
        connection.close();
    }
}