package Online;

import Online.MessagePayloadObjects.PayloadTable;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.util.Objects;

public class Connection implements Closeable {
    private final Socket socket;
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

    public void writeMessage(Message msg) throws IOException {
        if (!closed) {
            String header = msg.type.toString();
            writer.writeUTF(header);
            writer.flush();
            msg.payload.writeExternal(writer);
            writer.flush();
        } else {
            close();
            throw new SocketException("Write failed: connection closed");
        }
    }

    public Message readMessage() throws IOException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (!closed) {
            Message msg = new Message();
            String messageHeader = reader.readUTF();
            msg.type = MessageType.valueOf(messageHeader);
            ReadFunctions functions = PayloadTable.payloadFunctionsMap.get(msg.type.payload);
            if (functions != null) {
                msg.payload = functions.constructor().newInstance();
                functions.readMethod().invoke(msg.payload, reader);
            } else {
                throw new IllegalStateException("No payload functions associated with class: " + msg.type.payload);
            }
            return msg;
        } else {
            close();
            throw new SocketException("Write failed: connection closed");
        }
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
            if (!socket.isClosed()) {
                writer.close();
                socket.close();
            }
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
