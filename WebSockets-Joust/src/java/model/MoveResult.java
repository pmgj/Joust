package model;

public class MoveResult {
    private Move move;
    private Winner winner;

    public MoveResult(Move move) {
        this.move = move;
    }
    
    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Winner getWinner() {
        return winner;
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }
}
