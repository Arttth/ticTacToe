package ticTacToe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Move {
    private int x;
    private int y;
    private Player player;
    public Move(int x, int y, Player player)
    {
        this.x = x;
        this.y = y;
        this.player = player;
    }
}
