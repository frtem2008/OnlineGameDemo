package Online.MessagePayloadObjects;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public record PayloadFunctions(Method readMethod, Constructor<? extends MessagePayload> constructor) {

}
