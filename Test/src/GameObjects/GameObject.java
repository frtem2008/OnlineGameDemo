package GameObjects;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameObject implements Externalizable {
    public double x, y;
    public double w, h;
    public Rectangle hitbox;

    private final String uuid;
    public GameObjectType type;

    public final String getUUID() {
        return uuid;
    }

    public boolean updatedLastTick = true;

    public void draw(Graphics g) {
        g.setColor(Color.ORANGE);
        g.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    //perform every tick update
    public abstract void tick(double deltaTime, ConcurrentHashMap<String, GameObject> gameObjects);

    public GameObject() {
        // the value of uuid will be something like '03c9a439-fba6-41e1-a18a-4c542c12e6a8'
        this.uuid = java.util.UUID.randomUUID().toString();
        this.type = GameObjectType.UNDEFINED;
    }

    public GameObject(double x, double y, double w, double h) {
        this.uuid = java.util.UUID.randomUUID().toString();
        this.type = GameObjectType.UNDEFINED;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.hitbox = new Rectangle((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(w);
        out.writeDouble(h);
        out.writeLong(hitbox.x);
        out.writeLong(hitbox.y);
        out.writeLong(hitbox.width);
        out.writeLong(hitbox.height);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readDouble();
        y = in.readDouble();
        w = in.readDouble();
        h = in.readDouble();
        hitbox = new Rectangle((int) in.readLong(), (int) in.readLong(), (int) in.readLong(), (int) in.readLong());
    }
}
