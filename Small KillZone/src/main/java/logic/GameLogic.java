package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class GameLogic {

    public int[] firstPosition = {0, 0};
    public int[] secondPosition = {0, 0};

    private final int[][] boardServer = {
            {2, 2, 2, 2, 2},
            {2, 2, 2, 2, 2},
            {2, 2, 2, 2, 2},
            {2, 2, 2, 2, 2},
            {2, 2, 2, 2, 2}
    };

    public Player currentPlayer;

    public boolean hasWinner() {
       if(currentPlayer.life == false) {
           return true;
       }
       return false;
    }

    public GameLogic() {
        createBoard(boardServer);
    }


    public int[][] createBoard(int[][] boardServer){

        Random random = new Random();
        int cell2 =  random.nextInt(5)+4;
        int cell1 = random.nextInt(2)+3;

        for (int i = 0; i < 5 ; i++) {
            boardServer[2][i]=random.nextInt(3)+1;
            if(boardServer[2][i] == 3 ){
                if (cell2 == 0){
                    boardServer[2][i]=2;
                } else {
                    cell2 = cell2 - 1;
                }
            }

            if (boardServer[2][i] == 1){
                if (cell1 == 0){
                    boardServer[2][i]=2;
                } else {
                    cell1 = cell1 - 1;
                }
            }

            boardServer[0][i]=random.nextInt(3)+1;
            if(boardServer[0][i] == 3 ){
                if (cell2 == 0){
                    boardServer[0][i]=2;
                } else {
                    cell2 = cell2 - 1;
                }
            }
            if (boardServer[0][i] == 1){
                if (cell1 == 0){
                    boardServer[0][i]=2;
                } else {
                    cell1 = cell1 - 1;
                }
            }

            boardServer[4][i]=random.nextInt(3)+1;
            if(boardServer[4][i] == 3 ){
                if (cell2 == 0){
                    boardServer[4][i]=2;
                } else {
                    cell2 = cell2 - 1;
                }
            }

            if (boardServer[4][i] == 1){
                if (cell1 == 0){
                    boardServer[4][i]=2;
                } else {
                    cell1 = cell1 - 1;
                }
            }

            boardServer[1][i]=random.nextInt(3)+1;
            if(boardServer[1][i] == 3 ){
                if (cell2 == 0){
                    boardServer[1][i]=2;
                } else {
                    cell2 = cell2 - 1;
                }
            }

            if (boardServer[1][i] == 1){
                if (i == 1 && boardServer[0][1] == 1) {
                    boardServer[0][1]= 3;
                } else if ( boardServer[0][2] == 1 && i == 1 ){
                    boardServer[0][2]= 3;
                } else  if ( boardServer[0][2]==1 && i==3){
                    boardServer[0][2] = 3;
                } else if (boardServer[0][3]==1 && i==3){
                    boardServer[0][3]=3;
                }
                if (cell1 == 0){
                    boardServer[1][i]=2;
                } else {
                    cell1 = cell1 - 1;
                }
            }

            boardServer[3][i]=random.nextInt(3)+1;
            if(boardServer[3][i] == 3 ){
                if (cell2 == 0){
                    boardServer[3][i]=2;
                } else {
                    cell2 = cell2 - 1;
                }
            }

            if (boardServer[3][i] == 1){
                if (i == 1 && boardServer[4][1] == 1) {
                    boardServer[4][1]= 3;
                } else if ( boardServer[4][2] == 1 && i == 1 ){
                    boardServer[4][2]= 3;
                } else  if ( boardServer[4][2]==1 && i==3){
                    boardServer[4][2] = 3;
                } else if (boardServer[4][3]==1 && i==3){
                    boardServer[4][3]=3;
                }
                if (cell1 == 0){
                    boardServer[3][i]=2;
                } else {
                    cell1 = cell1 - 1;
                }
            }
        }
        boardServer = randomPlaceMap(boardServer, 5, firstPosition);
        boardServer = randomPlaceMap(boardServer, 6, secondPosition);

        return boardServer;

    }

    public void printMap() {
        for(int i = 0; i < boardServer.length; i++) {
            for(int k = 0; k < boardServer[i].length; k++) {
                System.out.print("["+boardServer[i][k]+"]");
            }
            System.out.println();
        }
    }

    public int[][] randomPlaceMap(int[][] serverBoard,int mark, int[] position){
        Random random=new Random();
        int x = random.nextInt(5);
        int y = random.nextInt(5);
        position[1] = x;
        position[0] = y;

        if(serverBoard[x][y]==2){
            serverBoard[x][y] = mark+2;
            return serverBoard;
        } else if(serverBoard[x][y]==7) {
            serverBoard[x][y] = mark + 7;
            return serverBoard;
        } else{
            return randomPlaceMap(serverBoard, mark, position);
        }
    }

    public class Player extends Thread {

        public int mark;
        public int x;
        public int y;
        boolean life = true;
        boolean step = true;
        boolean bomb = true;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;
        int[][] boardClient =
                {{0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0},
                 {0, 0, 0, 0, 0, 0, 0, 0, 0}};

        public Player(Socket socket, int mark, int x, int y) throws IOException {
            this.socket = socket;
            this.mark = mark;
            this.x = x;
            this.y = y;
            try {
                input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);
                output.println("WELCOME " + mark);
                output.println("MESSAGE Wait for your opponent");
            } catch (IOException e) {
                System.out.println("Player quit " + e);
            }
        }

        public synchronized void moveTop(Player player) throws IOException {
            if (isLegalMove(boardServer, player, x, y-1)) {
                boardServer[y][x] -= mark;
                boardServer[y - 1][x] += mark;
                currentPlayer.output.println("VALID_MOVE " + boardServer[y][x]+":"+"up");
                currentPlayer.opponent.otherPlayerMoved(boardServer[y][x]+":"+"up");
                y--;
            }
        }

        public synchronized void moveDown(Player player){
            if (isLegalMove(boardServer, player, x, y + 1)) {
                boardServer[y][x] -= mark;
                boardServer[y + 1][x] += mark;
                currentPlayer.output.println("VALID_MOVE " + boardServer[y][x]+":"+"down");
                currentPlayer.opponent.otherPlayerMoved(boardServer[y][x]+":"+"down");
                y++;
            }
        }

        public synchronized void moveLeft(Player player){
            if (isLegalMove(boardServer, player, x-1, y)) {
                boardServer[y][x] -= mark;
                boardServer[y][x-1] += mark;
                currentPlayer.output.println("VALID_MOVE " + boardServer[y][x]+":"+"left");
                currentPlayer.opponent.otherPlayerMoved(boardServer[y][x]+":"+"left");
                x--;
            }
        }

        public synchronized void moveRight(Player player){
            if (isLegalMove(boardServer, player, x+1, y)) {
                boardServer[y][x] -= mark;
                boardServer[y][x+1] += mark;
                currentPlayer.output.println("VALID_MOVE " + boardServer[y][x]+":"+"right");
                currentPlayer.opponent.otherPlayerMoved(boardServer[y][x]+":"+"right");
                x++;
            }
        }

        public synchronized boolean isLegalMove(int[][] serverBoard, Player player , int x, int y) {
            if (player == currentPlayer) {
                if (y >= 0 && y < serverBoard.length && x >= 0 && x < serverBoard[y].length) {
                    switch (serverBoard[y][x]) {
                        case 2:
                        case 4:
                        default:
                            step = false;
                            return true;
                        case 1:
                        case 3:
                            output.println("INVALID_MOVE You can not move");
                            return false;
                    }
                } else {
                    output.println("INVALID_MOVE You can not move");
                    return false;
                }
            } else {
                player.otherPlayerMove();
                return false;
            }
        }


        public boolean isLegalToBlew(Player player, int x, int y, String direction) {
            if (player == currentPlayer) {
                if (y >= 0 && y < boardServer.length && x >= 0 && x < boardServer[y].length) {
                    switch (boardServer[y][x]){
                        case 2:
                            output.println("BLEW 2:" + direction);
                            currentPlayer = currentPlayer.opponent;
                            currentPlayer.step = true;
                            currentPlayer.opponent.otherPlayerMove();
                            currentPlayer.otherPlayerBombed(direction, boardServer[y][x]);
                            return false;
                        case 3:
                            output.println("BLEW 4:"+ direction);
                            currentPlayer = currentPlayer.opponent;
                            currentPlayer.step = true;
                            boardServer[y][x] = 4;
                            currentPlayer.opponent.otherPlayerMove();
                            currentPlayer.otherPlayerBombed(direction, boardServer[y][x]);
                            return true;
                        case 1:
                            output.println("BLEW 1:"+ direction);
                            currentPlayer = currentPlayer.opponent;
                            currentPlayer.step = true;
                            currentPlayer.opponent.otherPlayerMove();
                            currentPlayer.otherPlayerBombed(direction, boardServer[y][x]);
                            return false;
                        case 4:
                            output.println("BLEW 4:"+ direction);
                            currentPlayer = currentPlayer.opponent;
                            currentPlayer.step = true;
                            currentPlayer.opponent.otherPlayerMove();
                            currentPlayer.otherPlayerBombed(direction, boardServer[y][x]);
                            return false;
                        default:
                            currentPlayer.opponent.life=false;
                            currentPlayer.opponent.otherPlayerYouBombed(direction);
                            output.println("BLEW "+currentPlayer.mark+2+":"+ direction);
                            return true;
                    }
                } else {
                    output.println("BLEW 1:"+direction);
                    currentPlayer = currentPlayer.opponent;
                    currentPlayer.step = true;
                    currentPlayer.opponent.otherPlayerMove();
                    currentPlayer.otherPlayerBombed(direction, 1);
                    return false;
                }
            } else {
                player.otherPlayerMove();
                return false;
            }
        }

        public void blowUpTop(Player player){
            if(isLegalToBlew(player, x, y-1, "up")) {
                boardServer[y-1][x] = 4;
            }
        }

        public void blowUpDown(Player player){
            if(isLegalToBlew(player, x, y+1, "down")) {
                boardServer[y+1][x] = 4;
            }
        }

        public void blowUpLeft(Player player){
            if(isLegalToBlew(player, x-1, y, "left")) {
                boardServer[y][x-1] = 4;
            }
        }

        public void blowUpRight(Player player){
            if(isLegalToBlew(player, x+1, y, "right")) {
                boardServer[y][x+1] = 4;
            }
        }



        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }


        public void otherPlayerMoved(String direction) {
            output.println("OPPONENT_MOVED " + direction);
        }

        public void otherPlayerMove() { output.println("OPPONENT_MOVE Opponent's turn. Please, wait.");}

        public void otherPlayerBombed(String direction, int code) {output.println("OPPONENT_BOMBED "+code+":"+direction);}

        public void otherPlayerYouBombed(String direction) {
            output.println("YOU_BOMBING "+direction);
        }

        public void run() {
            String command;
            try {
                // The thread is only started after everyone connects.
                output.println("GAME_STARTED All players connected. Game started");

                // Tell the first player that it is her turn.
                if (mark == 5) {
                    output.println("MESSAGE Your turn");
                    currentPlayer.opponent.otherPlayerMove();
                }

                // Repeatedly get commands from the client and process them.
                while (true) {
                    command = input.readLine();
                    if(command != null) {
                        if (command.startsWith("MOVE")) {
                            if (currentPlayer.step) {
                                String direction = command.substring(5);
                                if (direction.equals("up")) {
                                    moveTop(this);
                                } else if (direction.equals("down")) {
                                    moveDown(this);
                                } else if (direction.equals("left")) {
                                    moveLeft(this);
                                } else if (direction.equals("right")) {
                                    moveRight(this);
                                }
                                currentPlayer.step = false;
                            } else {
                                output.println("You already done your turn.");
                            }
                        } else if (command.startsWith("BLEW")) {
                            String direction = command.substring(5);
                            if (direction.equals("up")) {
                                blowUpTop(this);
                            } else if (direction.equals("down")) {
                                blowUpDown(this);
                            } else if (direction.equals("left")) {
                                blowUpLeft(this);
                            } else if (direction.equals("right")) {
                                blowUpRight(this);
                            }
                        } else if (command.startsWith("ESCAPE")) {
                            currentPlayer.otherPlayerMove();
                            currentPlayer = currentPlayer.opponent;
                            currentPlayer.step = true;
                            currentPlayer.output.println("YOU_MOVE");
                        } else if (command.startsWith("QUIT")) {
                            break;
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Player quit: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
        }

    }

     @Override
     public String toString() {
         if (currentPlayer != null) {
             return currentPlayer.mark + " won";
         } else {
             return "Game continues";
         }
     }

    public int[][] getBoardServer() {
        return boardServer;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
