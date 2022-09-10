package model;

import java.util.Arrays;
import java.util.function.BiFunction;

public class Joust {

    private final int rows;
    private final int cols;
    private final CellState[][] board;
    private Player turn = Player.PLAYER1;
    private Winner winner = Winner.NONE;

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

    public void move(Player player, Cell endCell) throws Exception {
        if (winner != Winner.NONE) {
            throw new Exception("This game is already finished.");
        }
        if (player != this.turn) {
            throw new Exception("It's not your turn.");
        }
        Cell beginCell = getPlayerCell(player);
        int or = beginCell.getX(), oc = beginCell.getY();
        int dr = endCell.getX(), dc = endCell.getY();
        if (beginCell.equals(endCell)) {
            throw new Exception("Origin and destination must be different.");
        }
        if (board[or][oc] == CellState.EMPTY) {
            throw new Exception("Origin does not have a piece.");
        }
        if (board[dr][dc] != CellState.EMPTY) {
            throw new Exception("Destination must be empty.");
        }
        if (!isValidMove(beginCell, endCell)) {
            throw new Exception("This move is invalid.");
        }
        board[dr][dc] = board[or][oc];
        board[or][oc] = CellState.BLOCKED;
        turn = (turn == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
        this.winner = isGameOver();
    }

    private boolean isValidMove(Cell beginCell, Cell endCell) {
        int or = beginCell.getX(), oc = beginCell.getY();
        int dr = endCell.getX(), dc = endCell.getY();
        /* Destino deve estar vazio */
        if (board[dr][dc] != CellState.EMPTY) {
            return false;
        }
        Cell[] pos = {new Cell(-2, -1), new Cell(-2, 1), new Cell(2, -1), new Cell(2, 1), new Cell(-1, -2), new Cell(-1, 2), new Cell(1, -2), new Cell(1, 2)};
        for (Cell c : pos) {
            Cell cell = new Cell(or + c.getX(), oc + c.getY());
            if (this.onBoard(cell) && cell.equals(endCell)) {
                return true;
            }
        }
        return false;
    }

    private Cell getPlayerCell(Player player) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((board[i][j] == CellState.PLAYER1 && player == Player.PLAYER1)
                        || (board[i][j] == CellState.PLAYER2 && player == Player.PLAYER2)) {
                    return new Cell(i, j);
                }
            }
        }
        return null;
    }

    private boolean canMove(Player player) {
        Cell cell = getPlayerCell(player);
        boolean ok = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isValidMove(cell, new Cell(i, j))) {
                    ok = true;
                }
            }
        }
        return ok;
    }

    private Winner isGameOver() {
        boolean p1 = canMove(Player.PLAYER1);
        boolean p2 = canMove(Player.PLAYER2);
        if (!p1 && !p2) {
            return Winner.DRAW;
        } else if (!p1) {
            return Winner.PLAYER2;
        } else if (!p2) {
            return Winner.PLAYER1;
        }
        return Winner.NONE;
    }

    public Winner getWinner() {
        return this.winner;
    }

    private boolean onBoard(Cell cell) {
        BiFunction<Integer, Integer, Boolean> inLimit = (value, limit) -> value >= 0 && value < limit;
        return (inLimit.apply(cell.getX(), this.rows) && inLimit.apply(cell.getY(), this.cols));
    }

    public Player getTurn() {
        return turn;
    }

    public CellState[][] getBoard() {
        return board;
    }
}
