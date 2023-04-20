package Online.MessagePayloadObjects;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class MessagePayload implements Externalizable {
    public MessagePayload() {
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF("DEFAULT WRITING METHOD (NOT TO USE)");
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        in.readUTF();
    }
}
