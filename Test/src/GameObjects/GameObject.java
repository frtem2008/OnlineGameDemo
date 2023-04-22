package GameObjects;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GameObject implements Externalizable, Cloneable {
    public double x, y;
    public double w, h;
    public Rectangle hitbox;

    private String uuid;
    public GameObjectType type;

    public final String getUUID() {
        return uuid;
    }

    public boolean markedForDelete = false;

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
        out.writeUTF(uuid);
        out.writeDouble(x);
        out.writeDouble(y);
        out.writeDouble(w);
        out.writeDouble(h);
        out.writeLong(hitbox.x);
        out.writeLong(hitbox.y);
        out.writeLong(hitbox.width);
        out.writeLong(hitbox.height);
        out.writeBoolean(markedForDelete);
    }

    public boolean differsFrom(GameObject gameObject) {
        return this.type != gameObject.type ||
                this.x != gameObject.x ||
                this.y != gameObject.y ||
                this.w != gameObject.w ||
                this.h != gameObject.h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameObject that = (GameObject) o;

        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        if (Double.compare(that.w, w) != 0) return false;
        if (Double.compare(that.h, h) != 0) return false;
        if (!Objects.equals(hitbox, that.hitbox)) return false;
        if (!Objects.equals(uuid, that.uuid)) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(w);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(h);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (hitbox != null ? hitbox.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        uuid = in.readUTF();
        x = in.readDouble();
        y = in.readDouble();
        w = in.readDouble();
        h = in.readDouble();
        hitbox = new Rectangle((int) in.readLong(), (int) in.readLong(), (int) in.readLong(), (int) in.readLong());
        markedForDelete = in.readBoolean();
    }

    @Override
    public GameObject clone() {
        try {
            GameObject clone = (GameObject) super.clone();
            clone.x = x;
            clone.y = y;
            clone.w = w;
            clone.h = h;
            clone.hitbox = (Rectangle) hitbox.clone();
            clone.type = type;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
