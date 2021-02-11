package model;

import java.util.Arrays;
import java.util.stream.Stream;

public class Joust {

    private final int rows;
    private final int cols;
    private Player turn = Player.PLAYER1;
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

    public Move move(Player player, Cell endCell) {
        if (player != turn) {
            return Move.INVALID;
        }
        Cell beginCell = getPlayerCell(player);
        Move m = isValidMove(beginCell, endCell);
        if (m == Move.VALID) {
            int or = beginCell.getX(), oc = beginCell.getY();
            int dr = endCell.getX(), dc = endCell.getY();
            board[dr][dc] = board[or][oc];
            board[or][oc] = CellState.BLOCKED;
            turn = (turn == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
        }
        return m;
    }

    private Move isValidMove(Cell beginCell, Cell endCell) {
        int or = beginCell.getX(), oc = beginCell.getY();
        int dr = endCell.getX(), dc = endCell.getY();
        /* Destino deve estar vazio */
        if (board[dr][dc] != CellState.EMPTY) {
            return Move.INVALID;
        }
        int[] lin = {-2, 2, -1, 1};
        int[][] inc = {{-1, 1}, {-1, 1}, {-2, 2}, {-2, 2}};
        for (int i = 0; i < lin.length; i++) {
            for (int j = 0; j < inc[i].length; j++) {
                Cell cell = new Cell(or + lin[i], oc + inc[i][j]);
                if (isValidCell(cell) && cell.equals(endCell)) {
                    return Move.VALID;
                }
            }
        }
        return Move.INVALID;
    }

    private Cell getPlayerCell(Player player) {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if ((board[i][j] == CellState.PLAYER1 && player == Player.PLAYER1)
                        || (board[i][j] == CellState.PLAYER2 && player == Player.PLAYER2)) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }

    private boolean isValidCell(Cell cell) {
        return (cell.getX() < rows && cell.getX() >= 0 && cell.getY() < cols && cell.getY() >= 0);
    }

    public Player getTurn() {
        return turn;
    }

    public CellState[][] getBoard() {
        return board;
    }

    public static void main(String[] args) {
        Joust j = new Joust(8, 8);
        Stream.of(j.getBoard()).forEach(x -> System.out.println(Arrays.toString(x)));
    }
}
