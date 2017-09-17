package me.matoosh.undernet.standalone.ui.dialog;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.KnownNode;

/**
 * A frame for adding new node to the node cache.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class AddNodeCacheFrame extends JDialog {
    /**
     * The text field with the username of the node.
     */
    private JTextField nodeUsernameField;
    /**
     * The text field with the address of the node.
     */
    private JTextField nodeAddressField;

    public AddNodeCacheFrame(JFrame parent) {
        //Setting the title of the dialog.
        super(parent, "Add Node", true);

        //Setting the content.
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        setSize(300, 200);
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
        nodeUsernameField = new JTextField();
        add(nodeUsernameField);
        add(new JLabel("IP Address"));
        nodeAddressField = new JTextField();
        add(nodeAddressField);

        add(new JPanel());

        JPanel buttonDrawer = new JPanel();
        buttonDrawer.setLayout(new BoxLayout(buttonDrawer, BoxLayout.X_AXIS));

        final JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                KnownNode savedNode = new KnownNode();

                //Getting the username.
                savedNode.username = nodeUsernameField.getText();

                //Getting the address.
                try {
                    String[] addressSplit = nodeAddressField.getText().split(":");
                    savedNode.address = InetAddress.getByName(addressSplit[0]);
                    if(addressSplit.length > 1) {
                        if(addressSplit[1] != null) {
                            if(!addressSplit[1].equals("")) {
                                //Custom port was provided.
                                savedNode.port = Integer.parseInt(addressSplit[1]);
                            }
                        }
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }


                //Adding the node to the cache.
                NodeCache.addNode(savedNode);

                //Closing the dialog.
                AddNodeCacheFrame.this.dispose();
            }
        });
        buttonDrawer.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Closing the dialog.
                AddNodeCacheFrame.this.dispose();
            }
        });

        buttonDrawer.add(cancelButton);

        add(buttonDrawer);
    }
}