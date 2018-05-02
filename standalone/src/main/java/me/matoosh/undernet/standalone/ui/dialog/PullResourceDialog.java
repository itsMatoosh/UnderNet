package me.matoosh.undernet.standalone.ui.dialog;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.data.NetworkID;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Dialog for pulling resources from the network.
 * Created by Mateusz Rębacz on 03.11.2017.
 */

public class PullResourceDialog extends JDialog {

    /**
     * Directory in which the resource will be saved.
     */
    public File resourceSaveDir;

    public PullResourceDialog(JFrame parent) {
        //Setting the title of the dialog.
        super(parent, "Pull Resource", true);

        //Setting the content.
        setLayout(new GridBagLayout());
        setSize(270, 110);
        centerDialogOnMouse();
        addContent();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Adds content to the frame.
     */
    private void addContent() {
        GridBagConstraints constraints0 = new GridBagConstraints();
        constraints0.gridx = 0;
        constraints0.gridy = 0;
        add(new JLabel("NetworkID:"), constraints0);

        GridBagConstraints constraints1 = new GridBagConstraints();
        constraints1.gridx = 1;
        constraints1.gridy = 0;
        constraints1.gridwidth = 30;
        final JTextField networkIdField = new JTextField();
        networkIdField.setPreferredSize(new Dimension(150, 20));
        add(networkIdField, constraints1);

        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.gridx = 0;
        constraints2.gridy = 1;
        JButton fileChooser = new JButton("Save to...");
        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //JFileChooser fileChooser1 = new JFileChooser();
                //fileChooser1.
            }
        });
        add(fileChooser, constraints2);

        GridBagConstraints constraints3 = new GridBagConstraints();
        constraints3.gridx = 0;
        constraints3.gridy = 2;
        JButton pullButton = new JButton("Pull!");
        pullButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                NetworkID netId = new NetworkID(networkIdField.getText());
                //if(netId.isValid()) {
                    //Starting the pull.
                    UnderNet.router.resourceManager.pull(netId); //Use inputed network id.
                //}
            }
        });
        add(pullButton, constraints3);
    }

    /**
     * Centers the window on mouse.
     */
    private void centerDialogOnMouse() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        setLocation(mousePoint.x - getSize().width/2, mousePoint.y - getSize().height/2);
    }
}
