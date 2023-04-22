package GameObjects;

import Online.ReadFunctions;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Externalizable {
    private ConcurrentHashMap<String, GameObject> gameObjects;
    private ConcurrentHashMap<String, Player> players;

    public Game(ConcurrentHashMap<String, Player> players) {
        this.gameObjects = new ConcurrentHashMap<>();
        this.players = players;
        gameObjects.putAll(players);
    }

    public Game() {
        this.players = new ConcurrentHashMap<>();
        this.gameObjects = new ConcurrentHashMap<>();
    }

    public void add(Player player) {
        players.put(player.name, player);
        gameObjects.put(player.getUUID(), player);
    }

    public void add(GameObject gameObject) {
        gameObjects.put(gameObject.getUUID(), gameObject);
    }

    public void remove(Player player) {
        gameObjects.remove(player.getUUID());
        players.remove(player.name);
    }

    public boolean containsPlayer(String nickname) {
        return players.containsKey(nickname);
    }

    public void removePlayer(String nickname) {
        gameObjects.remove(players.get(nickname).getUUID());
        players.remove(nickname);
    }

    public void remove(GameObject gameObject) {
        gameObjects.remove(gameObject.getUUID());
    }

    public void draw(Graphics g) {
        for (GameObject gameObject : gameObjects.values()) {
            gameObject.draw(g);
        }
    }

    public void tick(double deltaTime) {
        for (GameObject gameObject : gameObjects.values()) {
            if (gameObject.type == GameObjectType.UNDEFINED)
                throw new IllegalStateException("Added game object with udefined type to game: " + gameObject);
            gameObject.tick(deltaTime, gameObjects);
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameObjects=" + gameObjects.toString() + "\n" +
                "players=" + players.toString() + "\n" +
                '}';
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(gameObjects.size());
        for (GameObject gameObject : gameObjects.values()) {
            // Write game object type
            out.writeUTF(gameObject.type.toString());
            gameObject.writeExternal(out);
        }
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        gameObjects = new ConcurrentHashMap<>();
        long size = in.readLong();
        for (int i = 0; i < size; i++) {;
            String gameObjectType = in.readUTF();
            GameObjectType type = GameObjectType.valueOf(gameObjectType);

            ReadFunctions functions = GameObjectFunctionsTable.gameObjectFunctionsMap.get(type.gameObjectClass);
            Externalizable received;
            if (functions != null) {
                try {
                    received = functions.constructor().newInstance();
                    functions.readMethod().invoke(received, in);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new IllegalStateException("No read functions associated with class: " + gameObjectType);
            }
            if (received instanceof Player)
                players.put(((Player) received).name, (Player) received);
            if (received instanceof GameObject)
                gameObjects.put(((GameObject) received).getUUID(), (GameObject) received);
            else
                throw new IllegalStateException("Received not a game object: " + received);
        }
    }
}
