import CellState from './CellState.js';
import Player from './Player.js';
import Cell from './Cell.js';
import Winner from './Winner.js';

export default class Joust {
    constructor(nrows, ncols) {
        this.rows = nrows;
        this.cols = ncols;
        this.turn = Player.PLAYER1;    
        this.board = this.startBoard();
        this.winner = Winner.NONE;
    }
    getWinner() {
        return this.winner;
    }
    getBoard() {
        return this.board;
    }
    startBoard() {
        let matrix = Array(this.rows).fill().map(() => Array(this.cols).fill(CellState.EMPTY));
        let players = [CellState.PLAYER1, CellState.PLAYER2];
        for (let i = 0; i < players.length;) {
            let row = Math.floor(Math.random() * this.rows);
            let col = Math.floor(Math.random() * this.cols);
            if (matrix[row][col] === CellState.EMPTY) {
                matrix[row][col] = players[i++];
            }
        }
        return matrix;
    }
    getTurn() {
        return this.turn;
    }
    move(beginCell, endCell) {
        if(this.winner !== Winner.NONE) {
            throw new Error("This game is already finished.");
        }
        let { x: or, y: oc } = beginCell;
        let { x: dr, y: dc } = endCell;
        if (!beginCell || !endCell) {
            throw new Error("The value of one of the cells does not exist.");
        }
        let currentPiece = this.getPiece(beginCell);
        if ((currentPiece === CellState.PLAYER1 && this.turn === Player.PLAYER2) || (currentPiece === CellState.PLAYER2 && this.turn === Player.PLAYER1)) {
            throw new Error("It's not your turn.");
        }
        if (beginCell.equals(endCell)) {
            throw new Error("Origin and destination must be different.");
        }
        if (!this.onBoard(beginCell) || !this.onBoard(endCell)) {
            throw new Error("Origin or destination are not in the board.");
        }
        if (currentPiece === CellState.EMPTY) {
            throw new Error("Origin does not have a piece.");
        }
        if (this.getPiece(endCell) !== CellState.EMPTY) {
            throw new Error("Destination must be empty.");
        }
        let moves = this.possibleMoves(beginCell);
        if (!moves.some(z => z.equals(endCell))) {
            throw new Error("This move is invalid.");
        }
        this.board[dr][dc] = this.board[or][oc];
        this.board[or][oc] = CellState.BLOCKED;
        this.turn = (this.turn === Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
        this.winner = this.endOfGame();
    }
    getPiece({ x, y }) {
        return this.board[x][y];
    }
    onBoard({ x, y }) {
        let inLimit = (value, limit) => value >= 0 && value < limit;
        return (inLimit(x, this.rows) && inLimit(y, this.cols));
    }
    possibleMoves({ x, y }, matrix = this.board) {
        let positions = [new Cell(x - 2, y - 1), new Cell(x - 2, y + 1), new Cell(x + 2, y - 1), new Cell(x + 2, y + 1), new Cell(x - 1, y - 2), new Cell(x - 1, y + 2), new Cell(x + 1, y - 2), new Cell(x + 1, y + 2)];
        return positions.filter(cell => this.onBoard(cell) && matrix[cell.x][cell.y] === CellState.EMPTY);
    }
    endOfGame() {
        let p1 = this.canMove(Player.PLAYER1);
        let p2 = this.canMove(Player.PLAYER2);
        if (!p1 && !p2) {
            return Winner.DRAW;
        } else if (!p1) {
            return Winner.PLAYER2;
        } else if (!p2) {
            return Winner.PLAYER1;
        }
        return Winner.NONE;
    }
    canMove(player) {
        let i = this.board.flat().indexOf(player === Player.PLAYER1 ? CellState.PLAYER1 : CellState.PLAYER2);
        let cell = new Cell(Math.floor(i / this.cols), i % this.cols);
        return this.board.flat().some((e, i) => this.isValidMove(cell, new Cell(Math.floor(i / this.cols), i % this.cols)));
    }
    isValidMove(player, endCell) {
        let moves = this.possibleMoves(player);
        return moves.some(e => e.equals(endCell));
    }
}