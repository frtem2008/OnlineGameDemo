package Online.MessagePayloadObjects.PlayerMessagesPayloadObjects;

import Online.MessagePayloadObjects.MessagePayload;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class PayloadLoginData extends MessagePayload {
    public String nickname;
    public double x, y;
    public long w, h;
    public int colorRGB;

    public PayloadLoginData() {
        nickname = null;
        x = y = w = h = colorRGB = 0;
    }

    public PayloadLoginData(String nickname, double x, double y, int w, int h, Color color) {
        this.nickname = nickname;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.colorRGB = color.getRGB();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(nickname);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeLong(w);
        out.writeLong(h);
        out.writeLong(colorRGB);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        nickname = in.readUTF();
        x = in.readDouble();
        y = in.readDouble();
        w = in.readLong();
        h = in.readLong();
        colorRGB = (int) in.readLong();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadLoginData loginData = (PayloadLoginData) o;

        if (Double.compare(loginData.x, x) != 0) return false;
        if (Double.compare(loginData.y, y) != 0) return false;
        if (w != loginData.w) return false;
        if (h != loginData.h) return false;
        if (colorRGB != loginData.colorRGB) return false;
        return Objects.equals(nickname, loginData.nickname);
    }

    @Override
    public int hashCode() {
        long result;
        long temp;
        result = nickname != null ? nickname.hashCode() : 0;
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + w;
        result = 31 * result + h;
        result = 31 * result + colorRGB;
        return (int) result;
    }
}
