package view;

import javax.swing.*;
import java.awt.*;


public class GameField extends JFrame {
    public GameField() throws HeadlessException {
        super("Small KillZone");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

}

