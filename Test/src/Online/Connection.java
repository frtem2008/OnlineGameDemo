package Online;

import GameObjects.Game;
import Online.MessagePayloadObjects.MessagePayload;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Objects;


public class Connection implements AutoCloseable {
    private final Socket socket;
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    private final ObjectInputStream reader;
    private final ObjectOutputStream writer;
    public boolean closed;

    public Connection(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.writer = createWriter();
            this.reader = createReader();
            this.closed = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection(ServerSocket server) {
        try {
            this.socket = server.accept();
            this.writer = createWriter();
            this.reader = createReader();
            this.closed = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectInputStream createReader() throws IOException {
        return new ObjectInputStream(socket.getInputStream());
    }

    private ObjectOutputStream createWriter() throws IOException {
        ObjectOutputStream res = new ObjectOutputStream(socket.getOutputStream());
        res.flush();
        return res;
    }

    public String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "Unable to get ip";
    }

    /*public void writeLine(String msg) throws IOException {
        if (!closed) {
            writer.writeUTF(msg);
            writer.flush();
        } else throw new SocketException("Write failed: connection closed");
    }

    public void writeLong(Long l) throws IOException {
        if (!closed) {
            writer.writeLong(l);
            writer.flush();
        } else throw new SocketException("Write failed: connection closed");
    }

    public void writeBytes(byte[] bytes, int offset, int len) throws IOException {
        if (!closed) {
            writer.write(bytes, offset, len);
            writer.flush();
        } else throw new SocketException("Write failed: connection closed");
    }

    public void writeObject(Serializable obj) throws IOException {
        if (!closed) {
            writer.writeObject(obj);
            writer.flush();
        } else throw new SocketException("Write failed: connection closed");
    }

    public void writeObject(Externalizable obj) throws IOException {
        if (!closed) {
            obj.writeExternal(writer);
            writer.flush();
        } else throw new SocketException("Write failed: connection closed");
    }
*/
    public void writeMessage(Message msg) throws IOException {
        if (!closed) {
            String header = msg.type.toString();
            synchronized (writeLock) {
                writer.writeUTF(header);
                writer.flush();
                msg.payload.writeExternal(writer);
                writer.flush();
            }
        } else throw new SocketException("Write failed: connection closed");
    }

    /*public String readLine() throws IOException {
        if (!closed) return reader.readUTF();
        throw new SocketException("Read failed: connection closed");
    }

    public Long readLong() throws IOException {
        if (!closed) return reader.readLong();
        throw new SocketException("Read failed: connection closed");
    }

    public int readBytes(byte[] buf, int offset, int len) throws IOException {
        if (!closed) return reader.read(buf, offset, len);
        throw new SocketException("Read failed: connection closed");
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        if (!closed) {
            return reader.readObject();
        }
        throw new SocketException("Read failed: connection closed");
    }
*/
    public Message readMessage() throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!closed) {
            Message msg = new Message();
            synchronized (readLock) {
                String messageHeader = reader.readUTF();

            /*
            if (messageHeader.startsWith("MESSAGE:")) {
                msg.type = MessageType.valueOf(messageHeader.substring("MESSAGE:".length()));
            } else {
                throw new RuntimeException("Invalid message header: " + messageHeader);
            }
            */
                msg.type = MessageType.valueOf(messageHeader);

                Class<? extends MessagePayload> payloadClass = msg.type.payload;
                Method readMethod = payloadClass.getDeclaredMethod("readExternal", ObjectInput.class);

                msg.payload = payloadClass.getDeclaredConstructor().newInstance();
                readMethod.invoke(msg.payload, reader);
            }
            return msg;
        } else throw new SocketException("Write failed: connection closed");
    }

    public Game readGame() throws IOException {
        if (!closed) {
            Game game = new Game();
            game.readExternal(reader);
            return game;
        }
        throw new SocketException("Read failed: connection closed");
    }

    @Override
    public String toString() {
        return "Online.Connection{" + "ip=" + getIp() + '}';
    }


    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            reader.close();
            writer.close();
            socket.close();
        }
    }


    @Override
    public int hashCode() {
        return Objects.hash(socket, reader, writer, closed);
    }

    public boolean equals(Object x) {
        if (x == null || x.getClass() != this.getClass()) return false;
        if (x == this) return true;
        Connection cur = (Connection) x;
        return cur.socket == this.socket && cur.getIp().equals(this.getIp());
    }
}
