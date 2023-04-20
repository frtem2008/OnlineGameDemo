package GameObjects;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Externalizable {
    private ConcurrentHashMap<String, Player> players;

    public ConcurrentHashMap<String, Player> getPlayers() {
        return players;
    }

    public Game(ConcurrentHashMap<String, Player> players) {
        this.players = players;
    }

    public Game() {
        players = new ConcurrentHashMap<>();
    }

    public void add(Player player) {
        players.put(player.name, player);
    }

    public void remove(Player player) {
        players.remove(player.name);
    }


    public void draw(Graphics g) {
        for (Player pl : players.values()) {
            pl.draw(g);
        }
    }

    public void tick(double deltaTime) {
        for (Player pl : players.values()) {
            pl.move(deltaTime, null);
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "toDraw=" + players.toString() +
                '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(players.size());
        for (Player pl : players.values()) {
            pl.writeExternal(out);
        }
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        players = new ConcurrentHashMap<>();
        long size = in.readLong();
        for (int i = 0; i < size; i++) {
            Player player = new Player();
            player.readExternal(in);
            players.put(player.name, player);
        }
    }
}
