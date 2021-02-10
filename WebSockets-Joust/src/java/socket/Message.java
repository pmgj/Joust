package socket;

import model.CellState;

public class Message {
    private CellState[][] board;

    public CellState[][] getBoard() {
        return board;
    }

    public void setBoard(CellState[][] board) {
        this.board = board;
    }
}
