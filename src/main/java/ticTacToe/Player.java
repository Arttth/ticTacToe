package ticTacToe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private int id;
    private  String name;
    private Statistic statistic;
    private String checker;
    public Player(int id, String name, Statistic statistic, String checker)
    {
        this.id = id;
        this.name = name;
        this.statistic = statistic;
        this.checker = checker;
    }
}
