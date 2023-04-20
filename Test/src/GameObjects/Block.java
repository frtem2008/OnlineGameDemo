package GameObjects;

import java.awt.*;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Block implements Serializable {
    @Override
    public String toString() {
        return "Block{" +
                "hitbox=" + hitbox +
                ", id=" + id +
                '}';
    }

    public static AtomicInteger curId = new AtomicInteger(0);
    public Rectangle hitbox;
    public int id;

    public Block(Rectangle hitbox) {
        this.hitbox = hitbox;
        this.id = curId.incrementAndGet();
    }
}
