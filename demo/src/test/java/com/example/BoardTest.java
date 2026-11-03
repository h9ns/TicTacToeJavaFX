package com.example;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for board state, move validation, and win detection. */
class BoardTest {
    private Board board;

    /** Creates a fresh board before each test. */
    @BeforeEach
    void setUp() {
        board = new Board();
    }

    /** Verifies a new board is empty and displays the expected layout. */
    @Test
    void startsEmptyAndFormatsTheBoard() {
        assertFalse(board.isFull());
        assertFalse(board.checkWin());
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertEquals(' ', board.getCell(row, column));
            }
        }
        assertEquals(
                "  1   2   3\n"
                        + "1   |   |  \n"
                        + "  -----------\n"
                        + "2   |   |  \n"
                        + "  -----------\n"
                        + "3   |   |  \n",
                board.format());
    }

    /** Verifies display writes the formatted board to standard output. */
    @Test
    void displaysFormattedBoard() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        try {
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            board.display();
        } finally {
            System.setOut(originalOutput);
        }
        assertEquals(board.format() + System.lineSeparator(), output.toString(StandardCharsets.UTF_8));
    }

    /** Verifies only empty, in-bounds cells accept X and O markers. */
    @Test
    void acceptsOnlyEmptyInBoundsCellsAndPlacesValidMarkers() {
        assertTrue(board.isValid(0, 0));
        assertFalse(board.isValid(-1, 0));
        assertFalse(board.isValid(3, 0));
        assertFalse(board.isValid(0, -1));
        assertFalse(board.isValid(0, 3));
        assertTrue(board.place(0, 0, 'X'));
        assertFalse(board.isValid(0, 0));
        assertFalse(board.place(0, 0, 'O'));
        assertFalse(board.place(-1, 0, 'O'));
        assertFalse(board.place(3, 0, 'O'));
        assertFalse(board.place(0, -1, 'O'));
        assertFalse(board.place(0, 3, 'O'));
        assertFalse(board.place(1, 1, 'Z'));
        assertFalse(board.place(1, 1, ' '));
        assertTrue(board.place(1, 1, 'O'));
        assertEquals('X', board.getCell(0, 0));
        assertEquals('O', board.getCell(1, 1));
    }

    /** Verifies every row, column, and diagonal is recognized as a winning line. */
    @Test
    void detectsWinsAcrossEveryRowColumnAndDiagonal() {
        for (int row = 0; row < 3; row++) {
            assertPlacesAndWins(new int[][]{{row, 0}, {row, 1}, {row, 2}});
        }
        for (int column = 0; column < 3; column++) {
            assertPlacesAndWins(new int[][]{{0, column}, {1, column}, {2, column}});
        }
        assertPlacesAndWins(new int[][]{{0, 0}, {1, 1}, {2, 2}});
        assertPlacesAndWins(new int[][]{{0, 2}, {1, 1}, {2, 0}});
    }

    /** Verifies non-winning positions and a full board draw are recognized. */
    @Test
    void detectsNoWinAndFullBoardDraw() {
        assertFalse(board.checkWin());
        assertTrue(board.place(0, 0, 'X'));
        assertTrue(board.place(0, 1, 'O'));
        assertFalse(board.checkWin());

        board.initialize();
        char[][] marks = {{'X', 'O', 'X'}, {'X', 'O', 'O'}, {'O', 'X', 'X'}};
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertTrue(board.place(row, column, marks[row][column]));
            }
        }
        assertTrue(board.isFull());
        assertFalse(board.checkWin());
    }

    /** Verifies the board becomes full only after all nine cells are occupied. */
    @Test
    void becomesFullAfterAllCellsAreOccupied() {
        char[][] marks = {{'X', 'O', 'X'}, {'X', 'O', 'O'}, {'O', 'X', 'X'}};
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if (row != 2 || column != 2) {
                    assertTrue(board.place(row, column, marks[row][column]));
                    assertFalse(board.isFull());
                }
            }
        }
        assertTrue(board.place(2, 2, marks[2][2]));
        assertTrue(board.isFull());
    }

    /** Verifies initialization clears every cell, including a full winning board. */
    @Test
    void initializeClearsTheBoard() {
        for (int column = 0; column < 3; column++) {
            assertTrue(board.place(0, column, 'O'));
        }
        assertTrue(board.checkWin());
        board.initialize();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                assertEquals(' ', board.getCell(row, column));
                assertTrue(board.isValid(row, column));
            }
        }
        assertFalse(board.isFull());
        assertFalse(board.checkWin());
    }

    /** Verifies invalid coordinates are rejected when reading a cell. */
    @Test
    void rejectsOutOfBoundsCellReads() {
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(0, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(0, 3));
    }

    /** Places the supplied cells for X and verifies that they form a win. */
    private void assertPlacesAndWins(int[][] cells) {
        board.initialize();
        for (int[] cell : cells) {
            assertTrue(board.place(cell[0], cell[1], 'O'));
        }
        assertTrue(board.checkWin());
    }
}