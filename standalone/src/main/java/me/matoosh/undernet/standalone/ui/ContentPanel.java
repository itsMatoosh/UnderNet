package me.matoosh.undernet.standalone.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The panel showing content from the currently active community.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class ContentPanel extends JPanel {
    public ContentPanel () {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(Color.BLACK);

        JLabel underNetText = new JLabel("UnderNet", SwingConstants.CENTER );
        underNetText.setForeground(Color.WHITE);

        //underNetText.
        add(underNetText, BorderLayout.CENTER);
    }
}
