import Cell from "./Cell.js";
import Player from "./Player.js";

class JoustPlayer {
    constructor(_turn, _game) {
        this.turn = turn;
        this.game = _game;
    }
    minimax(matrix, currentPlayer = this.turn, depth = 0) {
        let cell = this.getPlayerPosition(currentPlayer);
        let moves = this.game.possibleMoves(cell, matrix);
        
    }
    getPlayerPosition(player) {
        let matrix = this.game.getBoard();
        let i = matrix.flat().indexOf(player === Player.PLAYER1 ? CellState.PLAYER1 : CellState.PLAYER2);
        return new Cell(Math.floor(i / this.cols), i % this.cols);
    }
}