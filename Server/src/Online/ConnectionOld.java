package Online;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.Objects;


public class ConnectionOld implements Closeable {
    private final Socket socket;

    private final DataInputStream reader;

    private final DataOutputStream writer;

    public boolean closed;

    public ConnectionOld(String ip, int port) {
        try {
            this.socket = new Socket(ip, port);
            this.reader = createReader();
            this.writer = createWriter();
            this.closed = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ConnectionOld(ServerSocket server) {
        try {
            this.socket = server.accept();
            this.reader = createReader();
            this.writer = createWriter();
            this.closed = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private DataInputStream createReader() throws IOException {
        return new DataInputStream(socket.getInputStream());
    }

    private DataOutputStream createWriter() throws IOException {
        return new DataOutputStream(socket.getOutputStream());
    }

    public String getIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "Unable to get ip";
    }


    public void writeLine(String msg) throws IOException {
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

    public String readLine() throws IOException {
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

    @Override
    public String toString() {
        return "ConnectionOld{" + "ip=" + getIp() + '}';
    }


    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            writer.close();
            reader.close();
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
        ConnectionOld cur = (ConnectionOld) x;
        return cur.socket == this.socket && cur.getIp().equals(this.getIp());
    }
}
