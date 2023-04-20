package GameObjects;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Game implements Externalizable {
    public HashMap<String, Player> players;

    public Game(HashMap<String, Player> players) {
        this.players = players;
    }

    public Game() {
        players = new HashMap<>();
    }

    public void add(Player player) {
        players.put(player.name, player);
    }

    public void remove(Player player) {
        players.remove(player.name);
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
        for (Player pl: players.values()) {
            pl.writeExternal(out);
        }
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        players = new HashMap<>();
        long size = in.readLong();
        for (int i = 0; i < size; i++) {
            Player player = new Player();
            player.readExternal(in);
            players.put(player.name, player);
        }
    }
}
