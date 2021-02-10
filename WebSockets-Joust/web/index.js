function GUI() {
    var ws = null;
    var images = {PLAYER1: "Cavalo-Branco.svg", PLAYER2: "Cavalo-Preto.svg"};
    function printBoard(matrix) {
        let table = document.querySelector("table");
        for (let i = 0; i < matrix.length; i++) {
            for (let j = 0; j < matrix[i].length; j++) {
                let td = table.rows[i].cells[j];
                td.innerHTML = "";
                td.className = "";
                switch (matrix[i][j]) {
                    case "PLAYER1":
                    case "PLAYER2":
                        td.innerHTML = `<img src='imagens/${images[matrix[i][j]]}' alt=''>`;
                        break;
                }
            }
        }
    }
    function readData(evt) {
        let data = JSON.parse(evt.data);
        printBoard(data.board);
    }
    function startGame() {
        if (ws === null) {
            ws = new WebSocket(`ws://${document.location.host}${document.location.pathname}joust`);
            ws.onmessage = readData;
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