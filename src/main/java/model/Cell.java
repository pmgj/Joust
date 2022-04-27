package model;

public class Cell {

    private int x;
    private int y;

    public Cell() {

    }

    public Cell(int row, int col) {
        this.x = row;
        this.y = col;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            Cell cell = (Cell) obj;
            return (cell.x == this.x && cell.y == this.y);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.x;
        hash = 29 * hash + this.y;
        return hash;
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int row) {
        this.x = row;
    }

    public void setY(int col) {
        this.y = col;
    }
}
