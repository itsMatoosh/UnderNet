package me.matoosh.undernet.standalone.uix;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.cache.NodeCacheAddedEvent;
import me.matoosh.undernet.event.cache.NodeCacheRemovedEvent;
import me.matoosh.undernet.event.channel.ChannelClosedEvent;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.standalone.uix.dialog.NodeAddDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NodePanel extends EventHandler {
    private JPanel panel;
    private JButton removeNodeButton;
    private JButton addNodeButton;
    private JList nodeList;

    public NodePanel() {
        //add node button clicked.
        $$$setupUI$$$();
        addNodeButton.addActionListener(e -> new NodeAddDialog(MainFrame.instance.frame).setVisible(true));

        //remove node button
        removeNodeButton.addActionListener(e -> {
            if (nodeList.getSelectedValue() == null) return;
            Node selected = ((Node) nodeList.getSelectedValue());

            //Disconnect or remove from cache.
            if (selected.isConnected()) {
                UnderNet.router.disconnectNode(selected);
            } else {
                EntryNodeCache.removeNode(selected);
            }
        });

        //node list clicked
        nodeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JList list = (JList) e.getSource();
                if (e.getClickCount() == 2) {
                    //Double-click detected
                    if (UnderNet.router.status.equals(InterfaceStatus.STARTED)) {
                        int index = list.locationToIndex(e.getPoint());
                        if (index == -1) return;
                        Node node = (Node) nodeList.getModel().getElementAt(index);

                        //Checking if the node is connected.
                        if (!node.isConnected()) {
                            //Connecting to the node.
                            UnderNet.router.connectNode(node);
                        }
                    }
                } else if (e.getClickCount() == 1) {
                    //Single click detected
                    int index = list.locationToIndex(e.getPoint());
                    if (index == -1) return;
                    Node node = (Node) nodeList.getModel().getElementAt(index);

                    //Checking if the node is connected.
                    if (node.isConnected()) {
                        NodePanel.this.removeNodeButton.setText(ResourceBundle.getBundle("language").getString("button_disconnectNode"));
                    } else {
                        NodePanel.this.removeNodeButton.setText(ResourceBundle.getBundle("language").getString("button_removeNode"));
                    }
                }
            }
        });

        //event listeners
        registerListeners();

        //refresh
        refreshNodeList();
    }

    /**
     * Registers event listeners.
     */
    private void registerListeners() {
        EventManager.registerHandler(this, NodeCacheAddedEvent.class);
        EventManager.registerHandler(this, NodeCacheRemovedEvent.class);
        EventManager.registerHandler(this, ChannelCreatedEvent.class);
        EventManager.registerHandler(this, ChannelClosedEvent.class);
    }

    @Override
    public void onEventCalled(Event e) {
        refreshNodeList();
    }

    /**
     * Refreshes the nodes list based on the current node cache.
     */
    private void refreshNodeList() {
        if (UnderNet.router.status.equals(InterfaceStatus.STARTED)) {
            //Using connected and cached nodes if the router has started.
            ArrayList<Node> nodesToList = new ArrayList<>();
            nodesToList.addAll(UnderNet.router.getRemoteNodes());
            for (Node cachedNode :
                    EntryNodeCache.cachedNodes) {
                boolean canAdd = true;
                for (Node connectedNode : UnderNet.router.getRemoteNodes()) {
                    if (cachedNode.getAddress().equals(connectedNode.getAddress())) {
                        canAdd = false;
                    }
                }
                if (canAdd) {
                    nodesToList.add(cachedNode);
                }
            }
            nodeList.setListData(nodesToList.toArray());
        } else {
            //Using cached nodes if the router isn't online.
            nodeList.setListData(EntryNodeCache.cachedNodes.toArray());
        }
    }

    private void createUIComponents() {
        nodeList = new JList(new Node[]{});
        nodeList.setCellRenderer(new NodesListCellRenderer());
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel.setMinimumSize(new Dimension(-1, -1));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        nodeList.setSelectionMode(1);
        scrollPane1.setViewportView(nodeList);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addNodeButton = new JButton();
        this.$$$loadButtonText$$$(addNodeButton, ResourceBundle.getBundle("language").getString("button_addNode"));
        panel1.add(addNodeButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeNodeButton = new JButton();
        this.$$$loadButtonText$$$(removeNodeButton, ResourceBundle.getBundle("language").getString("button_removeNode"));
        panel1.add(removeNodeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Droid Sans Mono", Font.BOLD, 16, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("language").getString("title_nodes"));
        panel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}

/**
 * Renders elements within the nodes list.
 */
class NodesListCellRenderer extends DefaultListCellRenderer
{
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

                    //msg bg update
                    if(VisualPanelDraw.recentlyReceived.containsKey(node)) {
                        setBackground(Color.CYAN);
                    }
                } else {
                    setBackground(Color.GRAY);
                }
            }
            else if(UnderNet.router.status == InterfaceStatus.STARTING) {
                setBackground(Color.ORANGE);
            }
            else {
                setBackground(Color.GRAY);
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }
        } else {
            setText("UNKNOWN");
            setBackground(Color.GRAY);
        }
        return c;
    }
}
