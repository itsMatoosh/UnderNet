package me.matoosh.undernet.standalone.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;

/**
 * The main frame of the app.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class AppFrame extends JFrame {
    /**
     * The simulation panel.
     */
    public static ContentPanel contentPanel;

    public AppFrame() {
        super("UnderNet");
        setSize(800, 500);
        setLayout(new GridBagLayout());
        //addPanels();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    /**
     * Adds the app panels.
     */
    private void addPanels() {
        //Settings panel.
        contentPanel = new ContentPanel();
        add(contentPanel, new GridBagConstraints(0, 0, 1, 1, 0.05, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //Simulation panel.
        //simulationPanel = new SimulationPanel();
        //add(simulationPanel, new GridBagConstraints(1, 0, 1, 1, 0.95, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
}
