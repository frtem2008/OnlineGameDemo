package Server;

import Online.Message;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameTickData;
import Online.MessageType;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Server.Server.sendMessageToAll;

public class GameUpdaterAndSender implements Runnable {

    public GameUpdaterAndSender() {
        new Thread(this, "Game updating and sending thread").start();
    }

    @Override
    public void run() {
        double lastUpdate = 0;
        final long GAME_SEND_TIMEOUT = 30;
        AtomicInteger gameSendTimes = new AtomicInteger(0);

        while (!Thread.currentThread().isInterrupted()) {
            if (Server.getTimer().getGlobalTimeMillis() - lastUpdate > GAME_SEND_TIMEOUT) {
                lastUpdate = Server.getTimer().getGlobalTimeMillis();

                if (Server.getGame().getPlayerCount() != 0) {
                    Message gameMessage = new Message(MessageType.GAME_DATA_TICK, new PayloadGameTickData(Server.getGame()));
                    sendMessageToAll(gameMessage);
                    // all deletion info is sent, so we are now free to clear all marked game objects
                    Server.getGame().deleteMarkedGameObjects();
                    if (gameSendTimes.get() % 100 == 0) {
                        Logger.getLogger("Game sender").log(Level.INFO, "Game sent " + gameSendTimes.get() + " times");
                        Logger.getLogger("Game updater").log(Level.INFO,"Server tps: " + Server.getTimer().getTps());
                    }
                    gameSendTimes.incrementAndGet();
                }
            }
            Server.getTimer().tick();
            Server.getGame().tick(Server.getTimer().getGlobalDeltaTimeMillis());
        }
    }
}