package GameObjects;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Block extends GameObject {
    private Color color;

    public Block() {
        super();
        this.type = GameObjectType.BLOCK;
    }

    @Override
    public boolean differsFrom(GameObject gameObject) {
        return super.differsFrom(gameObject) || !Objects.equals(this.color, ((Block) gameObject).color);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Block block = (Block) o;

        return Objects.equals(color, block.color);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }

}
