import CellState from './CellState.js';
import Joust from './Joust.js';
import Cell from './Cell.js';

function GUI() {
    let game = new Joust(8, 8);
    const images = { PLAYER1: "White-Knight.svg", PLAYER2: "Black-Knight.svg" };
    function init() {
        let form = document.forms[0];
        let cols = parseInt(form.cols.value);
        let rows = parseInt(form.rows.value);
        game = new Joust(rows, cols);
        let tab = game.getBoard();
        let tbody = document.querySelector("tbody");
        tbody.innerHTML = "";
        for (let i = 0; i < tab.length; i++) {
            let tr = document.createElement("tr");
            for (let j = 0; j < tab[i].length; j++) {
                let td = document.createElement("td");
                if (tab[i][j] !== CellState.EMPTY) {
                    let img = document.createElement("img");
                    img.src = `images/${images[tab[i][j]]}`;
                    td.appendChild(img);
                }
                td.onclick = play;
                tr.appendChild(td);
            }
            tbody.appendChild(tr);
        }
        changeMessage();
    }
    function play() {
        let image = document.querySelector(`img[src*="${images[game.getTurn()]}"]`);
        let origin = image.parentNode;
        try {
            let begin = coordinates(origin);
            let end = coordinates(this);
            let mr = game.move(begin, end);
            let { x: or, y: oc } = begin;
            let { x: dr, y: dc } = end;
            const time = 1000;
            let anim = image.animate([{ top: 0, left: 0 }, { top: `${(dr - or) * 58}px`, left: `${(dc - oc) * 58}px` }], time);
            anim.onfinish = () => {
                origin.textContent = "X";
                origin.className = "blocked";
                this.innerHTML = "";
                this.appendChild(image);
                changeMessage(mr);
            };
        } catch (ex) {
            setMessage(ex.message);
        }
    }
    function coordinates(cell) {
        return new Cell(cell.parentNode.rowIndex, cell.cellIndex);
    }
    function setMessage(msg) {
        let message = document.getElementById("message");
        message.innerHTML = msg;
    }
    function changeMessage(m) {
        hidePossibleMoves();
        let objs = { DRAW: "Draw!", PLAYER2: "Black's win!", PLAYER1: "White's win!" };
        if (objs[m]) {
            setMessage(`Game Over! ${objs[m]}`);
        } else {
            let msgs = { PLAYER1: "White's turn.", PLAYER2: "Black's turn." };
            setMessage(msgs[game.getTurn()]);
            showPossibleMoves();
        }
    }
    function showPossibleMoves() {
        let image = document.querySelector(`img[src*="${images[game.getTurn()]}"]`);
        let origin = image.parentNode;
        let moves = game.possibleMoves(coordinates(origin));
        for (let { x, y } of moves) {
            let tempCell = document.querySelector(`tr:nth-child(${x + 1}) td:nth-child(${y + 1})`);
            tempCell.className = 'selected';
        }
    }
    function hidePossibleMoves() {
        let cells = document.querySelectorAll("td");
        cells.forEach(c => {
            if (c.className !== 'blocked') {
                c.className = 'unselected';
            }
        });
    }
    function registerEvents() {
        let form = document.forms[0];
        form.rows.onchange = init;
        form.cols.onchange = init;
        init();
    }
    return { registerEvents };
}
let gui = GUI();
gui.registerEvents();
