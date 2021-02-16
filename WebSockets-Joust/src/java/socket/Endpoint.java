package socket;

import java.io.IOException;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import model.Cell;
import model.Joust;
import model.Move;
import model.MoveResult;
import model.Player;
import model.Winner;

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
        } else {
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, Cell message) throws EncodeException, IOException {
        MoveResult ret = game.move(session == s1 ? Player.PLAYER1 : Player.PLAYER2, message);
        if (ret.getMove() == Move.VALID) {
            if (ret.getWinner() == null) {
                sendMessage(new Message(ConnectionType.MESSAGE, game.getTurn(), game.getBoard()));
            } else {
                sendMessage(new Message(ConnectionType.ENDGAME, game.getWinner(), game.getBoard()));
            }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) throws IOException, EncodeException {
        if (s1 == session) {
            if (reason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
                s2.getBasicRemote().sendObject(new Message(ConnectionType.ENDGAME, Winner.PLAYER2, game.getBoard()));
            }
            s1 = null;
        }
        if (s2 == session) {
            if (reason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
                s1.getBasicRemote().sendObject(new Message(ConnectionType.ENDGAME, Winner.PLAYER1, game.getBoard()));
            }
            s2 = null;
        }
    }

    private void sendMessage(Message msg) throws EncodeException, IOException {
        s1.getBasicRemote().sendObject(msg);
        s2.getBasicRemote().sendObject(msg);
    }
}
