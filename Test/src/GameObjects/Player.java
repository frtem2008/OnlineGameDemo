package GameObjects;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
}
