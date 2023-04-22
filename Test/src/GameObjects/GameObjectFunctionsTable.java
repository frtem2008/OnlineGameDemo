package GameObjects;

import Online.ReadFunctions;

import java.util.HashMap;

import static Online.ReadFunctions.fromClass;

public class GameObjectFunctionsTable {
    public static final HashMap<Class<? extends GameObject>, ReadFunctions> gameObjectFunctionsMap = new HashMap<>();

    static {
        try {
            gameObjectFunctionsMap.put(Player.class, fromClass(Player.class));
            gameObjectFunctionsMap.put(Block.class, fromClass(Block.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
