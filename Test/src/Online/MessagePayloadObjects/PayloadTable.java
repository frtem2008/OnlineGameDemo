package Online.MessagePayloadObjects;

import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadLoginData;
import Online.MessagePayloadObjects.PlayerMessagesPayloadObjects.PayloadSpeedXY;
import Online.MessagePayloadObjects.ServerMessagesPayloadObjects.PayloadGameData;

import java.io.ObjectInput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

public class PayloadTable {
    public static HashMap<Class<? extends MessagePayload>, PayloadFunctions> payloadFunctionsMap = new HashMap<>();

    static {
        try {
            payloadFunctionsMap.put(PayloadInvalid.class, fromPayloadClass(PayloadInvalid.class));

            payloadFunctionsMap.put(PayloadStringData.class, fromPayloadClass(PayloadStringData.class));

            payloadFunctionsMap.put(PayloadLoginData.class, fromPayloadClass(PayloadLoginData.class));
            payloadFunctionsMap.put(PayloadSpeedXY.class, fromPayloadClass(PayloadSpeedXY.class));

            payloadFunctionsMap.put(PayloadGameData.class, fromPayloadClass(PayloadGameData.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static PayloadFunctions fromPayloadClass(Class<? extends MessagePayload> payloadClass) throws NoSuchMethodException {
        Method readMethod = payloadClass.getDeclaredMethod("readExternal", ObjectInput.class);
        Constructor<? extends MessagePayload> constructor = payloadClass.getDeclaredConstructor();
        return new PayloadFunctions(readMethod, constructor);
    }
}
