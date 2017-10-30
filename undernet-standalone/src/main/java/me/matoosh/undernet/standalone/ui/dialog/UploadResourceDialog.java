package me.matoosh.undernet.standalone.ui.dialog;

import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * Dialog for uploading resources to the network.
 * Created by Mateusz Rębacz on 20.10.2017.
 */

public class UploadResourceDialog extends JDialog {
    public UploadResourceDialog(JFrame parent) {
        //Setting the title of the dialog.
        super(parent, "Upload Resource", true);

        //Setting the content.
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        setSize(500, 400);
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

    }
}
