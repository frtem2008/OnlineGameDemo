package Online.MessagePayloadObjects;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class PayloadInvalid extends MessagePayload {
    public String invalid;

    @Override
    public String toString() {
        return "PayloadInvalid{" +
                "invalid='" + invalid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PayloadInvalid that = (PayloadInvalid) o;

        return Objects.equals(invalid, that.invalid);
    }

    @Override
    public int hashCode() {
        return invalid != null ? invalid.hashCode() : 0;
    }

    public PayloadInvalid() {
        invalid = null;
    }

    public PayloadInvalid(String invalid) {
        this.invalid = invalid;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF(invalid);
        out.flush();
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        invalid = in.readUTF();
    }
}
