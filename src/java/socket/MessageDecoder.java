package socket;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<Message> {

    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Message decode(final String textMessage) throws DecodeException {
        return JsonbBuilder.create().fromJson(textMessage, Message.class);
    }

    @Override
    public boolean willDecode(final String s) {
        try {
            JsonbBuilder.create().fromJson(s, Message.class);
            return true;
        } catch (JsonbException ex) {
            return false;
        }
    }
}
