package Online.MessagePayloadObjects.PlayerMessagesPayloadObjects;

import Online.MessagePayloadObjects.MessagePayload;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class PayloadSpeedXY extends MessagePayload {
    public double speedX, speedY;

    public PayloadSpeedXY() {
        speedX = speedY = 0;
    }

    public PayloadSpeedXY(double speedX, double speedY) {
        this.speedX = speedX;
        this.speedY = speedY;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(speedX);
        out.writeDouble(speedY);
        out.flush();
    }

    @Override
    public String toString() {
        return "PayloadSpeedXY{" +
                "speedX=" + speedX +
                ", speedY=" + speedY +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadSpeedXY that = (PayloadSpeedXY) o;

        if (Double.compare(that.speedX, speedX) != 0) return false;
        return Double.compare(that.speedY, speedY) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(speedX);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(speedY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        speedX = in.readDouble();
        speedY = in.readDouble();
    }
}
