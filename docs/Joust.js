import {CellState} from './CellState.js';
import {Player} from './Player.js';

function Joust(nrows, ncols) {
    const rows = nrows;
    const cols = ncols;
    let board = startBoard();
    let turn = Player.PLAYER1;
    function getBoard() {
        return board;
    }
    function startBoard() {
        let matrix = Array(rows).fill().map(() => Array(cols).fill(CellState.EMPTY));
        let players = [CellState.PLAYER1, CellState.PLAYER2];
        for (let i = 0; i < players.length; ) {
            let row = Math.floor(Math.random() * rows);
            let col = Math.floor(Math.random() * cols);
            if (matrix[row][col] === CellState.EMPTY) {
                matrix[row][col] = players[i++];
            }
        }
        return matrix;
    }
    function getTurn() {
        return turn;
    }
    function move(beginCell, endCell) {
        let {x: or, y: oc} = beginCell;
        let {x: dr, y: dc} = endCell;
        if (!beginCell || !endCell) {
            throw new Error("The value of one of the cells does not exist.");
        }
        let currentPiece = getPiece(beginCell);
        if ((currentPiece === CellState.PLAYER1 && turn === Player.PLAYER2) || (currentPiece === CellState.PLAYER2 && turn === Player.PLAYER1)) {
            throw new Error("It's not your turn.");
        }
        if (beginCell.equals(endCell)) {
            throw new Error("Origin and destination must be different.");
        }
        if (!onBoard(beginCell) || !onBoard(endCell)) {
            throw new Error("Origin or destination are not in the board.");
        }
        if (currentPiece === CellState.EMPTY) {
            throw new Error("Origin does not have a piece.");
        }
        if (getPiece(endCell) !== CellState.EMPTY) {
            throw new Error("Destination must be empty.");
        }
        /* Realizar movimento */
        board[dr][dc] = board[or][oc];
        board[or][oc] = CellState.BLOCKED;
        turn = (turn === Player.PLAYER1) ? Player.PLAYER2 : Player.PLAYER1;
    }
    function getPiece( {x, y}) {
        return board[x][y];
    }
    function onBoard( {x, y}) {
        let inLimit = (value, limit) => value >= 0 && value < limit;
        return (inLimit(x, rows) && inLimit(y, cols));
    }
    return {getBoard, getTurn, move};
}

export {Joust};