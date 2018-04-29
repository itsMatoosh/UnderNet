package me.matoosh.undernet.standalone.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.channels.Channel;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.cache.NodeCacheAddedEvent;
import me.matoosh.undernet.event.cache.NodeCacheRemovedEvent;
import me.matoosh.undernet.event.channel.ChannelClosedEvent;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.PingMessage;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import me.matoosh.undernet.standalone.ui.dialog.AddNodeCacheDialog;

/**
 * Panel displaying the list of known nodes.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class NodesPanel extends JPanel {
    /**
     * Nodes that recently sent messages to self.
     */
    public ArrayList<Node> receivedMsgFrom = new ArrayList<>();

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
        nodesList = new JList(new Node[] {});
        nodesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nodesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    //Double-click detected
                    if(UnderNet.router.status.equals(InterfaceStatus.STARTED)) {
                        int index = list.locationToIndex(evt.getPoint());
                        Node node = (Node)nodesList.getModel().getElementAt(index);

                        //Checking if the node is connected.
                        if(node.isConnected()) {
                            //Sending a ping content.
                            node.send(new NetworkMessage(MsgType.NODE_PING, new PingMessage(false)));
                        } else {
                            //Connecting to the node.
                            UnderNet.router.connectNode(node);
                        }
                    }
                }
            }
        });
        add(new JScrollPane(nodesList));

        //Setting cell renderer for the nodes list.
        nodesList.setCellRenderer(new NodesListCellRenderer(this));

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
                Node selected = ((Node) nodesList.getSelectedValue());

                //Disconnect or remove from cache.
                if(selected.isConnected()) {
                    UnderNet.router.disconnectNode(selected);
                } else {
                    EntryNodeCache.removeNode(selected);
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
        }, ChannelCreatedEvent.class);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                //Called when a connection has been dropped.
                refreshNodesList();
            }
        }, ChannelClosedEvent.class);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                //Called when a message has been received.
                ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
                receivedMsgFrom.add(messageReceivedEvent.remoteNode);
            }
        }, ChannelMessageReceivedEvent.class);
    }

    /**
     * Refreshes the nodes list based on the current node cache.
     */
    private void refreshNodesList() {
        if(UnderNet.router.status.equals(InterfaceStatus.STARTED)) {
            //Using connected and cached nodes if the router has started.
            ArrayList<Node> nodesToList = new ArrayList<>();
            nodesToList.addAll(UnderNet.router.connectedNodes);
            for (Node cachedNode:
            EntryNodeCache.cachedNodes) {
                boolean canAdd = true;
                for (Node connectedNode : UnderNet.router.connectedNodes) {
                    if(cachedNode.address.equals(connectedNode.address)) {
                        canAdd = false;
                    }
                }
                if(canAdd) {
                    nodesToList.add(cachedNode);
                }
            }
            nodesList.setListData(nodesToList.toArray());
        } else {
            //Using cached nodes if the router isn't online.
            nodesList.setListData(EntryNodeCache.cachedNodes.toArray());
        }
    }

    /**
     * Opens the add node dialog.
     */
    public void openNodeAddDialog() {
        //The add button was pressed.
        JDialog nodeAddDialog = new AddNodeCacheDialog(UnderNetStandalone.mainAppFrame);
        nodeAddDialog.setVisible(true);
    }
}
class NodesListCellRenderer extends DefaultListCellRenderer
{
    private NodesPanel nodesPanel;

    public NodesListCellRenderer(NodesPanel panel) {
        this.nodesPanel = panel;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Node) {
            Node node = (Node) value;
            setText(node.toString());
            if(UnderNet.router.status == InterfaceStatus.STARTED) {
                if (node.isConnected()) {
                    setBackground(Color.GREEN);
                    for (int i = 0; i < this.nodesPanel.receivedMsgFrom.size(); i++) {
                        if (this.nodesPanel.receivedMsgFrom.get(i).address.equals(node.address)) {
                            setBackground(Color.CYAN);
                            this.nodesPanel.receivedMsgFrom.remove(node);
                        }
                    }
                } else {
                    setBackground(Color.RED);
                }
            } else {
                setBackground(Color.ORANGE);
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }
        } else {
            setText("UNKNOWN");
            setBackground(Color.gray);
        }
        return c;
    }
}