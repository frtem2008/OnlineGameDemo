package gameObjects;
//класс для хранения игры

import Main.Main;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    //данные
    @Serial
    private static final long serialVersionUID = 1L;

    public ArrayList<Wall> walls;
    public ArrayList<Player> players;
    public ArrayList<Bot> bots;
    public ArrayList<Bullet> bullets;

    //инициализация в конструкторе
    public Game() {
        walls = new ArrayList<>();
        players = new ArrayList<>();
        bots = new ArrayList<>();
        bullets = new ArrayList<>();
    }

    /**
     * игровой тик
     *
     * @param frames нужен, чтобы двигать ботов раз в несколько кадров
     *               боты пока не двигаются
     */
    public void tick(double frames) {
        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).move();
        }
        for (int i = 0; i < players.size(); i++) {
            players.get(i).move(this);
        }
        for (int i = 0; i < bots.size(); i++) {
            if (frames % 10 == 0) {
                bots.get(i).moveBot(Main.botMap, Main.mainPlayerX, Main.mainPlayerY, (int) bots.get(i).cords.x, (int) bots.get(i).cords.y);
            }
            //bots.get(i).moveBot();
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "walls=" + walls.toString() +
                ", players=" + players.toString() +
                ", bots=" + bots.toString() +
                ", bullets=" + bullets.toString() +
                '}';
    }
}
