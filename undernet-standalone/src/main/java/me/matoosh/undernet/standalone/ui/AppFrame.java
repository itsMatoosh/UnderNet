package me.matoosh.undernet.standalone.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;

import me.matoosh.undernet.UnderNet;

/**
 * The main frame of the app.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class AppFrame extends JFrame {
    /**
     * The simulation panel.
     */
    public static ContentPanel contentPanel;
    /**
     * The list of nodes.
     */
    public NodesPanel nodesList;
    /**
     * The list of communities.
     */
    public CommunitiesPanel communitiesList;


    public AppFrame() {
        super("UnderNet");
        setSize(800, 800);
        setLayout(new GridBagLayout());
        addPanels();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    /**
     * Adds the app panels.
     */
    private void addPanels() {
        //Adding the content panel, nodes list and communities list.
        contentPanel = new ContentPanel();
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        nodesList = new NodesPanel();
        nodesList.setBorder(BorderFactory.createLineBorder(Color.black));
        communitiesList = new CommunitiesPanel();
        communitiesList.setBorder(BorderFactory.createLineBorder(Color.black));
        add(communitiesList, new GridBagConstraints(0, 0, 1, 1, 0.125, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(contentPanel, new GridBagConstraints(1, 0, 1, 1, 0.75, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(nodesList, new GridBagConstraints(2, 0, 1, 1, 0.125, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //Connect button.
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UnderNet.connect();
            }
        });

        add(connectButton, new GridBagConstraints(0, 1, 3, 1, 1, 0.05, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //Console panel.
        add(new ConsolePanel(), new GridBagConstraints(0, 2, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
}