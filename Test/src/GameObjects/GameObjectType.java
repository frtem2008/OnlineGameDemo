package GameObjects;

public enum GameObjectType {
    // weapons, explodes, textures, so on
    UNDEFINED(null),
    BLOCK(Block.class),    // block data
    PLAYER(Player.class);  // player data

    final Class<? extends GameObject> gameObjectClass;

    GameObjectType(Class<? extends GameObject> gameObject) {
        this.gameObjectClass = gameObject;
    }
}
