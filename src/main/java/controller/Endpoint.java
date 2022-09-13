package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
                    room.setS1(session);
                    session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.OPEN, Player.PLAYER1));
                } else if (room.getS2() == null) {
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
                room.getVisitors().add(session);
                session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.OPEN, Player.VISITOR));
                session.getBasicRemote().sendObject(new OutputMessage(ConnectionType.MESSAGE, game));
                break;
            case EXIT_ROOM:
                this.exitRoom(session);
                break;
            case MOVE_PIECE:
                room = this.findRoom(session).get();
                game = room.getGame();
                try {
                    game.move(session == room.getS1() ? Player.PLAYER1 : Player.PLAYER2, message.getCell());
                    Winner ret = game.getWinner();
                    sendMessage(room, new OutputMessage(
                            ret == Winner.NONE ? ConnectionType.MESSAGE : ConnectionType.ENDGAME, game));
                } catch (Exception ex) {

                }
            default:
                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) throws IOException, EncodeException {
        this.exitRoom(session);
        onRooms.remove(session);
    }

    private void exitRoom(Session session) throws EncodeException, IOException {
        Optional<Room> optional = this.findRoom(session);
        if (optional.isEmpty()) {
            return;
        }
        Room room = optional.get();
        Session s1 = room.getS1();
        Session s2 = room.getS2();
        Joust game = room.getGame();
        if (s1 == session) {
            game.setWinner(Winner.PLAYER2);
        }
        if (s2 == session) {
            game.setWinner(Winner.PLAYER1);
        }
        if (s1 == session || s2 == session) {
            sendMessage(room, new OutputMessage(ConnectionType.ENDGAME, game));
        }
        List<Session> visitors = room.getVisitors();
        visitors.remove(session);
        if (s1 == session || s2 == session) {
            room.reset();
            updateRooms();
        }
    }

    private Optional<Room> findRoom(Session s) {
        for (Room r : rooms) {
            if (r.getS1() == s || r.getS2() == s || r.getVisitors().contains(s)) {
                return Optional.of(r);
            }
        }
        return Optional.empty();
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
