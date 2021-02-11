package socket;

import model.CellState;
import model.Player;

public class Message {
    private ConnectionType type;
    private Player turn;
    private CellState[][] board;

    public Message() {
        
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
}
