import { CellState } from './CellState.js';
import { Joust } from './Joust.js';

function GUI() {
    let game;
    function init() {
        let form = document.forms[0];
        let cols = parseInt(form.cols.value);
        let rows = parseInt(form.rows.value);
        game = new Joust(rows, cols);
        let tab = game.getBoard();
        let tbody = document.querySelector("tbody");
        tbody.innerHTML = "";
        const images = { PLAYER1: "White-Knight.svg", PLAYER2: "Black-Knight.svg" };
        for (let i = 0; i < tab.length; i++) {
            let tr = document.createElement("tr");
            for (let j = 0; j < tab[i].length; j++) {
                let td = document.createElement("td");
                if (tab[i][j] !== CellState.EMPTY) {
                    let img = document.createElement("img");
                    img.src = `images/${images[tab[i][j]]}`;
                    td.appendChild(img);
                }
                tr.appendChild(td);
            }
            tbody.appendChild(tr);
        }
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
