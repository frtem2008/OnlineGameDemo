package GameObjects;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

public class Player extends GameObject implements Externalizable {
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
        x = y = w = h = -1;
        name = null;
        color = null;
    }

    public Player(String name, double x, double y, long w, long h, Color color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
        this.hitbox = new Rectangle((int) x, (int) y, (int)w, (int)h);
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

    public void move(Collection<GameObject> gameObjects) {
//        System.out.println("Moving: sx:" + speedX + ", sy: " + speedY);
        x += speedX * 0.000001;
        y += speedY * 0.000001;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect((int) x, (int) y, (int)w, (int)h);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        assert name != null;
        out.writeUTF(name);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeLong(w);
        out.writeLong(h);
        out.writeLong(hitbox.x);
        out.writeLong(hitbox.y);
        out.writeLong(hitbox.width);
        out.writeLong(hitbox.height);
        out.writeLong(color.getRGB());
        out.writeDouble(speedX);
        out.writeDouble(speedY);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        name = in.readUTF();
        x = in.readDouble();
        y = in.readDouble();
        w = in.readLong();
        h = in.readLong();
        hitbox = new Rectangle((int) in.readLong(), (int) in.readLong(), (int) in.readLong(), (int) in.readLong());
        color = new Color((int) in.readLong());
        speedX = in.readDouble();
        speedY = in.readDouble();
    }
}
