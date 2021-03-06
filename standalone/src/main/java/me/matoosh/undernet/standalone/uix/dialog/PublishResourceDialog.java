package me.matoosh.undernet.standalone.uix.dialog;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import me.matoosh.undernet.standalone.uix.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ResourceBundle;

/**
 * The publish resource dialog.
 */
public class PublishResourceDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonPublish;
    private JButton buttonCancel;
    private JTextField filePathField;
    private JButton chooseFileButton;

    public PublishResourceDialog() {
        setContentPane(contentPane);
        setModal(true);
        setTitle(ResourceBundle.getBundle("language").getString("dialog_publishResource"));
        getRootPane().setDefaultButton(buttonPublish);
        centerDialogOnMouse();
        onFileChosen();

        chooseFileButton.addActionListener(e -> onChooseFile());
        buttonPublish.addActionListener(e -> onPublish());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        // call onPublish() on ENTER
        contentPane.registerKeyboardAction(e -> onPublish(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        filePathField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                onFileChosen();
            }
        });
    }

    private void onPublish() {
        //Button clicked. Checking whether a correct path has been chosen.
        if (filePathField.getText() != null && !filePathField.getText().trim().equals("")) {
            new Thread(() -> {
                //Publishing resource on UnderNet.
                FileResource fileResource = new FileResource(UnderNet.router, new File(filePathField.getText().replaceAll("\"", "")));
                if (fileResource.file == null || !fileResource.file.exists()) {
                    EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.instance.frame, String.format(ResourceBundle.getBundle("language").getString("dialog_publishResource_cantAccessFile"), fileResource.file), ResourceBundle.getBundle("language").getString("dialog_publishResource_cantPublishTitle"), JOptionPane.ERROR_MESSAGE));
                    return;
                }

                //Copying the network to clipboard.
                StringSelection stringSelection = new StringSelection(fileResource.getNetworkID().getStringValue());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);

                //Showing the published network id.
                EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.instance.frame, String.format(ResourceBundle.getBundle("language").getString("dialog_publishResource_publishingMessage"), fileResource.file, fileResource.getNetworkID().getStringValue()), ResourceBundle.getBundle("language").getString("dialog_publishResource_publishingTitle"), JOptionPane.INFORMATION_MESSAGE));

                if (!fileResource.copyToContent()) {
                    EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.instance.frame, String.format(ResourceBundle.getBundle("language").getString("dialog_publishResource_cantPublishMessage"), fileResource.file), ResourceBundle.getBundle("language").getString("dialog_publishResource_cantPublishTitle"), JOptionPane.ERROR_MESSAGE));
                    return;
                }
                try {
                    UnderNet.router.resourceManager.publish(fileResource);
                } catch (Exception e) {
                    EventQueue.invokeLater(() -> JOptionPane.showMessageDialog(MainFrame.instance.frame, String.format(ResourceBundle.getBundle("language").getString("dialog_publishResource_cantPublishMessage"), fileResource.file), ResourceBundle.getBundle("language").getString("dialog_publishResource_cantPublishTitle"), JOptionPane.ERROR_MESSAGE));
                    return;
                }
            }).start();

            dispose();
        } else {
            getToolkit().beep();
        }
    }

    private void onCancel() {
        dispose();
    }

    private void onChooseFile() {
        //Open file choose dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getPath());
            onFileChosen();
        }
    }

    /**
     * Called when the file field is modified.
     */
    private void onFileChosen() {
        if (filePathField.getText() != null && filePathField.getText().trim() != "") {
            buttonPublish.setEnabled(true);
        } else {
            buttonPublish.setEnabled(false);
        }
    }

    /**
     * Centers the window on mouse.
     */
    private void centerDialogOnMouse() {
        Point mousePoint = MouseInfo.getPointerInfo().getLocation();
        setLocation(mousePoint.x - getWidth() / 2, mousePoint.y - getHeight() / 2);
    }

    public static void newInstance() {
        PublishResourceDialog dialog = new PublishResourceDialog();
        dialog.pack();
        dialog.setVisible(true);
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
        contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(10, 10, 10, 10), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, 1, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel1.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonPublish = new JButton();
        this.$$$loadButtonText$$$(buttonPublish, ResourceBundle.getBundle("language").getString("button_publishResource"));
        panel2.add(buttonPublish, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonCancel = new JButton();
        this.$$$loadButtonText$$$(buttonCancel, ResourceBundle.getBundle("language").getString("button_cancel"));
        panel2.add(buttonCancel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        filePathField = new JTextField();
        panel4.add(filePathField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        chooseFileButton = new JButton();
        this.$$$loadButtonText$$$(chooseFileButton, ResourceBundle.getBundle("language").getString("dialog_publishResource_chooseFile"));
        panel4.add(chooseFileButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        this.$$$loadLabelText$$$(label1, ResourceBundle.getBundle("language").getString("dialog_publishResource_file"));
        panel4.add(label1, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JSeparator separator1 = new JSeparator();
        contentPane.add(separator1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
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
