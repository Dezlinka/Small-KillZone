package view;
import com.sun.istack.internal.NotNull;
import logic.GameLogic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;


public class Client {

    private static final int PORT = 9952;
    private boolean exit = false;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean isConnected;

    private static final GameLogic gameLogic = new GameLogic();
    int[][] boardLogic = gameLogic.getBoardServer();
    int[][] boardClient = new int[9][9];

    int[] currentPosition = new int[2];
    int[] currentOpponentPosition = new int[2];

    JFrame frame = new GameField();
    Square[][] boardView = new Square[9][9];
    Square[][] opponentBoardView = new Square[9][9];
    int mark;

    JLabel message = new JLabel("");
    Square currentSquare = new Square(0);
    Square prevSquare = new Square(0);
    Square currentSquareOpponent = new Square(0);
    Square prevSquareOpponnent = new Square(0);

    private ImageIcon icon;
    private ImageIcon opponentIcon;

    final JPanel boardPanel;
    final JPanel opponentBoardPanel;
    JPanel mainBoard;

    public Client(String serverAddress) throws IOException {
        mainBoard = new JPanel();
        boardPanel = new JPanel();
        boardPanel.setFocusable(true);
        opponentBoardPanel = new JPanel();
        try {

            socket = new Socket(serverAddress, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            isConnected = true;

            message.setBackground(Color.LIGHT_GRAY);
            message.setPreferredSize(new Dimension(500,20));


            String response = in.readLine();
            if (response.startsWith("WELCOME")) {
                mark = Integer.parseInt(response.substring(8));
                frame.setTitle("Small KillZone " + (mark == 5 ? 1 : 2));
                icon = mark == 5 ? createImageIcon("Player.jpg", "Client player icon") : createImageIcon("Enemy.jpg", "Opponent player icon");
                opponentIcon = mark == 5 ? createImageIcon("Enemy.jpg", "Opponent player icon") : createImageIcon("Player.jpg", "Client player icon");

            }


            boardPanel.setPreferredSize(new Dimension(250,250));
            boardPanel.setBackground(new Color(50,50,58));
            boardPanel.setLayout(new GridLayout(9, 9, 1, 1));


            opponentBoardPanel.setPreferredSize(new Dimension(250,250));
            opponentBoardPanel.setBackground(new Color(50,50,58));
            opponentBoardPanel.setLayout(new GridLayout(9, 9, 1, 1));
            for (int i = 0; i < 9; i++) {
                for(int k = 0; k < 9; k++) {
                    if (i == 4 && k == 4) {

                        currentPosition[0] = i;
                        currentPosition[1] = k;


                        currentOpponentPosition[0] = i;
                        currentOpponentPosition[1] = k;


                        boardView[i][k] = new Square(icon);
                        currentSquare = boardView[i][k];


                        opponentBoardView[i][k] = new Square(99);
                        currentSquareOpponent = opponentBoardView[i][k];
                    } else {
                        boardView[i][k] = new Square(0);
                        opponentBoardView[i][k] = new Square(0);
                    }

                    boardPanel.add(boardView[i][k]);
                    opponentBoardPanel.add(opponentBoardView[i][k]);
                }
            }


            boardPanel.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        out.println("MOVE up");
                        System.out.println("up");
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        out.println("MOVE down");
                        System.out.println("down");
                    } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        out.println("MOVE left");
                        System.out.println("left");
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        out.println("MOVE right");
                        System.out.println("right");
                    } else if(e.getKeyCode() == KeyEvent.VK_W) {
                        out.println("BLEW up");
                    } else if(e.getKeyCode() == KeyEvent.VK_S) {
                        out.println("BLEW down");
                    } else if(e.getKeyCode() == KeyEvent.VK_A) {
                        out.println("BLEW left");
                    } else if(e.getKeyCode() == KeyEvent.VK_D) {
                        out.println("BLEW right");
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        out.println("ESCAPE");
                    }
                }
            });

            mainBoard.setLayout(new FlowLayout());
            mainBoard.setBackground(Color.DARK_GRAY);
            mainBoard.add(boardPanel);
            mainBoard.add(opponentBoardPanel);
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(message, BorderLayout.SOUTH);
            frame.getContentPane().add(mainBoard, BorderLayout.CENTER);
            frame.setResizable(false);
            frame.setVisible(true);
        } catch (ConnectException ce) {
            isConnected = false;
        }
    }

    public static void main(String[] args) throws Exception {
        while(true) {
            String serverAddress = (args.length == 0) ? "localhost" : args[1];
            final Client client = new Client(serverAddress);
            if (client.isConnected) {
                try {
                    client.play();
                    if (!client.wantsToPlayAgain()) {
                        break;
                    }
                } catch (SocketException se) {
                    client.isConnected = false;
                }
            } else {
                final JFrame frame = new JFrame();
                frame.setSize(280, 100);
                JPanel errorText = new JPanel();
                JLabel message = new JLabel("Server is unavailable. Try later.");
                message.setSize(250, 50);
                errorText.add(message);
                JButton okButton = new JButton("Quit");
                okButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.exit(0);
                    }
                });
                errorText.add(okButton);
                frame.add(errorText);
                frame.setResizable(false);
                frame.setVisible(true);
                frame.setLocationRelativeTo(null);
                break;
            }
        }
    }

    public void play() throws Exception {
        String response;
        try {
            while (true) {
                response = in.readLine();
                if(!"".equals(response)) {
                    if (response.startsWith("VALID_MOVE")) {
                        String[] res = response.substring(11).split(":");
                        int code = Integer.valueOf(res[0]);
                        String direction = res[1];
                        message.setText("Available turn. Choose direction (WASD) to throw the bomb");
                        prevSquare = boardView[currentPosition[1]][currentPosition[0]];
                        if(direction.equals("up")) {
                            currentSquare = boardView[currentPosition[1]-1][currentPosition[0]];
                            currentPosition[1]-=1;
                        } else if(direction.equals("down")) {
                            currentSquare = boardView[currentPosition[1]+1][currentPosition[0]];
                            currentPosition[1]+=1;
                        } else if(direction.equals("left")) {
                            currentSquare = boardView[currentPosition[1]][currentPosition[0]-1];
                            currentPosition[0]-=1;
                        } else if(direction.equals("right")) {
                            currentSquare = boardView[currentPosition[1]][currentPosition[0]+1];
                            currentPosition[0]+=1;
                        }
                        prevSquare.setIcon(getImageIcon(code));
                        currentSquare.setIcon(icon);
                        currentSquare.repaint();
                        prevSquare.repaint();
                    } else if(response.startsWith("INVALID_MOVE")) {
                        message.setText(response.substring(13)+" Throw the bomb(WASD) or skip your turn (ESC)");
                    } else if (response.startsWith("OPPONENT_MOVED")) {
                        String[] res = response.substring(15).split(":");
                        int code = Integer.valueOf(res[0]);
                        String direction = res[1];
                        prevSquareOpponnent = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]];
                        if(direction.equals("up")) {
                            currentSquareOpponent = opponentBoardView[currentOpponentPosition[1]-1][currentOpponentPosition[0]];
                            currentOpponentPosition[1] -=1;
                        } else if(direction.equals("down")) {
                            currentSquareOpponent = opponentBoardView[currentOpponentPosition[1]+1][currentOpponentPosition[0]];
                            currentOpponentPosition[1] += 1;
                        } else if(direction.equals("left")) {
                            currentSquareOpponent = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]-1];
                            currentOpponentPosition[0] -= 1;
                        } else if(direction.equals("right")) {
                            currentSquareOpponent = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]+1];
                            currentOpponentPosition[0] += 1;
                        }
                        prevSquareOpponnent.setIcon(getImageIcon(code));
                        currentSquareOpponent.setIcon(opponentIcon);
                        currentSquare.repaint();
                        prevSquare.repaint();
                        opponentBoardPanel.repaint();
                    } else if (response.equals("YOU_MOVE")) {
                        message.setText("Your opponent ended the turn. It's yours.");
                    } else if (response.startsWith("OPPONENT_MOVE")) {
                        message.setText(response.substring(14));
                    } else if (response.startsWith("BLEW")) {
                        String direction = "";
                        int code = 0;
                        String[] res = response.substring(5).split(":");
                        code = Integer.valueOf(res[0]);
                        direction = res[1];
                        Square blowedBlock = new Square();
                        if (direction.equals("up")) {
                            blowedBlock = boardView[currentPosition[1]-1][currentPosition[0]];
                            setBlowedIcon(blowedBlock, code);
                        } else if (direction.equals("down")) {
                            blowedBlock = boardView[currentPosition[1]+1][currentPosition[0]];
                            setBlowedIcon(blowedBlock, code);
                        } else if (direction.equals("left")) {
                            blowedBlock = boardView[currentPosition[1]][currentPosition[0]-1];
                            setBlowedIcon(blowedBlock, code);
                        } else if (direction.equals("right")) {
                            blowedBlock = boardView[currentPosition[1]][currentPosition[0]+1];
                            setBlowedIcon(blowedBlock, code);
                        }
                        blowedBlock.repaint();
                        if (code > 4) {
                            setBlowedIcon(blowedBlock, mark+2);
                            blowedBlock.repaint();
                            message.setText("You won!");
                            break;
                        }
                    } else if (response.startsWith("OPPONENT_BOMBED")) {
                        String direction = "";
                        int code = 0;
                        String[] res = response.substring(16).split(":");
                        code = Integer.valueOf(res[0]);
                        direction = res[1];
                        Square bombedSquare = new Square();
                        if(direction.equals("up")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]-1][currentOpponentPosition[0]];
                            setBlowedIcon(bombedSquare, code);
                        } else if(direction.equals("down")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]+1][currentOpponentPosition[0]];
                            setBlowedIcon(bombedSquare, code);
                        } else if(direction.equals("left")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]-1];
                            setBlowedIcon(bombedSquare, code);
                        } else if(direction.equals("right")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]+1];
                            setBlowedIcon(bombedSquare, code);
                        }
                        bombedSquare.repaint();
                        message.setText("Your opponent ended the turn. It's yours.");
                    } else if (response.startsWith("OPPONENT_EXIT")) {
                        message.setText("Your opponent leaved. Wait for another player.");
                        exit = true;
                        break;
                    } else if (response.startsWith("YOU_BOMBING")) {
                        String direction = response.substring(12);
                        Square bombedSquare = new Square();
                        if(direction.equals("up")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]-1][currentOpponentPosition[0]];
                        } else if(direction.equals("down")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]+1][currentOpponentPosition[0]];
                        } else if(direction.equals("left")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]-1];
                        } else if(direction.equals("right")) {
                            bombedSquare = opponentBoardView[currentOpponentPosition[1]][currentOpponentPosition[0]+1];
                        }
                        setBlowedIcon(bombedSquare, mark == 5 ? 8 : 7);
                        bombedSquare.repaint();
                        message.setText("You lose!");
                        break;
                    } else if (response.startsWith("GAME_STARTED")) {
                        currentSquare.setIcon(icon);
                        currentSquare.repaint();
                        currentSquareOpponent.setIcon(opponentIcon);
                        currentSquareOpponent.repaint();
                        currentSquareOpponent.setIcon(opponentIcon);
                        currentSquareOpponent.repaint();
                        message.setText(response.substring(13));
                    } else if (response.startsWith("MESSAGE")) {
                        message.setText(response.substring(8));
                    }
                }
            }
            out.println("QUIT");
        } finally {
            socket.close();
        }
    }

    private boolean wantsToPlayAgain() {
        int response = JOptionPane.showConfirmDialog(frame,
                "Wanna play again?",
                "",
                JOptionPane.YES_NO_OPTION);
        frame.dispose();
        return response == JOptionPane.YES_OPTION;
    }

    static class Square extends JPanel {
        JLabel label = new JLabel((Icon)null);

        public Square(int code) {
            setIcon(getImageIcon(code));
            FlowLayout layout = (FlowLayout)this.getLayout();
            layout.setVgap(0);
            layout.setHgap(0);
            this.setBorder(null);
            add(label);
        }

        public Square(ImageIcon img) {
            setIcon(img);
            FlowLayout layout = (FlowLayout)this.getLayout();
            layout.setVgap(0);
            layout.setHgap(0);
            this.setBorder(null);
            add(label);
        }

        public Square() {

        }

        @NotNull
        public void setIcon(Icon icon) {
            label.setIcon(icon);
            label.repaint();
        }

    }

    static ImageIcon createImageIcon(String path,
                                     String description) {
        java.net.URL imgURL = Square.class.getClassLoader().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("File doesn't exist: " + path);
            return null;
        }
    }

    static ImageIcon getImageIcon(int code) {
        switch (code) {
            case 0:
                return createImageIcon("Fog.jpg", "Fog");
            case 1:
                return createImageIcon("Bedrock.jpg", "Immortal block");
            case 2:
                return createImageIcon("Ground.png", "Empty Space");
            case 3:
                return createImageIcon("Wall.png", "Destroyable block");
            case 4:
                return createImageIcon("Destroyed_Wall.png", "Bombed wall");
            case 5:
                return createImageIcon("Player.jpg", "You player icon");
            case 6:
                return createImageIcon("Enemy.jpg", "Opposite player icon");
            case 7:
                return createImageIcon("Dead_Enemy.jpg", "Dead enemy");
            case 8:
                return createImageIcon("Dead_Player.jpg", "Dead gamer");
            case 99:
                return createImageIcon("Anonymous.jpg", "Anonymous player icon");
            default:
                return null;
        }
    }

    void setBlowedIcon(Square blowedBlock, int code) {
        switch (code) {
            case 4:
                blowedBlock.setIcon(getImageIcon(4));
                break;
            case 3:
                blowedBlock.setIcon(getImageIcon(3));
                break;
            case 2:
                blowedBlock.setIcon(getImageIcon(2));
                break;
            case 1:
                blowedBlock.setIcon(getImageIcon(1));
                break;
            case 5:
                blowedBlock.setIcon(getImageIcon(5));
                break;
            case 6:
                blowedBlock.setIcon(getImageIcon(6));
                break;
            case 7:
                blowedBlock.setIcon(getImageIcon(7));
                break;
            case 8:
                blowedBlock.setIcon(getImageIcon(8));
                break;
        }
    }

}
