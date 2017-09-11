package me.matoosh.undernet.standalone.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * The console panel.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class ConsolePanel extends JPanel {
    public ConsolePanel() {
        setLayout(new BorderLayout());

        JTextArea consoleTextArea = new JTextArea(13, 0);
        consoleTextArea.setLineWrap(true);

        TextAreaOutputStream textAreaOutputStream = new TextAreaOutputStream(consoleTextArea, "UnderNet");
        add(BorderLayout.CENTER, consoleTextArea);

        //Attaching console to the output.
        //System.(new PrintStream(textAreaOutputStream));
    }
}
