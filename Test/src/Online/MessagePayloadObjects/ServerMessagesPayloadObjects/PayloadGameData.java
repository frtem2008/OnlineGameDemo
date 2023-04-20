package Online.MessagePayloadObjects.ServerMessagesPayloadObjects;

import GameObjects.Game;
import Online.MessagePayloadObjects.MessagePayload;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class PayloadGameData extends MessagePayload {
    public Game game;
    /* Something else to send */

    public PayloadGameData() {
    }

    public PayloadGameData(Game game) {
        this.game = game;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        game.writeExternal(out);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        game = new Game();
        game.readExternal(in);
    }

    @Override
    public String toString() {
        return "PayloadGameData{" +
                "game=" + game +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadGameData that = (PayloadGameData) o;

        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return game != null ? game.hashCode() : 0;
    }
}