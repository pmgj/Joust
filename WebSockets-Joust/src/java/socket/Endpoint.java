package socket;

import java.io.IOException;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import model.Cell;
import model.Joust;
import model.Move;
import model.Player;

@ServerEndpoint(value = "/joust", encoders = MessageEncoder.class, decoders = CellDecoder.class)
public class Endpoint {

    private static Session s1;
    private static Session s2;
    private static Joust game;

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        if (s1 == null) {
            s1 = session;
            s1.getBasicRemote().sendObject(new Message(ConnectionType.OPEN, Player.PLAYER1));
        } else if (s2 == null) {
            s2 = session;
            game = new Joust(8, 8);
            s2.getBasicRemote().sendObject(new Message(ConnectionType.OPEN, Player.PLAYER2));
            sendMessage(new Message(ConnectionType.MESSAGE, game.getTurn(), game.getBoard()));
//            Message m = new Message();
//            m.setBoard(game.getBoard());
//            s1.getBasicRemote().sendObject(m);
//            s2.getBasicRemote().sendObject(m);
        } else {
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, Cell message) throws EncodeException, IOException {
        Move ret = game.move(session == s1 ? Player.PLAYER1 : Player.PLAYER2, message);
        if (ret == Move.VALID) {
            sendMessage(new Message(ConnectionType.MESSAGE, game.getTurn(), game.getBoard()));
        }
    }

    private void sendMessage(Message msg) throws EncodeException, IOException {
        s1.getBasicRemote().sendObject(msg);
        s2.getBasicRemote().sendObject(msg);
    }
}
