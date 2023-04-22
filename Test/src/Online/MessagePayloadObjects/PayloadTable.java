package Online.MessagePayloadObjects;

import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameFullData;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameTickData;
import Online.ReadFunctions;

import java.util.HashMap;

import static Online.ReadFunctions.fromClass;

public class PayloadTable {
    public static final HashMap<Class<? extends MessagePayload>, ReadFunctions> payloadFunctionsMap = new HashMap<>();

    static {
        try {
            payloadFunctionsMap.put(PayloadInvalid.class, fromClass(PayloadInvalid.class));

            payloadFunctionsMap.put(PayloadStringData.class, fromClass(PayloadStringData.class));

            payloadFunctionsMap.put(PayloadLoginData.class, fromClass(PayloadLoginData.class));
            payloadFunctionsMap.put(PayloadSpeedXY.class, fromClass(PayloadSpeedXY.class));

            payloadFunctionsMap.put(PayloadGameTickData.class, fromClass(PayloadGameTickData.class));
            payloadFunctionsMap.put(PayloadGameFullData.class, fromClass(PayloadGameFullData.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
