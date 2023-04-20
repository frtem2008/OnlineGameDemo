package Online;

import Online.MessagePayloadObjects.MessagePayload;
import Online.MessagePayloadObjects.PayloadStringData;

import java.io.Externalizable;
import java.util.Objects;

public class Message {
    public MessageType type;
    public Externalizable payload;

    public Message() {
        type = MessageType.INVALID;
        payload = null;
    }

    public Message(MessageType type, MessagePayload payload) {
        this.type = type;
        if (payload.getClass().equals(type.payload))
            this.payload = payload;
        else
            throw new IllegalArgumentException("Message type and payload type are different");
    }

    public static Message ErrorMessage(String errorMsg) {
        return new Message(MessageType.ERROR, new PayloadStringData(errorMsg));
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                ", payload=" + payload +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (type != message.type) return false;
        return Objects.equals(payload, message.payload);
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (payload != null ? payload.hashCode() : 0);
        return result;
    }
}
