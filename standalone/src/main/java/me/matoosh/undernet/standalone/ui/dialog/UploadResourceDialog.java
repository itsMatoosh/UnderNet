package me.matoosh.undernet.standalone.ui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;

/**
 * Dialog for uploading resources to the network.
 * Created by Mateusz Rębacz on 20.10.2017.
 */

public class UploadResourceDialog extends JDialog {
    /**
     * The result of the last file choosing the user has done.
     */
    private File fileChooseResult;

    public UploadResourceDialog(JFrame parent) {
        //Setting the title of the dialog.
        super(parent, "Publish Resource", true);

        //Setting the content.
        setLayout(new GridBagLayout());
        setSize(220, 110);
        centerDialogOnMouse();
        addContent();
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Centers the window on mouse.
     */
    private void centerDialogOnMouse() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        setLocation(mousePoint.x - getSize().width/2, mousePoint.y - getSize().height/2);
    }

    /**
     * Adds content to the dialog.
     */
    private void addContent() {
        JButton chooseFileButton = new JButton("Select file...");
        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Button clicked.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(UploadResourceDialog.this);
                if(result == JFileChooser.APPROVE_OPTION) {
                    fileChooseResult = fileChooser.getSelectedFile();
                }
            }
        });
        add(chooseFileButton, new GridBagConstraints());

        JButton uploadButton = new JButton("Publish!");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Button clicked. Checking whether a correct path has been chosen.
                if(fileChooseResult != null) {
                    //Publishing resource on UnderNet.
                    UnderNet.router.resourceManager.publish(new FileResource(fileChooseResult));
                    UploadResourceDialog.this.dispose();
                }
            }
        });
        add(uploadButton, new GridBagConstraints());
    }
}
