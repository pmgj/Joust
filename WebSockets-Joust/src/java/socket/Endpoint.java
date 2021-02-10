package socket;

import java.io.IOException;
import javax.websocket.EncodeException;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import model.Joust;

@ServerEndpoint(value = "/joust", encoders = MessageEncoder.class)
public class Endpoint {

    private static Session s1;
    private static Session s2;
    private static Joust game;

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        if (s1 == null) {
            s1 = session;
        } else if (s2 == null) {
            s2 = session;
            game = new Joust(8, 8);
            Message m = new Message();
            m.setBoard(game.getBoard());
            s1.getBasicRemote().sendObject(m);
            s2.getBasicRemote().sendObject(m);
        } else {
            session.close();
        }
    }
}
