package controller;

import java.util.List;
import model.Cell;
import model.CellState;
import model.Player;
import model.Winner;

public class Message {

    private ConnectionType type;
    private Player turn;
    private CellState[][] board;
    private Winner winner;
    private List<RoomMessage> rooms;
    private int room;
    private Cell cell;

    public Message() {

    }

    public Message(ConnectionType type, List<RoomMessage> rooms) {
        this.type = type;
        this.rooms = rooms;
    }

    public Message(ConnectionType type, Player turn) {
        this.type = type;
        this.turn = turn;
    }

    public Message(ConnectionType type, Player turn, CellState[][] board) {
        this.type = type;
        this.turn = turn;
        this.board = board;
    }

    public Message(ConnectionType type, Winner win, CellState[][] board) {
        this.type = type;
        this.winner = win;
        this.board = board;
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

    public CellState[][] getBoard() {
        return board;
    }

    public void setBoard(CellState[][] board) {
        this.board = board;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public List<RoomMessage> getRooms() {
        return rooms;
    }

    public void setRooms(List<RoomMessage> rooms) {
        this.rooms = rooms;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
}
