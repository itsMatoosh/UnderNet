package me.matoosh.undernet.standalone.ui.dialog;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        saveButton.addActionListener(actionEvent -> {
            //Adds node to the cache.
            Node saved = EntryNodeCache.addNode(nodeAddressField.getText());

            //Connecting if started.
            if (UnderNet.router.status.equals(InterfaceStatus.STARTED) || UnderNet.router.status.equals(InterfaceStatus.STARTING)) {
                UnderNet.router.connectNode(saved);
            }

            //Closing the dialog.
            AddNodeCacheDialog.this.dispose();
        });
        if(UnderNet.router.status.equals(InterfaceStatus.STARTED) || UnderNet.router.status.equals(InterfaceStatus.STARTING)) {
            saveButton.setText("Connect");
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