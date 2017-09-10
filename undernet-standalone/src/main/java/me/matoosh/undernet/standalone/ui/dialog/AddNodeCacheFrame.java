package me.matoosh.undernet.standalone.ui.dialog;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A frame for adding new node to the node cache.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class AddNodeCacheFrame extends JFrame {
    public AddNodeCacheFrame() {
        super("Add Node");
        setSize(500, 200);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        addContent();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        requestFocus();
    }

    /**
     * Adds content to the dialog.
     */
    private void addContent() {
        add(new JLabel("Username"));
        add(new JTextField());
        add(new JLabel("IP Address"));
        add(new JTextField());

        add(new JPanel());

        add(new JButton("Save"));
    }
}
