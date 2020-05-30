package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private final int XTiles;
    private final int YTiles;
    private final int bombs;
    private int flags;

    private final Hex[][] grid;
    private boolean lose;
    private boolean victory;
    private boolean isItFirstTurn = true;


    public Board(int x, int y, int bombs) {
        this.XTiles = x;
        this.YTiles = y;
        this.bombs = bombs;
        flags = bombs;
        grid = new Hex[XTiles][YTiles];
        for (int i = 0; i < XTiles; i++) {
            for (int j = 0; j < YTiles; j++) {
                Hex hex = new Hex(i, j);
                grid[i][j] = hex;
            }
        }
    }


    public List<Hex> getNeighbours(Hex hex) {
        return this.getNeighbours(hex.getX(), hex.getY());
    }

    public List<Hex> getNeighbours(int x, int y) {
        List<Hex> neighbours = new ArrayList<>();
        int[] points;
        if (x % 2 == 0) {
            points = new int[]{
                    -1, -1,
                    0, -1,
                    1, -1,
                    1, 0,
                    0, 1,
                    -1, 0
            };
        } else {
            points = new int[]{
                    -1, 0,
                    0, -1,
                    1, 0,
                    1, 1,
                    0, 1,
                    -1, 1
            };
        }
        for (int i = 0; i < points.length; i++) {
            int newX = x + points[i];
            int newY = y + points[++i];
            if (newX >= 0 & newX < XTiles & newY >= 0 & newY < YTiles)
                neighbours.add(grid[newX][newY]);
        }
        return neighbours;
    }

    public int surroundingBombs(Hex hex) {
        List<Hex> neighbours = getNeighbours(hex);
        int result = 0;
        for (Hex h : neighbours)
            if (h.isBomb())
                result++;
        return result;
    }

    public void open(Hex hex) {
        if (hex.isOpened())
            return;
        if (hex.isFlagged())
            flags++;
        if (isItFirstTurn) {
            plantBombs(hex);
        }
        if (hex.isBomb()) {
            endGame();
            return;
        }
        hex.open();
        if (hex.getBombs() == 0)
            for (Hex h : this.getNeighbours(hex)) {
                this.open(h);
            }
        checkWin();
    }

    void plantBombs(Hex hex) {
        isItFirstTurn = false;
        hex.open();
        List<Hex> notBombs = new ArrayList<>();

        for (int i = 0; i < XTiles; i++) {
            for (int j = 0; j < YTiles; j++) {
                Hex h = grid[i][j];
                if (!h.isOpened())
                    notBombs.add(h);
            }
        }

        for (int i = 0; i < bombs; i++) {
            Random random = new Random();
            int n = random.nextInt(notBombs.size());
            Hex h = notBombs.get(n);
            h.makeBomb();
            notBombs.remove(h);
        }

        for (int i = 0; i < XTiles; i++) {
            for (int j = 0; j < YTiles; j++) {
                Hex h = grid[i][j];
                h.setBombs(this.surroundingBombs(h));
            }
        }
    }

    private void endGame() {
        lose = true;
        for (int i = 0; i < XTiles; i++) {
            for (int j = 0; j < YTiles; j++) {
                Hex hex = grid[i][j];
                if (hex.isBomb()) {
                    hex.open();
                }
            }
        }
    }

    private void checkWin() {
        for (int x = 0; x < XTiles; x++) {
            for (int y = 0; y < YTiles; y++) {
                if (!grid[x][y].isOpened() && !grid[x][y].isBomb())
                    return;
            }
        }
        victory = true;
    }

    public void flag(Hex hex) {
        if (!hex.isFlagged() && flags != 0) {
            hex.flag();
            flags--;
        } else if (hex.isFlagged()) {
            hex.flag();
            flags++;
        }
    }

    public int getFlags() {
        return flags;
    }

    public boolean getLose() {
        return lose;
    }

    public boolean getVictory() {
        return victory;
    }

    public Hex[][] getGrid() {
        return grid;
    }

}
