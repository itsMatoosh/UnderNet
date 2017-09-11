package me.matoosh.undernet.standalone.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.cache.NodeCacheAddedEvent;
import me.matoosh.undernet.event.cache.NodeCacheRemovedEvent;
import me.matoosh.undernet.event.connection.ConnectionDroppedEvent;
import me.matoosh.undernet.event.connection.ConnectionEstablishedEvent;
import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import me.matoosh.undernet.standalone.ui.dialog.AddNodeCacheFrame;

/**
 * Panel displaying the list of known nodes.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class NodesPanel extends JPanel {
    /**
     * The list of nodes.
     */
    private JList nodesList;

    public NodesPanel() {
        //Setting to gridbaglayout.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Adding the label
        add(new JLabel("Nodes"));

        //Adding the list.
        nodesList = new JList(new String[] {
                "Loading nodes..."
        });
        nodesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(nodesList));

        //Adding the buttons on the bottom.
        JPanel buttonsDrawer = new JPanel();
        buttonsDrawer.setMaximumSize(new Dimension(200 ,50));
        buttonsDrawer.setLayout(new GridLayout());

        //Add button
        JButton addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                openNodeAddDialog();
            }
        });
        buttonsDrawer.add(addButton);
        //Remove button
        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(nodesList.getSelectedValue() == null) return;
                String selected = (String) nodesList.getSelectedValue();
                if(selected.equals("No nodes cached...") || selected.equals("Loading nodes...")) return;

                try {
                    for (Node n : NodeCache.cachedNodes) {
                        if (n.toString().equals(selected)) {
                            NodeCache.removeNode(n);
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
        buttonsDrawer.add(removeButton);
        add(buttonsDrawer);

        //Refreshing the nodes list.
        refreshNodesList();
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                //Called when a node is added to the node cache.
                refreshNodesList();
            }
        }, NodeCacheAddedEvent.class);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                //Called when a node is removed from the node cache.
                refreshNodesList();
            }
        }, NodeCacheRemovedEvent.class);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                //Called when a connection has been established.
                refreshNodesList();
            }
        }, ConnectionEstablishedEvent.class);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                //Called when a connection has been dropped.
                refreshNodesList();
            }
        }, ConnectionDroppedEvent.class);
    }

    /**
     * Refreshes the nodes list based on the current node cache.
     */
    private void refreshNodesList() {
        ArrayList<String> nodes = new ArrayList<String>();

        for(Node node : NodeCache.cachedNodes) {
            //Whether we are currently connected to the node.
            boolean connected = false;

            for(Connection conn : UnderNet.router.connections) {
                if(conn.other == node) {
                    connected = true;
                }
            }
            if(connected) {
                nodes.add(node.toString() + " [connected]");
            } else {
                nodes.add(node.toString());
            }
        }

        if(nodes.size() == 0) {
            nodes.add("No nodes cached...");
        }

        nodesList.setListData(nodes.toArray());
    }

    /**
     * Opens the add node dialog.
     */
    public void openNodeAddDialog() {
        //The add button was pressed.
        JDialog nodeAddDialog = new AddNodeCacheFrame(UnderNetStandalone.mainAppFrame);
        nodeAddDialog.setVisible(true);
    }
}
