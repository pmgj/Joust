package socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import model.Joust;
import model.Player;
import model.Winner;

@ServerEndpoint(value = "/joust", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class Endpoint {

    private final static List<Room> rooms = new ArrayList();
    private final static List<Session> onRooms = new ArrayList();

    static {
        for (int i = 0; i < 12; i++) {
            Room s = new Room();
            rooms.add(s);
        }
    }

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(new Message(ConnectionType.GET_ROOMS, this.convert(rooms)));
        onRooms.add(session);
    }

    @OnMessage
    public void onMessage(Session session, Message message) throws EncodeException, IOException {
        Room room;
        switch (message.getType()) {
            case ENTER_ROOM:
                room = rooms.get(message.getRoom());
                if (room.getS1() == null) {
                    onRooms.remove(session);
                    room.setS1(session);
                    session.getBasicRemote().sendObject(new Message(ConnectionType.OPEN, Player.PLAYER1));
                } else if (room.getS2() == null) {
                    onRooms.remove(session);
                    room.setS2(session);
                    session.getBasicRemote().sendObject(new Message(ConnectionType.OPEN, Player.PLAYER2));
                    room.createGame();
                    Joust j = room.getGame();
                    sendMessage(room, new Message(ConnectionType.MESSAGE, j.getTurn(), j.getBoard()));
                    updateRooms();
                }
                break;
            case WATCH_ROOM:
                room = rooms.get(message.getRoom());
                Joust j = room.getGame();
                if(j == null) return;
                onRooms.remove(session);
                room.getVisitors().add(session);
                session.getBasicRemote().sendObject(new Message(ConnectionType.OPEN, Player.VISITOR));
                session.getBasicRemote().sendObject(new Message(ConnectionType.MESSAGE, j.getTurn(), j.getBoard()));
                break;
            case MESSAGE:
                room = findRoom(session);
                Joust game = room.getGame();
                try {
                    Winner ret = game.move(session == room.getS1() ? Player.PLAYER1 : Player.PLAYER2, message.getCell());
                    if (ret == null) {
                        sendMessage(room, new Message(ConnectionType.MESSAGE, game.getTurn(), game.getBoard()));
                    } else {
                        sendMessage(room, new Message(ConnectionType.ENDGAME, ret, game.getBoard()));
                    }
                } catch (Exception ex) {

                }
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) throws IOException, EncodeException {
        if (onRooms.contains(session)) {
            onRooms.remove(session);
        }
        Room r = findRoom(session);
        if (r == null) {
            return;
        }
        Session s1 = r.getS1();
        Session s2 = r.getS2();
        Joust game = r.getGame();
        List<Session> visitors = r.getVisitors();
        if (s1 == session) {
            if (s2 != null && reason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
                sendMessage(r, new Message(ConnectionType.ENDGAME, Winner.PLAYER2, game.getBoard()));
            }
        }
        if (s2 == session) {
            if (s1 != null && reason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
                sendMessage(r, new Message(ConnectionType.ENDGAME, Winner.PLAYER1, game.getBoard()));
            }
        }
        if (visitors.contains(session)) {
            visitors.remove(session);
            onRooms.add(session);
        }
        if (s1 == session || s2 == session) {
            if (rooms.contains(r)) {
                int index = rooms.indexOf(r);
                rooms.set(index, new Room());
                updateRooms();
            }
        }
    }

    private Room findRoom(Session s) {
        for (Room r : rooms) {
            if (r.getS1() == s || r.getS2() == s || r.getVisitors().contains(s)) {
                return r;
            }
        }
        return null;
    }

    private void updateRooms() throws EncodeException, IOException {
        Message message = new Message(ConnectionType.GET_ROOMS, this.convert(rooms));
        for (Session s : onRooms) {
            if (s.isOpen()) {
                s.getBasicRemote().sendObject(message);
            }
        }
    }

    private List<RoomMessage> convert(List<Room> rooms) {
        List<RoomMessage> ret = new ArrayList();
        rooms.stream().map(room -> new RoomMessage(room)).forEachOrdered(rm -> ret.add(rm));
        return ret;
    }

    private void sendMessage(Room room, Message msg) throws EncodeException, IOException {
        if (room.getS1().isOpen()) {
            room.getS1().getBasicRemote().sendObject(msg);
        }
        if (room.getS2().isOpen()) {
            room.getS2().getBasicRemote().sendObject(msg);
        }
        for (Session s : room.getVisitors()) {
            s.getBasicRemote().sendObject(msg);
        }
    }
}
