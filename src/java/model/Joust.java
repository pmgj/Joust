package model;

import java.util.Arrays;
import java.util.stream.Stream;

public class Joust {

    private final int rows;
    private final int cols;
    private final CellState[][] board;
    private Player turn = Player.PLAYER1;
    private MoveResult gameIsOver = new MoveResult(Move.VALID);

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

    public MoveResult move(Player player, Cell endCell) {
        if (gameIsOver.getWinner() != null) {
            return gameIsOver;
        }
        if (player != turn) {
            return new MoveResult(Move.INVALID);
        }
        Cell beginCell = getPlayerCell(player);
        MoveResult m = isValidMove(beginCell, endCell);
        if (m.getMove() == Move.VALID) {
            int or = beginCell.getX(), oc = beginCell.getY();
            int dr = endCell.getX(), dc = endCell.getY();
            board[dr][dc] = board[or][oc];
            board[or][oc] = CellState.BLOCKED;
            turn = (turn == Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
            gameIsOver = isGameOver();
            return gameIsOver;
        }
        return m;
    }

    private MoveResult isValidMove(Cell beginCell, Cell endCell) {
        int or = beginCell.getX(), oc = beginCell.getY();
        int dr = endCell.getX(), dc = endCell.getY();
        /* Destino deve estar vazio */
        if (board[dr][dc] != CellState.EMPTY) {
            return new MoveResult(Move.INVALID);
        }
        int[] lin = {-2, 2, -1, 1};
        int[][] inc = {{-1, 1}, {-1, 1}, {-2, 2}, {-2, 2}};
        for (int i = 0; i < lin.length; i++) {
            for (int j = 0; j < inc[i].length; j++) {
                Cell cell = new Cell(or + lin[i], oc + inc[i][j]);
                if (isValidCell(cell) && cell.equals(endCell)) {
                    return new MoveResult(Move.VALID);
                }
            }
        }
        return new MoveResult(Move.INVALID);
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

    private boolean canMove(Player player) {
        Cell cell = getPlayerCell(player);
        boolean ok = false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (isValidMove(cell, new Cell(i, j)).getMove() == Move.VALID) {
                    ok = true;
                }
            }
        }
        return ok;
    }

    private MoveResult isGameOver() {
        boolean p1 = canMove(Player.PLAYER1);
        boolean p2 = canMove(Player.PLAYER2);
        MoveResult mr = new MoveResult(Move.VALID);
        if (!p1 && !p2) {
            mr.setWinner(Winner.DRAW);
        } else if (!p1) {
            mr.setWinner(Winner.PLAYER2);
        } else if (!p2) {
            mr.setWinner(Winner.PLAYER1);
        }
        return mr;
    }

    public Winner getWinner() {
        return isGameOver().getWinner();
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
