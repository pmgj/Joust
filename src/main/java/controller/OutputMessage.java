package controller;

import java.util.List;

import model.Joust;
import model.Player;

public class OutputMessage {

    private ConnectionType type;
    private Player turn;
    private List<RoomMessage> rooms;
    private Joust game;

    public OutputMessage() {

    }

    public OutputMessage(ConnectionType type, List<RoomMessage> rooms) {
        this.type = type;
        this.rooms = rooms;
    }

    public OutputMessage(ConnectionType type, Player turn) {
        this.type = type;
        this.turn = turn;
    }

    public OutputMessage(ConnectionType type, Joust game) {
        this.type = type;
        this.game = game;
    }

    public ConnectionType getType() {
        return type;
    }

    public void setType(ConnectionType type) {
        this.type = type;
    }

    public Player getTurn() {
        return turn;
    }

    public void setTurn(Player turn) {
        this.turn = turn;
    }

    public List<RoomMessage> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomMessage> rooms) {
        this.rooms = rooms;
    }

    public Joust getGame() {
        return game;
    }

    public void setGame(Joust game) {
        this.game = game;
    }
}
