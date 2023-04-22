package Online;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public record ReadFunctions(Method readMethod, Constructor<? extends Externalizable> constructor) {
    public static ReadFunctions fromClass(Class<? extends Externalizable> payloadClass) throws NoSuchMethodException {
        Method readMethod = payloadClass.getDeclaredMethod("readExternal", ObjectInput.class);
        Constructor<? extends Externalizable> constructor = payloadClass.getDeclaredConstructor();
        return new ReadFunctions(readMethod, constructor);
    }
}
