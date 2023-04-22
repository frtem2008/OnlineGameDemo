package GameObjects;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;

public class Block extends GameObject {
    private Color color;

    public Block() {
        super();
        this.type = GameObjectType.BLOCK;
    }

    public Block(double x, double y, double w, double h, Color color) {
        super(x, y, w, h);
        this.type = GameObjectType.BLOCK;
        this.color = color;
    }


    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect((int) x, (int) y, (int) w, (int) h);
    }

    @Override
    public void tick(double deltaTime, ConcurrentHashMap<String, GameObject> gameObjects) {
        // do nothing
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(color.getRGB());
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        color = new Color((int) in.readLong());
    }
}
