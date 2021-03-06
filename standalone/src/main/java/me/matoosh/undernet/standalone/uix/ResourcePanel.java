package me.matoosh.undernet.standalone.uix;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferErrorEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferStartedEvent;
import me.matoosh.undernet.event.router.RouterControlLoopEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.file.FileManager;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import me.matoosh.undernet.standalone.uix.dialog.PublishResourceDialog;
import me.matoosh.undernet.standalone.uix.dialog.PullResourceDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ResourceBundle;

public class ResourcePanel extends EventHandler {
    private JPanel panel;
    private JList resourceList;
    private JButton publishButton;
    private JButton pullButton;
    private JLabel sectionTitle;

    public ResourcePanel() {
        $$$setupUI$$$();
        registerListeners();
        publishButton.addActionListener(e -> PublishResourceDialog.newInstance());
        pullButton.addActionListener(e -> PullResourceDialog.newInstance());
        resourceList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //Res
                    int index = resourceList.locationToIndex(e.getPoint());
                    Resource res = (Resource) resourceList.getModel().getElementAt(index);
                    if (res == null) return;

                    //Double clicked on resource, copying network id to clipboard.
                    StringSelection stringSelection = new StringSelection(res.getNetworkID().getStringValue());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                }
            }
        });
        sectionTitle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    //Opening the content folder.
                    try {
                        Desktop.getDesktop().open(UnderNet.fileManager.getContentFolder());
                    } catch (IOException e1) {
                    }
                }
            }
        });

        refreshList();
    }

    private void registerListeners() {
        EventManager.registerHandler(this, RouterStatusEvent.class);
        EventManager.registerHandler(this, ResourceTransferStartedEvent.class);
        EventManager.registerHandler(this, ResourceTransferFinishedEvent.class);
        EventManager.registerHandler(this, ResourceTransferErrorEvent.class);
        EventManager.registerHandler(this, RouterControlLoopEvent.class);
    }

    private void refreshList() {
        if (resourceList.getModel().getSize() == 0) {
            resourceList.setListData(new String[]{ResourceBundle.getBundle("language").getString("string_empty")});
        }
        new Thread(() -> {
            Object[] resources = FileResource.getStoredFileResources(UnderNet.router).toArray();
            EventQueue.invokeLater(() -> resourceList.setListData(resources));
        }).start();
    }

    @Override
    public void onEventCalled(Event e) {
        if (e instanceof RouterStatusEvent) {
            RouterStatusEvent statusEvent = (RouterStatusEvent) e;

            switch (statusEvent.newStatus) {
                case STARTED:
                    publishButton.setEnabled(true);
                    pullButton.setEnabled(true);
                    break;
                case STOPPED:
                    publishButton.setEnabled(false);
                    pullButton.setEnabled(false);
                    break;
                case STARTING:
                    publishButton.setEnabled(false);
                    pullButton.setEnabled(false);
                    break;
                case STOPPING:
                    publishButton.setEnabled(false);
                    pullButton.setEnabled(false);
                    break;
            }
        } else if (e instanceof ResourceTransferStartedEvent) {
            publishButton.setEnabled(false);
            pullButton.setEnabled(false);
        } else if (e instanceof ResourceTransferFinishedEvent) {
            publishButton.setEnabled(true);
            pullButton.setEnabled(true);
            refreshList();
        } else if (e instanceof ResourceTransferErrorEvent) {
            publishButton.setEnabled(true);
            pullButton.setEnabled(true);
        } else if (e instanceof RouterControlLoopEvent) {
            refreshList();
        }
    }

    private void createUIComponents() {
        resourceList = new JList(new Resource[0]);
        resourceList.setCellRenderer(new ResourceListCellRenderer());
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
        panel.setForeground(new Color(-1));
        sectionTitle = new JLabel();
        Font sectionTitleFont = this.$$$getFont$$$("Droid Sans", Font.BOLD, 18, sectionTitle.getFont());
        if (sectionTitleFont != null) sectionTitle.setFont(sectionTitleFont);
        this.$$$loadLabelText$$$(sectionTitle, ResourceBundle.getBundle("language").getString("title_resources"));
        panel.add(sectionTitle, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final DefaultListModel defaultListModel1 = new DefaultListModel();
        resourceList.setModel(defaultListModel1);
        resourceList.setSelectionMode(1);
        scrollPane1.setViewportView(resourceList);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        publishButton = new JButton();
        publishButton.setEnabled(false);
        this.$$$loadButtonText$$$(publishButton, ResourceBundle.getBundle("language").getString("button_publishResource"));
        panel1.add(publishButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pullButton = new JButton();
        pullButton.setEnabled(false);
        this.$$$loadButtonText$$$(pullButton, ResourceBundle.getBundle("language").getString("button_pullResource"));
        panel1.add(pullButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
 * Renders elements within the resource list.
 */
class ResourceListCellRenderer extends DefaultListCellRenderer
{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Resource) {
            Resource resource = (Resource) value;

            if(resource instanceof FileResource) {
                FileResource file = (FileResource) resource;
                setText(file.attributes.get(1));
            } else {
                setText(resource.toString());
            }

            if (isSelected) {
                setBackground(getBackground().darker());
            }
        } else if (value instanceof String) {
            setText((String) value);
        } else {
            setText("UNKNOWN");
        }
        return c;
    }
}
