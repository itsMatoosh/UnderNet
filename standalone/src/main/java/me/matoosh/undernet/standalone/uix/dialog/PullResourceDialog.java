package me.matoosh.undernet.standalone.uix.dialog;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceType;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

/**
 * A pull resource dialog.
 */
public class PullResourceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonPull;
    private JButton buttonCancel;
    private JTextField filePathField;
    private JButton chooseFileButton;
    private JTextField networkIdField;

    public PullResourceDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonPull);
        centerDialogOnMouse();

        buttonPull.addActionListener(e -> onPull());
        buttonCancel.addActionListener(e -> onCancel());
        chooseFileButton.addActionListener(e -> onChooseSaveDir());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // call onOK() on ENTER
        contentPane.registerKeyboardAction(e -> onPull(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        filePathField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                verifyFields();
            }
        });
        networkIdField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                verifyFields();
            }
        });
    }

    private void onPull() {
        if (!verifyFields()) {
            getToolkit().beep();
            return;
        }

        NetworkID netId = new NetworkID(networkIdField.getText().replaceAll("\\s+", ""));
        UnderNet.router.resourceManager.pull(netId); //Use inputted network id.
        String saveDir = filePathField.getText();

        //Registering the pull event.
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                ResourceTransferFinishedEvent transferFinishedEvent = (ResourceTransferFinishedEvent) e;

                if (transferFinishedEvent.getTransferHandler().getTransferType() == ResourceTransferType.INBOUND && transferFinishedEvent.getTransferHandler().getResource().getNetworkID().equals(netId) && transferFinishedEvent.getTransferHandler().getResource().getInfo().resourceType == ResourceType.FILE) {
                    FileResource fileResource = (FileResource) transferFinishedEvent.getTransferHandler().getResource();

                    //Copying the received file to dest.
                    if (saveDir != null) {
                        try {
                            Files.copy(Paths.get(fileResource.file.getAbsolutePath()), Paths.get(saveDir + "/" + fileResource.file.getName()), StandardCopyOption.REPLACE_EXISTING);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } finally {
                            //File dialog.
                            EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(PullResourceDialog.this, String.format(ResourceBundle.getBundle("language").getString("dialog_pullResource_transferFinished_message"), fileResource.file.getName(), fileResource.getNetworkID().getStringValue(), saveDir), ResourceBundle.getBundle("language").getString("dialog_pullResource_transferFinished_title"), JOptionPane.INFORMATION_MESSAGE));
                        }
                    } else {
                        //File dialog.
                        EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(PullResourceDialog.this, String.format(ResourceBundle.getBundle("language").getString("dialog_pullResource_transferFinished_message"), fileResource.file.getName(), fileResource.getNetworkID().getStringValue(), UnderNet.fileManager.getContentFolder()), ResourceBundle.getBundle("language").getString("dialog_pullResource_transferFinished_title"), JOptionPane.INFORMATION_MESSAGE));
                    }
                }

                //Unregistering the handler.
                EventManager.unregisterHandler(this, ResourceTransferFinishedEvent.class);
            }
        }, ResourceTransferFinishedEvent.class);

        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onChooseSaveDir() {
        //Open file choose dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose the save directory");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getPath());
            verifyFields();
        }
    }

    private boolean verifyFields() {
        boolean correct;

        //network id
        if (networkIdField.getText() != null && !networkIdField.getText().trim().equals("")) {
            correct = new NetworkID(networkIdField.getText().replaceAll("\\s+", "")).isValid();
        } else {
            correct = false;
        }

        //file
        if (filePathField.getText() == null || filePathField.getText().trim().equals(""))
            correct = false;

        buttonPull.setEnabled(correct);
        return correct;
    }

    public static void newInstance() {
        PullResourceDialog dialog = new PullResourceDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Centers the window on mouse.
     */
    private void centerDialogOnMouse() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        setLocation(mousePoint.x - getSize().width / 2, mousePoint.y - getSize().height / 2);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(4, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        filePathField = new JTextField();
        panel1.add(filePathField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        chooseFileButton = new JButton();
        this.$$$loadButtonText$$$(chooseFileButton, ResourceBundle.getBundle("language").getString("dialog_publishResource_chooseFile"));
        panel1.add(chooseFileButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("language").getString("dialog_pullResource_saveTo"));
        panel1.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel2.add(panel3, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonPull = new JButton();
        this.$$$loadButtonText$$$(buttonPull, ResourceBundle.getBundle("language").getString("button_pullResource"));
        panel3.add(buttonPull, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, ResourceBundle.getBundle("language").getString("button_cancel"));
        panel3.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        this.$$$loadLabelText$$$(label2, ResourceBundle.getBundle("language").getString("dialog_pullResource_networkID"));
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        networkIdField = new JTextField();
        panel4.add(networkIdField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JSeparator separator1 = new JSeparator();
        contentPane.add(separator1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
        return contentPane;
    }

}
