package core;

public class Hex {
    private final int x;
    private final int y;
    private boolean isBomb = false;
    boolean opened = false;
    private boolean flagged = false;
    private int bombs; //number of bombs around this hex

    public Hex(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void makeBomb() {
        isBomb = true;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void setBombs (int n) {
        bombs = n;
    }

    public int getBombs () {
       return bombs;
    }

    public int getX() {return x;}
    public int getY() {return y;}

    public boolean isOpened() {
        return opened;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void open() {
        opened = true;
    }

    public void flag() {
        if (!opened) {
            flagged = !flagged;
        }
    }

    public String toString() {
        return ("x " + x + " y " + y);
    }
}
