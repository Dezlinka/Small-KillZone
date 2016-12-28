package interaction;

import logic.GameLogic;
import org.apache.log4j.Logger;

import java.net.ServerSocket;


public class Server {

    static Logger log = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) throws Exception {
        log.info("Welcome to Small KillZone");
        try (ServerSocket server = new ServerSocket(9952)) {
            while (true) {
                GameLogic game = new GameLogic();
                game.printMap();

                GameLogic.Player firstPlayer = game.new Player(server.accept(), 5, game.firstPosition[0], game.firstPosition[1]);
                log.info("First");

                GameLogic.Player secondPlayer = game.new Player(server.accept(), 6, game.secondPosition[0], game.secondPosition[1]);
                log.info("Second");

                firstPlayer.setOpponent(secondPlayer);
                secondPlayer.setOpponent(firstPlayer);
                System.out.println("First mark: "+firstPlayer.mark + " x = "+firstPlayer.x + " y = "+firstPlayer.y);
                System.out.println("Second mark: "+secondPlayer.mark + " x = "+secondPlayer.x + " y = "+secondPlayer.y);
                System.out.println("First position: " );
                game.currentPlayer = firstPlayer;
                firstPlayer.start();
                secondPlayer.start();
            }
        }
    }

}
