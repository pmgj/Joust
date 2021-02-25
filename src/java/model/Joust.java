package model;

import java.util.Arrays;
import java.util.stream.Stream;

public class Joust {

    private final int rows;
    private final int cols;
    private final CellState[][] board;

    public Joust(int nrows, int ncols) {
        this.rows = nrows;
        this.cols = ncols;
        this.board = this.startBoard();
    }

    private CellState[][] startBoard() {
        CellState[][] matrix = new CellState[rows][cols];
        for (CellState[] row : matrix) {
            Arrays.fill(row, CellState.EMPTY);
        }
        CellState[] players = {CellState.PLAYER1, CellState.PLAYER2};
        for (int i = 0; i < players.length;) {
            int row = (int) Math.floor(Math.random() * rows);
            int col = (int) Math.floor(Math.random() * cols);
            if (matrix[row][col] == CellState.EMPTY) {
                matrix[row][col] = players[i];
                i++;
            }
        }
        return matrix;
    }    

    public CellState[][] getBoard() {
        return board;
    }
    
    public static void main(String[] args) {
        Joust j = new Joust(8, 8);
        Stream.of(j.getBoard()).forEach(x -> System.out.println(Arrays.toString(x)));
    }
}
