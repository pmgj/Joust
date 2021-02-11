package socket;

import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import model.Cell;

public class CellDecoder implements Decoder.Text<Cell> {

    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

    @Override
    public Cell decode(final String textMessage) throws DecodeException {
        return JsonbBuilder.create().fromJson(textMessage, Cell.class);
    }

    @Override
    public boolean willDecode(final String s) {
        try {
            JsonbBuilder.create().fromJson(s, Cell.class);
            return true;
        } catch (JsonbException ex) {
            return false;
        }
    }
}
