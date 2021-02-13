function GUI() {
    var ws = null;
    var images = {PLAYER1: "Cavalo-Branco.svg", PLAYER2: "Cavalo-Preto.svg"};
    function coordinates(cell) {
        return new Cell(cell.parentNode.rowIndex, cell.cellIndex);
    }
    function setMessage(msg) {
        let message = document.getElementById("message");
        message.innerHTML = msg;
    }
    function play(event) {
        let cellDestino = event.currentTarget;
        let cell = coordinates(cellDestino);
        ws.send(JSON.stringify(cell));
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
                        td.className = "caminhado";
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
    function setButtonText(on) {
        let button = document.querySelector("input[type='button']");
        button.value = (on) ? "Start" : "Quit";
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
    function readData(evt) {
        let data = JSON.parse(evt.data);
        switch (data.type) {
            case "OPEN":
                player = data.turn;
                setMessage("");
                clearBoard();
                setPlayerPiece(`imagens/${images[player]}`);
                break;
            case "MESSAGE":
                printBoard(data.board);
                setMessage(data.turn === player ? "Your turn." : "Opponent's turn.");
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
        setButtonText(true);
        setMessage(`Game Over! ${(winner === "DRAW") ? "Draw!" : (winner === player ? "You win!" : "You lose!")}`);
    }
    function startGame() {
        if (ws) {
            closeConnection(4000);
        } else {
            ws = new WebSocket(`ws://${document.location.host}${document.location.pathname}joust`);
            ws.onmessage = readData;
            setButtonText(false);
        }
    }
    function init() {
        let button = document.querySelector("input[type='button']");
        button.onclick = startGame;
    }
    return {init};
}
onload = function () {
    let gui = new GUI();
    gui.init();
};