package me.matoosh.undernet.standalone.ui.dialog;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.standalone.UnderNetStandalone;

/**
 * A dialog for changing the network identity.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class ChangeIdentityDialog extends JDialog {
    /**
     * The text field with the username.
     */
    private JTextField usernameField;

    public ChangeIdentityDialog(JFrame parent) {
        //Setting the title of the dialog.
        super(parent, "Change Identity", true);

        //Setting the content.
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        setSize(300, 120);
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
        add(new JLabel("Username"));
        usernameField = new JTextField();
        usernameField.setText(UnderNetStandalone.networkIdentity.getUsername());
        add(usernameField);

        add(new JPanel());

        JPanel buttonDrawer = new JPanel();
        buttonDrawer.setLayout(new BoxLayout(buttonDrawer, BoxLayout.X_AXIS));

        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Setting the new username.
                NetworkIdentity identity = new NetworkIdentity();
                identity.setUsername(usernameField.getText());
                UnderNetStandalone.setNetworkIdentity(identity);

                //Closing the dialog.
                ChangeIdentityDialog.this.dispose();
            }
        });
        buttonDrawer.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Closing the dialog.
                ChangeIdentityDialog.this.dispose();
            }
        });

        buttonDrawer.add(cancelButton);

        add(buttonDrawer);
    }
}
