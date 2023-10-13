package org.example;

import ticTacToe.Game;
import ticTacToe.Player;
import ticTacToe.Statistic;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String name, checker;
        int id;
        int menuChoice;
        int boardSize = 3;
        Player pl2 = new Player(2, "Player2", new Statistic(), "O");
        Player pl1 = new Player(1, "Player1", new Statistic(), "X");
        Game ticTacToe1 = new Game(pl1, pl2, boardSize);
        while (true)
        {
            System.out.println("1 - Играть на одно ПК");
            System.out.println("2 - Выбрать размер");
            System.out.println("3 - Играть онлайн");
            System.out.println("4 - Выйти");
            menuChoice = sc.nextInt();
            if (menuChoice == 1) {
                ticTacToe1.Play();
            }
            else if (menuChoice == 2) {/*
                System.out.println("Введите размер:");
                boardSize = sc.nextInt();
                ticTacToe1.setBoardSize(menuChoice);
                */
            }
            else if (menuChoice == 3) {
                System.out.println("Введите ip:");
                ticTacToe1.setIp(sc.next());
                System.out.println("Введите номер порта:");
                ticTacToe1.setPort(sc.nextInt());
                ticTacToe1.PlayOnline();
            }
            else if (menuChoice == 4) {
                System.out.println("Спасибо за игру!");
                return;
            }
        }
    }
}