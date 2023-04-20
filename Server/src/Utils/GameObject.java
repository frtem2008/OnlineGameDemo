package Utils;
//все игровые объекты имеют общие характеристики

import java.awt.*;
import java.io.Serial;
import java.io.Serializable;

public abstract class GameObject implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public Vector2D cords = new Vector2D(0, 0), size = new Vector2D(0, 0);
    public Image texture;

    public Rectangle hitbox;

    public GameObject(double x, double y, double w, double h) {
        this.cords.x = x;
        this.cords.y = y;
        this.size.x = w;
        this.size.y = h;
        this.hitbox = new Rectangle((int) x, (int) y, (int) w, (int) h);
    }

    public GameObject(double x, double y, double w, double h, Image texture) {
        this.cords.x = x;
        this.cords.y = y;
        this.size.x = w;
        this.size.y = h;
        this.hitbox = new Rectangle((int) x, (int) y, (int) w, (int) h);
        this.texture = texture;
    }

    public void drawHitbox(Graphics g, int xLayout, int yLayout, Color hitboxColor) {
        g.setColor(hitboxColor);
        g.drawRect((int) cords.x + xLayout, (int) cords.y + yLayout, (int) size.x, (int) size.y);
    }

    public void draw(Graphics g, int xLayout, int yLayout) {
        g.drawImage(texture, (int) cords.x + xLayout, (int) cords.y + yLayout, (int) size.x, (int) size.y, null);
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "cords=" + cords +
                ", size=" + size +
                ", texture=" + texture +
                '}';
    }
}
