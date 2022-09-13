import Cell from "./Cell.js";
import CellState from "./CellState.js";
import ConnectionType from "./ConnectionType.js";
import MessageType from "./MessageType.js";
import Winner from "./Winner.js";
import Player from "./Player.js";

class GUI {
    constructor() {
        this.ws = null;
        this.images = { PLAYER1: "White-Knight.svg", PLAYER2: "Black-Knight.svg" };
        this.player = null;
    }
    coordinates(cell) {
        return new Cell(cell.parentNode.rowIndex, cell.cellIndex);
    }
    setMessage(msg) {
        let message = document.getElementById("message");
        message.innerHTML = msg;
    }
    play(event) {
        let cellDestino = event.currentTarget;
        let dCell = this.coordinates(cellDestino);
        this.ws.send(JSON.stringify({ type: MessageType.MOVE_PIECE, cell: dCell }));
    }
    printBoard(matrix) {
        let table = document.querySelector("table");
        for (let i = 0; i < matrix.length; i++) {
            for (let j = 0; j < matrix[i].length; j++) {
                let td = table.rows[i].cells[j];
                td.innerHTML = "";
                td.className = "";
                td.onclick = this.play.bind(this);
                switch (matrix[i][j]) {
                    case CellState.BLOCKED:
                        td.className = "blocked";
                        td.innerHTML = "X";
                        break;
                    case CellState.PLAYER1:
                    case CellState.PLAYER2:
                        td.innerHTML = `<img src='images/${this.images[matrix[i][j]]}' alt=''>`;
                        break;
                }
            }
        }
    }
    setButtonText(message) {
        let button = document.querySelector("#quit");
        button.value = message;
    }
    clearBoard() {
        let cells = document.querySelectorAll("td");
        cells.forEach(td => {
            td.innerHTML = "";
            td.className = "";
            td.onclick = undefined;
        });
    }
    unsetEvents() {
        let cells = document.querySelectorAll("td");
        cells.forEach(td => td.onclick = undefined);
    }
    enterRoom(evt) {
        let input = evt.currentTarget;
        let obj = { type: MessageType.ENTER_ROOM, room: parseInt(input.dataset.room) };
        this.ws.send(JSON.stringify(obj));
    }
    watchRoom(evt) {
        let input = evt.currentTarget;
        let obj = { type: MessageType.WATCH_ROOM, room: parseInt(input.dataset.room) };
        this.ws.send(JSON.stringify(obj));
    }
    exitRoom() {
        let obj = { type: MessageType.EXIT_ROOM };
        this.ws.send(JSON.stringify(obj));
        this.showRoom(false);
    }
    readData(evt) {
        let data = JSON.parse(evt.data);
        let game = data.game;
        switch (data.type) {
            case ConnectionType.GET_ROOMS:
                let s = "";
                for (let i = 0; i < data.rooms.length; i++) {
                    let room = data.rooms[i];
                    s += `<fieldset><legend>Room ${i + 1}</legend>
                    <input type="button" value="Play" data-room="${i}" ${room.s1 && room.s2 ? "disabled='disabled'" : ""} />
                    <input type="button" value="Watch" data-room="${i}" ${!room.s1 || !room.s2 ? "disabled='disabled'" : ""} /></fieldset>`;
                }
                let rooms = document.querySelector("#rooms");
                rooms.innerHTML = s;
                let bPlay = document.querySelectorAll("input[value='Play']");
                bPlay.forEach(b => b.onclick = this.enterRoom.bind(this));
                let bWatch = document.querySelectorAll("input[value='Watch']");
                bWatch.forEach(b => b.onclick = this.watchRoom.bind(this));
                break;
            case ConnectionType.OPEN:
                this.showRoom(true);
                this.player = data.turn;
                this.setMessage("");
                this.clearBoard();
                let msg = document.getElementById("pieceMessage");
                if (this.player === Player.VISITOR) {
                    msg.style.display = "none";
                } else {
                    msg.style.display = "block";
                    this.setPlayerPiece(`images/${this.images[this.player]}`);
                }
                break;
            case ConnectionType.MESSAGE:
                this.printBoard(game.board);
                if (this.player === Player.VISITOR) {
                    this.setMessage(`Current turn: <img src='images/${this.images[game.turn]}' alt=''>`);
                } else {
                    this.setMessage(game.turn === this.player ? "Your turn." : "Opponent's turn.");
                }
                break;
            case ConnectionType.ENDGAME:
                this.printBoard(game.board);
                this.closeConnection(game.winner);
                break;
        }
    }
    setPlayerPiece(url) {
        let img = document.getElementById("playerPiece");
        img.src = url;
    }
    closeConnection(winner) {
        this.unsetEvents();
        if (this.player === Player.VISITOR) {
            if (winner) {
                this.setMessage(`Game Over! ${(winner === Winner.DRAW) ? "Draw!" : `Winner: <img src='images/${this.images[winner]}' alt=''>`}`);
            }
        } else {
            this.setMessage(`Game Over! ${(winner === Winner.DRAW) ? "Draw!" : (winner === this.player ? "You win!" : "You lose!")}`);
        }
    }
    startGame() {
        this.ws = new WebSocket(`ws://${document.location.host}${document.location.pathname}joust`);
        this.ws.onmessage = this.readData.bind(this);
        this.showRoom(false);
    }
    showRoom(show) {
        let rooms = document.querySelector("#rooms");
        let room = document.querySelector("#room");
        if (show) {
            rooms.style.display = "none";
            room.style.display = "grid";
        } else {
            rooms.style.display = "grid";
            room.style.display = "none";
        }
    }
    createBoard() {
        let tbody = document.querySelector("tbody");
        let str = "";
        for (let i = 0; i < 8; i++) {
            str += "<tr>";
            for (let j = 0; j < 8; j++) {
                str += "<td></td>";
            }
        }
        tbody.innerHTML = str;
    }
    init() {
        let button = document.querySelector("#quit");
        button.onclick = this.exitRoom.bind(this);
        this.createBoard();
        this.startGame();
    }
}
let gui = new GUI();
gui.init();