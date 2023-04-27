package GameObjects;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Player extends GameObject {
    private double speedX, speedY;
    public Color color;
    public String name;

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }


    public Player() {
        super();
        type = GameObjectType.PLAYER;
        x = y = w = h = -1;
        name = null;
        color = null;
    }

    public Player(String name, double x, double y, long w, long h, Color color) {
        super(x, y, w, h);
        this.type = GameObjectType.PLAYER;
        this.name = name;
        this.color = color;
    }

    @Override
    public String toString() {
        return "Player{" +
                "name=" + name +
                "speedX=" + speedX +
                ", speedY=" + speedY +
                ", color=" + color +
                ", x=" + x +
                ", y=" + y +
                ", w=" + w +
                ", h=" + h +
                '}';
    }


    @Override
    public void tick(double deltaTime, ConcurrentHashMap<String, GameObject> gameObjects) {
        x += speedX * deltaTime;
        y += speedY * deltaTime;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        assert name != null;
        out.writeUTF(name);
        super.writeExternal(out);
        out.writeLong(color.getRGB());
        out.writeDouble(speedX);
        out.writeDouble(speedY);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name = in.readUTF();
        super.readExternal(in);
        color = new Color((int) in.readLong());
        speedX = in.readDouble();
        speedY = in.readDouble();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Player player = (Player) o;

        if (Double.compare(player.speedX, speedX) != 0) return false;
        if (Double.compare(player.speedY, speedY) != 0) return false;
        if (!Objects.equals(color, player.color)) return false;
        return Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(speedX);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(speedY);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
