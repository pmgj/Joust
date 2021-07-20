import {Cell} from "./Cell.js";

function GUI() {
    let ws = null;
    let images = {PLAYER1: "Cavalo-Branco.svg", PLAYER2: "Cavalo-Preto.svg"};
    let player;
    let msgs = {QUIT_GAME: "Quit game", EXIT_ROOM: "Exit room"};
    function coordinates(cell) {
        return new Cell(cell.parentNode.rowIndex, cell.cellIndex);
    }
    function setMessage(msg) {
        let message = document.getElementById("message");
        message.innerHTML = msg;
    }
    function play(event) {
        let cellDestino = event.currentTarget;
        let dCell = coordinates(cellDestino);
        ws.send(JSON.stringify({type: "MESSAGE", cell: dCell}));
    }
    function printBoard(matrix) {
        let table = document.querySelector("table");
        for (let i = 0; i < matrix.length; i++) {
            for (let j = 0; j < matrix[i].length; j++) {
                let td = table.rows[i].cells[j];
                td.innerHTML = "";
                td.className = "";
                td.onclick = play;
                switch (matrix[i][j]) {
                    case "BLOCKED":
                        td.className = "blocked";
                        td.innerHTML = "X";
                        break;
                    case "PLAYER1":
                    case "PLAYER2":
                        td.innerHTML = `<img src='imagens/${images[matrix[i][j]]}' alt=''>`;
                        break;
                }
            }
        }
    }
    function setButtonText(message) {
        let button = document.querySelector("#quit");
        button.value = message;
    }
    function clearBoard() {
        let cells = document.querySelectorAll("td");
        cells.forEach(td => {
            td.innerHTML = "";
            td.className = "";
            td.onclick = undefined;
        });
    }
    function unsetEvents() {
        let cells = document.querySelectorAll("td");
        cells.forEach(td => td.onclick = undefined);
    }
    function enterRoom() {
        let obj = {type: "ENTER_ROOM", room: parseInt(this.dataset.room)};
        ws.send(JSON.stringify(obj));
    }
    function watchRoom() {
        let obj = {type: "WATCH_ROOM", room: parseInt(this.dataset.room)};
        ws.send(JSON.stringify(obj));
    }
    function readData(evt) {
        let data = JSON.parse(evt.data);
        switch (data.type) {
            case "GET_ROOMS":
                let s = "";
                for (let i = 0; i < data.rooms.length; i++) {
                    let room = data.rooms[i];
                    s += `<fieldset><legend>Room ${i + 1}</legend>
                    <input type="button" value="Play" data-room="${i}" ${room.s1 && room.s2 ? "disabled='disabled'" : ""} />
                    <input type="button" value="Watch" data-room="${i}" /></fieldset>`;
                }
                let rooms = document.querySelector("#rooms");
                rooms.innerHTML = s;
                let bPlay = document.querySelectorAll("input[value='Play']");
                bPlay.forEach(b => b.onclick = enterRoom);
                let bWatch = document.querySelectorAll("input[value='Watch']");
                bWatch.forEach(b => b.onclick = watchRoom);
                break;
            case "OPEN":
                showRoom(true);
                player = data.turn;
                setMessage("");
                clearBoard();
                let msg = document.getElementById("pieceMessage");
                if (player === "VISITOR") {
                    msg.style.display = "none";
                } else {
                    msg.style.display = "block";
                    setPlayerPiece(`imagens/${images[player]}`);
                }
                break;
            case "MESSAGE":
                printBoard(data.board);
                if (player === "VISITOR") {
                    setMessage(`Current turn: <img src='imagens/${images[data.turn]}' alt=''>`);
                } else {
                    setMessage(data.turn === player ? "Your turn." : "Opponent's turn.");
                }
                break;
            case "ENDGAME":
                printBoard(data.board);
                closeConnection(1000, data.winner);
                break;
        }
    }
    function setPlayerPiece(url) {
        let img = document.getElementById("playerPiece");
        img.src = url;
    }
    function closeConnection(closeCode, winner) {
        unsetEvents();
        ws.close(closeCode);
        ws = null;
        setButtonText(msgs["EXIT_ROOM"]);
        if (player === "VISITOR") {
            if (winner) {
                setMessage(`Game Over! ${(winner === "DRAW") ? "Draw!" : `Winner: <img src='imagens/${images[winner]}' alt=''>`}`);
            }
        } else {
            setMessage(`Game Over! ${(winner === "DRAW") ? "Draw!" : (winner === player ? "You win!" : "You lose!")}`);
        }
    }
    function startGame() {
        if (ws) {
            closeConnection(4000);
        } else {
            ws = new WebSocket(`ws://${document.location.host}${document.location.pathname}joust`);
            ws.onmessage = readData;
            setButtonText(msgs["QUIT_GAME"]);
            showRoom(false);
        }
    }
    function showRoom(show) {
        let rooms = document.querySelector("#rooms");
        let room = document.querySelector("#room");
        if (show) {
            rooms.style.display = "none";
            room.style.display = "block";
        } else {
            rooms.style.display = "block";
            room.style.display = "none";
        }
    }
    function exit() {
        ws.close();
        ws = null;
    }
    function init() {
        let button = document.querySelector("#quit");
        button.onclick = startGame;
        window.onbeforeunload = exit;
        startGame();
    }
    return {init};
}
let gui = new GUI();
gui.init();