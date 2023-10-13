package ticTacToe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    private int[][] cells;
    private int boardSize;

    public Board(int boardSize)
    {
        this.boardSize = boardSize;
        cells = new int[boardSize][boardSize];
    }

    void markCell(int x, int y, int playerId)
    {
        cells[x][y] = playerId;
    }

    int getCellValue(int x, int y)
    {
        if (x >= boardSize || y >= boardSize || x < 0 || y < 0)
            throw new IndexOutOfBoundsException("Coordinates out of bound board!");
        return cells[x][y];
    }
}
