package me.matoosh.undernet.standalone.uix;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataReceivedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataSentEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.event.router.RouterErrorEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.data.resource.transfer.FileTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import me.matoosh.undernet.standalone.serialization.SerializationTools;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.ResourceBundle;

public class MainFrame extends EventHandler {

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(MainFrame.class);

    /**
     * The instance of the class.
     */
    public static MainFrame instance;

    /**
     * The frame.
     */
    public JFrame frame;

    private JPanel panel;
    private ResourcePanel resourcePanel;
    private NodePanel nodePanel;
    private TunnelPanel tunnelPanel;
    private JProgressBar progressBar;
    private JButton mainButton;
    private VisualPanel visualPanel;
    private TransferPanel transferPanel;

    //top menu
    JMenuBar menuBar;
    JMenu identityMenu;
    JMenuItem newIdentityOption;
    JMenuItem changeIdentityOption;

    public static final int START_HEIGHT = 600;
    public static final int START_WIDTH = 950;

    /**
     * Whether the app is running on mac.
     */
    public static boolean IS_MAC;

    public MainFrame() {
        $$$setupUI$$$();
        mainButton.addActionListener(e -> onMainButtonClicked());
    }

    public static void newInstance() {
        if (instance != null) {
            logger.warn("Can't open more than one Mainframe!");
            return;
        }
        logger.info("Opening the Mainframe...");
        instance = new MainFrame();

        instance.frame = new JFrame("UnderNet");
        instance.frame.setContentPane(instance.panel);
        instance.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        instance.initialize();
        instance.frame.pack();
        instance.frame.setLocationRelativeTo(null);
        instance.frame.setSize(START_WIDTH, START_HEIGHT);

        instance.frame.setVisible(true);
    }

    private void initialize() {
        //Check if running on a mac.
        String lcOSName = System.getProperty("os.name").toLowerCase();
        IS_MAC = lcOSName.startsWith("mac os x");

        setLook();
        registerListener();
        addMenus();
    }

    private void setLook() {
        if (IS_MAC) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "UnderNet");
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }
    }

    private void registerListener() {
        EventManager.registerHandler(this, RouterStatusEvent.class);
        EventManager.registerHandler(this, RouterErrorEvent.class);
        EventManager.registerHandler(this, ResourceTransferDataReceivedEvent.class);
        EventManager.registerHandler(this, ResourceTransferDataSentEvent.class);
        EventManager.registerHandler(this, ResourceTransferFinishedEvent.class);
    }

    private void addMenus() {
        menuBar = new JMenuBar();
        identityMenu = new JMenu(ResourceBundle.getBundle("language").getString("menu_identity"));
        newIdentityOption = new JMenuItem(ResourceBundle.getBundle("language").getString("menu_identity_new"));
        newIdentityOption.addActionListener(e -> newIdentityPressed());
        changeIdentityOption = new JMenuItem(ResourceBundle.getBundle("language").getString("menu_identity_change"));
        changeIdentityOption.addActionListener(e -> changeIdentityPressed());
        identityMenu.add(newIdentityOption);
        identityMenu.add(changeIdentityOption);
        menuBar.add(identityMenu);
        frame.setJMenuBar(menuBar);
    }

    private void newIdentityPressed() {
        NetworkIdentity identity = new NetworkIdentity();

        //Open file choose dialog
        File saveFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Identity Files", "id", "identity");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            saveFile = fileChooser.getSelectedFile();
            if (FilenameUtils.getExtension(saveFile.getPath()) != ".id") {
                saveFile = new File(saveFile.getPath() + ".id");
            }
        }

        if (saveFile == null) return;

        SerializationTools.writeObjectToFile(identity, saveFile);
        UnderNetStandalone.setNetworkIdentity(identity, saveFile);
    }

    private void changeIdentityPressed() {
        //Open file choose dialog
        File openFile = null;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Identity Files", "id", "identity");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            openFile = fileChooser.getSelectedFile();
        }

        if (openFile == null || !openFile.exists()) return;

        NetworkIdentity identity = (NetworkIdentity) SerializationTools.readObjectFromFile(openFile);
        UnderNetStandalone.setNetworkIdentity(identity, openFile);
    }

    /**
     * Called when the main button is clicked.
     */
    public void onMainButtonClicked() {
        if (UnderNet.router.status == InterfaceStatus.STOPPED)
            UnderNetStandalone.connect();
        else if (UnderNet.router.status == InterfaceStatus.STARTED)
            UnderNetStandalone.disconnect();
    }

    @Override
    public void onEventCalled(Event e) {
        //router status event
        if (e instanceof RouterStatusEvent) {
            RouterStatusEvent statusEvent = (RouterStatusEvent) e;

            switch (statusEvent.newStatus) {
                case STOPPED:
                    mainButton.setEnabled(true);
                    identityMenu.setEnabled(true);
                    mainButton.setText(ResourceBundle.getBundle("language").getString("button_connect"));
                    this.frame.repaint();
                    break;
                case STARTED:
                    instance.mainButton.setEnabled(true);
                    instance.identityMenu.setEnabled(false);
                    instance.mainButton.setText(ResourceBundle.getBundle("language").getString("button_disconnect"));
                    break;
                case STOPPING:
                    mainButton.setEnabled(false);
                    identityMenu.setEnabled(false);
                    break;
                case STARTING:
                    mainButton.setEnabled(false);
                    identityMenu.setEnabled(false);
                    new Thread(() -> drawLoop()).start();
                    break;
            }
        } else if (e instanceof ResourceTransferDataReceivedEvent) {
            ResourceTransferDataReceivedEvent dataReceivedEvent = (ResourceTransferDataReceivedEvent) e;
            ResourceTransferHandler transferHandler = dataReceivedEvent.getTransferHandler();

            if (transferHandler instanceof FileTransferHandler) {
                FileTransferHandler fileTransferHandler = (FileTransferHandler) transferHandler;

                progressBar.setValue((int) (((float) fileTransferHandler.getWritten()) / ((float) fileTransferHandler.getFileLength()) * 100f));
            }
        } else if (e instanceof ResourceTransferDataSentEvent) {
            ResourceTransferDataSentEvent dataReceivedEvent = (ResourceTransferDataSentEvent) e;
            ResourceTransferHandler transferHandler = dataReceivedEvent.getTransferHandler();

            if (transferHandler instanceof FileTransferHandler) {
                FileTransferHandler fileTransferHandler = (FileTransferHandler) transferHandler;

                progressBar.setValue((int) (((float) fileTransferHandler.getSent()) / ((float) fileTransferHandler.getFileLength()) * 100f));
            }
        } else if (e instanceof ResourceTransferFinishedEvent) {
            ResourceTransferFinishedEvent transferFinishedEvent = (ResourceTransferFinishedEvent) e;
            ResourceTransferHandler transferHandler = transferFinishedEvent.getTransferHandler();

            if (transferHandler instanceof FileTransferHandler) {
                progressBar.setValue(0);
            }
        } else if (e instanceof RouterErrorEvent) {
            RouterErrorEvent errorEvent = (RouterErrorEvent) e;

            if (errorEvent.shouldReconnect) {
                JOptionPane.showMessageDialog(frame,
                        errorEvent.exception.getLocalizedMessage() + "\n" + ResourceBundle.getBundle("language").getString("dialog_router_error_reconnecting"),
                        ResourceBundle.getBundle("language").getString("dialog_router_error_title"),
                        JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame,
                        errorEvent.exception.getLocalizedMessage(),
                        ResourceBundle.getBundle("language").getString("dialog_router_error_title"),
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * The draw loop logic.
     */
    private void drawLoop() {
        int FRAMES_PER_SECOND = 30;
        int SKIP_TICKS = 1000 / FRAMES_PER_SECOND;

        long next_game_tick = System.currentTimeMillis();
        long sleep_time = 0;

        while (UnderNet.router != null && UnderNet.router.status != InterfaceStatus.STOPPED) {
            next_game_tick += SKIP_TICKS;
            sleep_time = next_game_tick - System.currentTimeMillis();
            if (sleep_time >= 0) {
                try {
                    visualPanel.getPanel().repaint();
                    Thread.sleep(sleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void createUIComponents() {
        mainButton = new JButton(ResourceBundle.getBundle("language").getString("button_connect"));
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
        panel.setLayout(new GridLayoutManager(6, 5, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        panel.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        resourcePanel = new ResourcePanel();
        panel.add(resourcePanel.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), null, null, 0, false));
        nodePanel = new NodePanel();
        panel.add(nodePanel.$$$getRootComponent$$$(), new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(300, -1), null, null, 0, false));
        panel.add(mainButton, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 50), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel.add(spacer2, new GridConstraints(3, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        panel.add(spacer3, new GridConstraints(1, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        progressBar = new JProgressBar();
        panel.add(progressBar, new GridConstraints(5, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer4 = new Spacer();
        panel.add(spacer4, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        visualPanel = new VisualPanel();
        panel.add(visualPanel.$$$getRootComponent$$$(), new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(350, -1), null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 5, 0, 5), -1, -1));
        panel.add(panel1, new GridConstraints(2, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        transferPanel = new TransferPanel();
        panel1.add(transferPanel.$$$getRootComponent$$$(), new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tunnelPanel = new TunnelPanel();
        panel1.add(tunnelPanel.$$$getRootComponent$$$(), new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
