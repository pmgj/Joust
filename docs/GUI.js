import CellState from './CellState.js';
import Joust from './Joust.js';
import Cell from './Cell.js';

class GUI {
    constructor() {
        this.game = null;
        this.images = { PLAYER1: "White-Knight.svg", PLAYER2: "Black-Knight.svg" };    
    }
    init() {
        let form = document.forms[0];
        let cols = parseInt(form.cols.value);
        let rows = parseInt(form.rows.value);
        this.game = new Joust(rows, cols);
        let tab = this.game.getBoard();
        let tbody = document.querySelector("tbody");
        tbody.innerHTML = "";
        for (let i = 0; i < tab.length; i++) {
            let tr = document.createElement("tr");
            for (let j = 0; j < tab[i].length; j++) {
                let td = document.createElement("td");
                if (tab[i][j] !== CellState.EMPTY) {
                    let img = document.createElement("img");
                    img.src = `images/${this.images[tab[i][j]]}`;
                    td.appendChild(img);
                }
                td.onclick = this.play.bind(this);
                tr.appendChild(td);
            }
            tbody.appendChild(tr);
        }
        this.changeMessage();
    }
    unregisterEvents() {
        let cells = document.querySelectorAll("td");
        cells.forEach(c => c.onclick = undefined);
    }
    play(evt) {
        let image = document.querySelector(`img[src*="${this.images[this.game.getTurn()]}"]`);
        let origin = image.parentNode;
        try {
            const destination = evt.target;
            let begin = this.coordinates(origin);
            let end = this.coordinates(destination);
            let mr = this.game.move(begin, end);
            let { x: or, y: oc } = begin;
            let { x: dr, y: dc } = end;
            const time = 1000;
            let anim = image.animate([{ top: 0, left: 0 }, { top: `${(dr - or) * 58}px`, left: `${(dc - oc) * 58}px` }], time);
            anim.onfinish = () => {
                origin.textContent = "X";
                origin.className = "blocked";
                destination.innerHTML = "";
                destination.appendChild(image);
                this.changeMessage(mr);
            };
        } catch (ex) {
            this.setMessage(ex.message);
        }
    }
    coordinates(cell) {
        return new Cell(cell.parentNode.rowIndex, cell.cellIndex);
    }
    setMessage(msg) {
        let message = document.getElementById("message");
        message.innerHTML = msg;
    }
    changeMessage(m) {
        this.hidePossibleMoves();
        let objs = { DRAW: "Draw!", PLAYER2: "Black's win!", PLAYER1: "White's win!" };
        if (objs[m]) {
            this.setMessage(`Game Over! ${objs[m]}`);
            this.unregisterEvents();
        } else {
            let msgs = { PLAYER1: "White's turn.", PLAYER2: "Black's turn." };
            this.setMessage(msgs[this.game.getTurn()]);
            this.showPossibleMoves();
        }
    }
    showPossibleMoves() {
        let image = document.querySelector(`img[src*="${this.images[this.game.getTurn()]}"]`);
        let origin = image.parentNode;
        let moves = this.game.possibleMoves(this.coordinates(origin));
        for (let { x, y } of moves) {
            let tempCell = document.querySelector(`tr:nth-child(${x + 1}) td:nth-child(${y + 1})`);
            tempCell.className = 'selected';
        }
    }
    hidePossibleMoves() {
        let cells = document.querySelectorAll("td");
        cells.forEach(c => {
            if (c.className !== 'blocked') {
                c.className = 'unselected';
            }
        });
    }
    registerEvents() {
        let form = document.forms[0];
        form.rows.onchange = this.init.bind(this);
        form.cols.onchange = this.init.bind(this);
        this.init();
    }
}
let gui = new GUI();
gui.registerEvents();
