package me.matoosh.undernet.standalone.ui.dialog;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetSocketAddress;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;

/**
 * A dialog for adding new node to the node cache.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class AddNodeCacheDialog extends JDialog {
    /**
     * The text field with the address of the node.
     */
    private JTextField nodeAddressField;

    public AddNodeCacheDialog(JFrame parent) {
        //Setting the title of the dialog.
        super(parent, "Add Node", true);

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
                //Creating an empty node.
                Node savedNode = new Node();

                //Getting the address.
                String[] addressSplit = nodeAddressField.getText().split(":");
                int port = 2017;
                if(addressSplit.length > 1) {
                    if(addressSplit[1] != null) {
                        if(!addressSplit[1].equals("")) {
                            //Custom port was provided.
                            port = Integer.parseInt(addressSplit[1]);
                            savedNode.port = port;
                        }
                    }
                }
                savedNode.address = new InetSocketAddress(addressSplit[0], port);


                //Adding the node to the cache.
                EntryNodeCache.addNode(savedNode);

                //Closing the dialog.
                AddNodeCacheDialog.this.dispose();
            }
        });
        if(UnderNet.router.status.equals(InterfaceStatus.STARTED) || UnderNet.router.status.equals(InterfaceStatus.STARTING)) {
            saveButton.setEnabled(false);
        }
        buttonDrawer.add(saveButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Closing the dialog.
                AddNodeCacheDialog.this.dispose();
            }
        });

        buttonDrawer.add(cancelButton);

        add(buttonDrawer);
    }
}