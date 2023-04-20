package Online.MessagePayloadObjects;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class PayloadStringData extends MessagePayload {
    public String str;

    public PayloadStringData() {
    }

    public PayloadStringData(String str) {
        this.str = str;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(str);
        out.flush();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadStringData that = (PayloadStringData) o;

        return Objects.equals(str, that.str);
    }

    @Override
    public int hashCode() {
        return str != null ? str.hashCode() : 0;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        str = in.readUTF();
    }

    @Override
    public String toString() {
        return "PayloadStringData{" +
                "str='" + str + '\'' +
                '}';
    }
}
