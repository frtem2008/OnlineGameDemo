package gameObjects;
//стена (пока ничем не отличается от gameObject)

import Utils.GameObject;

import java.awt.*;

public class Wall extends GameObject {

    public Wall(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public Wall(double x, double y, double w, double h, Image texture) {
        super(x, y, w, h, texture);
    }
}
