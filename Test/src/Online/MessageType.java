package Online;

import Online.MessagePayloadObjects.MessagePayload;
import Online.MessagePayloadObjects.PayloadInvalid;
import Online.MessagePayloadObjects.PayloadStringData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameData;

public enum MessageType {

    INVALID(PayloadInvalid.class), //for new non-created messages

    ERROR(PayloadStringData.class),       //reaction on invalid message
    INFO(PayloadStringData.class), //some information

    LOGIN_DATA(PayloadLoginData.class), //player login data (nick, etc)
    SPEED_XY(PayloadSpeedXY.class),   //player xy speed(for now: sx, sy)
    GAME_DATA(PayloadGameData.class);  //complete game data(for now, all players)

    final Class<? extends MessagePayload> payload;

    MessageType(Class<? extends MessagePayload> payload) {
        this.payload = payload;
    }
}
