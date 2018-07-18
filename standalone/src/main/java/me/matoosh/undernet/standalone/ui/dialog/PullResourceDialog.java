package me.matoosh.undernet.standalone.ui.dialog;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceType;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Dialog for pulling resources from the network.
 * Created by Mateusz Rębacz on 03.11.2017.
 */

public class PullResourceDialog extends JDialog {
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
        final File[] saveFile = {null};
        fileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Button clicked.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setDialogTitle("Choose the save directory");
                int result = fileChooser.showOpenDialog(PullResourceDialog.this);
                if(result == JFileChooser.APPROVE_OPTION) {
                    saveFile[0] = fileChooser.getSelectedFile();
                }
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
                final NetworkID netId = new NetworkID(networkIdField.getText().replaceAll("\\s+",""));
                if(netId.isValid()) {
                    //Starting the pull.
                    UnderNet.router.resourceManager.pull(netId); //Use inputted network id.

                    //Registering the pull event.
                    EventManager.registerHandler(new EventHandler() {
                        @Override
                        public void onEventCalled(Event e) {
                            ResourceTransferFinishedEvent transferFinishedEvent = (ResourceTransferFinishedEvent) e;

                            if(transferFinishedEvent.transferHandler.transferType == ResourceTransferType.INBOUND && transferFinishedEvent.transferHandler.resource.getNetworkID().equals(netId) && transferFinishedEvent.transferHandler.resource.getInfo().resourceType == ResourceType.FILE) {
                                FileResource fileResource = (FileResource)transferFinishedEvent.transferHandler.resource;

                                //Copying the received file to dest.
                                if(saveFile[0] != null) {
                                    try {
                                        Files.copy(Paths.get(fileResource.file.getAbsolutePath()), Paths.get(saveFile[0].getAbsolutePath() + "/" + fileResource.file.getName()));
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }


                                    //File dialog.
                                    EventQueue.invokeLater(()->JOptionPane.showMessageDialog(PullResourceDialog.this, String.format("Retrieved file %s! \nNetwork id: %s \nSaved to: %s", fileResource.file.getName(), fileResource.getNetworkID().getStringValue(), saveFile[0]),"File Retrieved!",  JOptionPane.INFORMATION_MESSAGE));
                                } else {
                                    //File dialog.
                                    EventQueue.invokeLater(()->JOptionPane.showMessageDialog(PullResourceDialog.this, String.format("Retrieved file %s! \nNetwork id: %s \nSaved to: %s", fileResource.file.getName(), fileResource.getNetworkID().getStringValue(), UnderNet.fileManager.getContentFolder()), "File Retrieved!", JOptionPane.INFORMATION_MESSAGE));
                                }
                            }

                            //Unregistering the handler.
                            EventManager.unregisterHandler(this, ResourceTransferFinishedEvent.class);
                        }
                    }, ResourceTransferFinishedEvent.class);

                    PullResourceDialog.this.dispose();
                } else {
                    JOptionPane.showMessageDialog(PullResourceDialog.this, "The Network ID you specified is invalid!", "Invalid Network ID", JOptionPane.ERROR_MESSAGE);
                }
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
