package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EncodeException;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import model.Joust;
import model.Player;
import model.Winner;

@ServerEndpoint(value = "/joust", encoders = MessageEncoder.class, decoders = MessageDecoder.class)
public class Endpoint {

    private final static List<Room> rooms = new ArrayList<>();
    private final static List<Session> onRooms = new ArrayList<>();

    static {
        for (int i = 0; i < 12; i++) {
            Room s = new Room();
            rooms.add(s);
        }
    }

    @OnOpen
    public void onOpen(Session session) throws IOException, EncodeException {
        session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.GET_ROOMS, this.convert(rooms)));
        onRooms.add(session);
    }

    @OnMessage
    public void onMessage(Session session, InputMessage message) throws EncodeException, IOException {
        Room room;
        Joust game;
        switch (message.getType()) {
            case ENTER_ROOM:
                room = rooms.get(message.getRoom());
                if (room.getS1() == null) {
                    onRooms.remove(session);
                    room.setS1(session);
                    session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.OPEN, Player.PLAYER1));
                } else if (room.getS2() == null) {
                    onRooms.remove(session);
                    room.setS2(session);
                    session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.OPEN, Player.PLAYER2));
                    room.createGame();
                    game = room.getGame();
                    sendMessage(room, new OutputMessage(ConnectionType.MESSAGE, game));
                    updateRooms();
                }
                break;
            case WATCH_ROOM:
                room = rooms.get(message.getRoom());
                game = room.getGame();
                if (game == null)
                    return;
                onRooms.remove(session);
                room.getVisitors().add(session);
                session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.OPEN, Player.VISITOR));
                session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.MESSAGE, game));
                break;
            case MOVE_PIECE:
                room = findRoom(session);
                game = room.getGame();
                try {
                    game.move(session == room.getS1() ? Player.PLAYER1 : Player.PLAYER2, message.getCell());
                    Winner ret = game.getWinner();
                    if (ret == Winner.NONE) {
                        sendMessage(room, new OutputMessage(ConnectionType.MESSAGE, game));
                    } else {
                        sendMessage(room, new OutputMessage(ConnectionType.ENDGAME, game));
                    }
                } catch (Exception ex) {

                }
            default:
                break;
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
                game.setWinner(Winner.PLAYER2);
                sendMessage(r, new OutputMessage(ConnectionType.ENDGAME, game));
            }
        }
        if (s2 == session) {
            if (s1 != null && reason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE) {
                game.setWinner(Winner.PLAYER1);
                sendMessage(r, new OutputMessage(ConnectionType.ENDGAME, game));
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
        OutputMessage message = new OutputMessage(ConnectionType.GET_ROOMS, this.convert(rooms));
        for (Session s : onRooms) {
            if (s.isOpen()) {
                s.getBasicRemote().sendObject(message);
            }
        }
    }

    private List<RoomMessage> convert(List<Room> rooms) {
        return rooms.stream().map(room -> new RoomMessage(room)).collect(Collectors.toList());
    }

    private void sendMessage(Room room, OutputMessage msg) throws EncodeException, IOException {
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
