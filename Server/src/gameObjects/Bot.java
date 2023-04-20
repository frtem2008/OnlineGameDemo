package gameObjects;

import Main.Main;
import Utils.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Bot extends Player {

    public Vector2D speed = new Vector2D(1d, 1d); // скорость игрока по x / y в клетках
    public Vector2D newPoint = new Vector2D(0, 0);

    public Bot(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    public Bot(double x, double y, double w, double h, Image texture) {
        super(x, y, w, h, texture);
    }

    //TODO перемещение бота В РАЗРАБОТКЕ
    /*public void moveBot() {
        System.out.println(newPoint.toString());
        if (!cords.equals(newPoint)) {
            cords.x += newPoint.x / speed.x;
            cords.y += newPoint.y / speed.y;
        }
    }*/


    //TODO расчет следующей точки и плавный путь к ней
    public void moveBot(boolean[][] map1, int playerX, int playerY, int botX, int botY) {
        //TODO как то пофиксить ботов, проходящих друг через друга
        boolean[][] map = Arrays.copyOf(map1, map1.length);
        map[botY / Main.cellSize][botX / Main.cellSize] = false;

        Vector2D from, to;
        from = new Vector2D(playerY / Main.cellSize, playerX / Main.cellSize);
        to = new Vector2D(cords.y / Main.cellSize, cords.x / Main.cellSize);

        ArrayList<Vector2D> path;

        path = Utils.LeeAlgorithm.findPath(map, to, from);
        if (path != null) {
            for (Vector2D ij : path) {
                if (ij == null) {
                    newPoint.x = 0;
                    newPoint.y = 0;
                    //break;
                    return;
                }
            }

            if (path.size() > 2) {
                if (path.get(0).y < path.get(1).y) {
                    cords.x += Main.cellSize;
                    newPoint.x = 1;
                    newPoint.y = 0;
                } else if (path.get(0).y > path.get(1).y) {
                    cords.x -= Main.cellSize;
                    newPoint.x = -1;
                    newPoint.y = 0;
                }
                if (path.get(0).x > path.get(1).x) {
                    cords.y -= Main.cellSize;
                    newPoint.x = 0;
                    newPoint.y = -1;
                } else if (path.get(0).x < path.get(1).x) {
                    cords.y += Main.cellSize;
                    newPoint.x = 0;
                    newPoint.y = 1;
                }
            }
        } else {
            newPoint.x = 0;
            newPoint.y = 0;
        }
    }
}
