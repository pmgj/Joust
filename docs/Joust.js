import { CellState } from './CellState.js';

function Joust(nrows, ncols) {
    const rows = nrows;
    const cols = ncols;
    let board = startBoard();
    function getBoard() {
        return board;
    }
    function startBoard() {
        let matrix = Array(rows).fill().map(() => Array(cols).fill(CellState.EMPTY));
        let players = [CellState.PLAYER1, CellState.PLAYER2];
        for (let i = 0; i < players.length;) {
            let row = Math.floor(Math.random() * rows);
            let col = Math.floor(Math.random() * cols);
            if (matrix[row][col] === CellState.EMPTY) {
                matrix[row][col] = players[i++];
            }
        }
        return matrix;
    }
    return { getBoard };
}

export { Joust };