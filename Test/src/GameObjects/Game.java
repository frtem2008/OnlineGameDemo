package GameObjects;

import Online.ReadFunctions;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game implements Externalizable {
    private final CopyOnWriteArrayList<GameObject> allGameObjects;
    private final ConcurrentHashMap<String, GameObject> gameObjects;
    private final ConcurrentHashMap<String, GameObject> gameObjectsUpdatedLastTick;
    private final ConcurrentHashMap<String, GameObject> gameObjectsForcedToUpdate;
    private final ConcurrentHashMap<String, GameObject> gameObjectsToDelete;
    private final ConcurrentHashMap<String, Player> players;

    public Game() {
        System.out.println("Game created");
        this.allGameObjects = new CopyOnWriteArrayList<>();
        this.players = new ConcurrentHashMap<>();
        this.gameObjects = new ConcurrentHashMap<>();
        this.gameObjectsUpdatedLastTick = new ConcurrentHashMap<>();
        this.gameObjectsToDelete = new ConcurrentHashMap<>();
        this.gameObjectsForcedToUpdate = new ConcurrentHashMap<>();
    }

    public void deleteMarkedGameObjects() {
        for (GameObject toDelete : gameObjectsToDelete.values()) {
            deleteGameObject(toDelete);
        }
        gameObjectsToDelete.clear();
    }

    public void add(GameObject gameObject) {
        gameObjects.put(gameObject.getUUID(), gameObject);
        allGameObjects.add(gameObject);

        if (gameObject instanceof Player)
            players.put(((Player) gameObject).name, (Player) gameObject);
    }

    public boolean containsPlayer(String nickname) {
        return players.containsKey(nickname);
    }

    public void remove(GameObject gameObject) {
        gameObject.markedForDelete = true;
        gameObjectsToDelete.put(gameObject.getUUID(), gameObject);
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public void tick(double deltaTime) {
        for (int i = 0; i < allGameObjects.size(); i++) {
            GameObject gameObject = allGameObjects.get(i);
            if (gameObject.type == GameObjectType.UNDEFINED)
                throw new IllegalStateException("Added game object with undefined type to game: " + gameObject);

            int oldHash = gameObject.hashCode();
            gameObject.tick(deltaTime, gameObjects);

            if (oldHash != gameObject.hashCode())
                gameObjectsUpdatedLastTick.put(gameObject.getUUID(), gameObject);
            else
                gameObjectsUpdatedLastTick.remove(gameObject.getUUID());
        }
    }

    private void deleteGameObject(GameObject toDelete) {
        toDelete.markedForDelete = true;
        gameObjects.remove(toDelete.getUUID());
        allGameObjects.remove(toDelete);
        if (toDelete instanceof Player)
            players.remove(((Player) toDelete).name);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public int getGameObjectCount() {
        return gameObjects.size();
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameObjects=" + gameObjects + "\n" +
                "players=" + players + "\n" +
                '}';
    }

    private void sendObjectMap(ConcurrentHashMap<String, ? extends GameObject> gameObjects, ObjectOutput out) throws IOException {
        for (GameObject gameObject : gameObjects.values()) {
            out.writeUTF(gameObject.type.toString());
            gameObject.writeExternal(out);
        }
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(gameObjectsUpdatedLastTick.size() + gameObjectsForcedToUpdate.size() + gameObjectsToDelete.size());
        sendObjectMap(gameObjectsUpdatedLastTick, out);
        sendObjectMap(gameObjectsForcedToUpdate, out);
        sendObjectMap(gameObjectsToDelete, out);

        out.flush();
    }

    public void writeFully(ObjectOutput out) throws IOException {
        out.writeLong(gameObjects.size());
        sendObjectMap(gameObjects, out);
    }

    private Externalizable readGameObject(ObjectInput in) throws IOException {
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
        return received;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        gameObjects.clear();
        long size = in.readLong();
        for (int i = 0; i < size; i++) {
            Externalizable received = readGameObject(in);
            if (received instanceof Player)
                players.put(((Player) received).name, (Player) received);
            if (received instanceof GameObject) {
                gameObjects.put(((GameObject) received).getUUID(), (GameObject) received);
            } else
                throw new IllegalStateException("Received not a game object: " + received);
        }
    }
}
