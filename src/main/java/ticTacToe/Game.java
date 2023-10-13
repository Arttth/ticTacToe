package ticTacToe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private Player player1;
    private Player player2;
    private Board board;
    private Player currentTurnPlayer;
    List<Move> moves;
    //
    private String ip;
    private int port;
    private Socket socket;
    private ServerSocket serverSocket;
    private DataOutputStream dataOutStream;
    private DataInputStream dataInStream;
    boolean isYourTurn = false;

    final int attempt = 10;
    boolean isConnected = false;

    public Game(Player pl1, Player pl2, int boardSize) {
        this.player1 = pl1;
        this.player2 = pl2;
        moves = new ArrayList<Move>();
        currentTurnPlayer = randomPlayer();
        board = new Board(boardSize);
    }

    Player randomPlayer()
    {
        return player1;
    }

    public void Play()
    {
        int coord, x, y;
        int n = board.getBoardSize();
        Scanner sc = new Scanner(System.in);
        while (true)
        {
            printInterface();
            try {
                System.out.println("Введите число от 0 до " + (n * n - 1) + "!");
                coord = sc.nextInt();
                x = coord / n;
                y = coord % n;
                Move curMove = new Move(x, y, currentTurnPlayer);
                moves.add(curMove);
                makeMove(curMove);
                if (checkWinner(curMove) != 0) {
                    printInterface();
                    winNotify();
                    break;
                }
                else if(checkDraw() == 1) {
                    printInterface();
                    drawNotify();
                    break;
                }
            }
            catch (IllegalArgumentException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void PlayOnline()
    {
        if (!isConnected) {
            if(!connect()) {
                startServer();
                listenForServerRequest();
                isYourTurn = true;
            }
        }
        int coord, x, y;
        int n = board.getBoardSize();
        Scanner sc = new Scanner(System.in);
        // данные о сопернике

        while (true)
        {
            printInterface();
            try {
                if (isYourTurn) {
                    System.out.println("Введите число от 0 до " + (n * n - 1) + "!");
                    coord = sc.nextInt();
                    int i = 0;
                    while (true) {
                        try {
                            dataOutStream.writeInt(coord);
                            dataOutStream.flush();
                            isYourTurn = false;
                            break;
                        }
                        catch (Exception e)
                        {
                            i++;
                            e.printStackTrace();
                            if(i > attempt)
                                System.out.println("Невозможно передать данные!");
                                return;
                        }
                    }
                }
                else {
                    System.out.println("Дождитесь хода соперника!");
                    int i = 0;
                    while (true) {
                        try {
                            System.out.println("До получения");
                            coord = dataInStream.readInt();
                            System.out.println("После получения");
                            isYourTurn = true;
                            break;
                        }
                        catch (Exception e)
                        {
                            i++;
                            e.printStackTrace();
                            if(i > attempt)
                                System.out.println("Невозможно получить данные!");
                            return;
                        }
                    }
                }

                x = coord / n;
                y = coord % n;
                Move curMove = new Move(x, y, currentTurnPlayer);
                moves.add(curMove);
                makeMove(curMove);
                if (checkWinner(curMove) != 0) {
                    printInterface();
                    winNotify();
                    break;
                }
                else if(checkDraw() == 1) {
                    printInterface();
                    drawNotify();
                    break;
                }
            }
            catch (IllegalArgumentException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        try {
            if (isConnected)
                socket.close();
            serverSocket.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    void winNotify()
    {
        System.out.println("Игрок " + moves.get(moves.size()-1).getPlayer().getName() + " победил!");
    }

    void drawNotify()
    {
        System.out.println("Боевая ничья!");
    }


    /**
     * @param cur coordinates of move and player
     * @return true if move is done successfully and false if it is failed
     */
    void makeMove(Move cur)
    {
        int n = board.getBoardSize();
        int x = cur.getX();
        int y = cur.getY();
        if (x >= n || y >= n || x < 0 || y < 0)
            throw new IllegalArgumentException("Move out of board!");
        if (board.getCellValue(x, y) != 0)
            throw new IllegalArgumentException("Cell is occupied!");

        board.markCell(x, y, cur.getPlayer().getId());
        if (currentTurnPlayer == player1)
            currentTurnPlayer = player2;
        else
            currentTurnPlayer = player1;
    }

    /**
     *
     * @param cur coordinates of move and player
     * @return winner +1 if player1 win, -1 if player2 and 0 otherwise
     */
    int checkWinner(Move cur)
    {
        int playerId = cur.getPlayer().getId();
        boolean winRow = true, winCol = true, winDiag = true, winRevDiag = true;
        for (int i = 0; i < board.getBoardSize(); ++i) {
            if (board.getCellValue(cur.getX(), i) != playerId)
                winRow = false;
            if (board.getCellValue(i, cur.getY()) != playerId)
                winCol = false;
            if (board.getCellValue(i, i) != playerId)
                winDiag = false;
            if (board.getCellValue(i, board.getBoardSize() - 1 - i) != playerId)
                winRevDiag = false;
        }
        if (winRow || winCol || winDiag || winRevDiag)
            return playerId;
        return 0;
    }

    int checkDraw()
    {
        for (int i = 0; i < board.getBoardSize(); ++i){
            for (int j = 0; j < board.getBoardSize(); ++j){
                if (board.getCellValue(i, j) == 0)
                    return 0;
            }
        }
        return 1;
    }

    void printInterface()
    {
        StringBuilder topBottomBoundary = new StringBuilder("");
        String outCell;

        for (int i = 0; i < board.getBoardSize(); ++i) {
            topBottomBoundary.append("+---");
        }
        topBottomBoundary.append("+");

        for (int[] row : board.getCells()) {
            System.out.println(topBottomBoundary);

            for (int cell : row) {
                if (cell == 0) {
                    outCell = " ";
                }
                else if (cell == player1.getId()) {
                    outCell = player1.getChecker();
                }
                else {
                    outCell = player2.getChecker();
                }
                System.out.print("| " + outCell + " ");
            }
            System.out.println("|");
        }
        System.out.println(topBottomBoundary);
        System.out.println();
    }

    public void setBoardSize(int boardSize)
    {
        board.setBoardSize(boardSize);
    }

    boolean connect()
    {
        try {
            socket = new Socket(ip, port);
            dataOutStream = new DataOutputStream(socket.getOutputStream());
            dataInStream = new DataInputStream(socket.getInputStream());
            System.out.println("Соединение установлено!");
            isConnected = true;
            return true;
        }
        catch (IOException ex)
        {
            System.out.println("Нет соединения с " + ip + ":" + port);
            System.out.println("Попытка создать сервер!");
            return false;
        }
    }

    void startServer()
    {
        try {
            serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
            currentTurnPlayer = player1;
        }
        catch (Exception ex) {
            System.out.println("Не удалось создать сервер!");
            ex.printStackTrace();
        }
    }

    private void listenForServerRequest() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            dataOutStream= new DataOutputStream(socket.getOutputStream());
            dataInStream = new DataInputStream(socket.getInputStream());
            System.out.println("Соединение установлено!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getIp()
    {
        return ip;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getPort()
    {
        return port;
    }
}
