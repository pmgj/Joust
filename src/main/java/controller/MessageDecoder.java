package controller;

import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.websocket.DecodeException;
import jakarta.websocket.Decoder;
import jakarta.websocket.EndpointConfig;

public class MessageDecoder implements Decoder.Text<InputMessage> {

    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public InputMessage decode(final String textMessage) throws DecodeException {
        return JsonbBuilder.create().fromJson(textMessage, InputMessage.class);
    }

    @Override
    public boolean willDecode(final String s) {
        try {
            JsonbBuilder.create().fromJson(s, InputMessage.class);
            return true;
        } catch (JsonbException ex) {
            return false;
        }
    }
}
