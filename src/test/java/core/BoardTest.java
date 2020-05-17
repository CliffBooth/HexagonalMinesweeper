package core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void getNeighbours() {
        Board board = new Board(3, 3, 0);
        Hex[][] grid = board.getGrid();

        Hex hex = grid[1][1];
        Hex[] check = new Hex[]{grid[0][1], grid[1][0], grid[2][1],  grid[2][2], grid[1][2], grid[0][2]};
        assertArrayEquals(check, board.getNeighbours(hex).toArray());

        hex = grid[1][0];
        check = new Hex[] {grid[0][0], grid[2][0], grid[2][1],  grid[1][1], grid[0][1]};
        assertArrayEquals(check, board.getNeighbours(hex).toArray());

        hex = grid[2][1];
        check = new Hex[] {grid[1][0], grid[2][0], grid[2][2],  grid[1][1]};
        assertArrayEquals(check, board.getNeighbours(hex).toArray());
    }

    @Test
    void surroundingBombs() {
        Board board = new Board(3, 3, 0);
        Hex[][] grid = board.getGrid();
        Hex hex = grid[1][1];
        grid[0][1].makeBomb();
        grid[1][0].makeBomb();
        grid[2][1].makeBomb();
        assertEquals(3, board.surroundingBombs(hex));
        hex = grid[0][0];
        assertEquals(2, board.surroundingBombs(hex));
        hex = grid[2][2];
        assertEquals(1, board.surroundingBombs(hex));
    }

    @Test
    void open() {
        Board board = new Board(3, 3, 0);
        Hex[][] grid = board.getGrid();
        grid[1][0].makeBomb();
        board.open(grid[1][2]);
        assertTrue(grid[1][2].isOpened());
        assertTrue(grid[1][1].isOpened());
        assertTrue(grid[0][2].isOpened());
        assertTrue(grid[0][1].isOpened());
        assertTrue(grid[2][2].isOpened());
        assertTrue(grid[2][1].isOpened());
        assertFalse(grid[0][0].isOpened());
        assertFalse(grid[2][0].isOpened());
    }

    @Test
    void plantBombs() {
        Board board = new Board (3,3,5);
        Hex[][] grid = board.getGrid();

        //board currently has no bombs
        int counter = 0;
        for (int x = 0; x<3; x++) {
            for (int y = 0; y<3; y++) {
                Hex hex = grid[x][y];
                if (hex.isBomb())
                    counter++;
            }
        }
        assertEquals(0, counter);

        Hex h = grid[1][1];
        board.plantBombs(h);
        //now there are 5 bombs
        for (int x = 0; x<3; x++) {
            for (int y = 0; y<3; y++) {
                Hex hex = grid[x][y];
                if (hex.isBomb())
                    counter++;
            }
        }
        assertEquals(5, counter);
        assertFalse(h.isBomb());
    }
}