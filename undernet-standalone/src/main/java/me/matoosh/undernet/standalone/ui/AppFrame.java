package me.matoosh.undernet.standalone.ui;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import me.matoosh.undernet.standalone.ui.dialog.ChangeIdentityDialog;
import me.matoosh.undernet.standalone.ui.dialog.UploadResourceDialog;

/**
 * The main frame of the app.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class AppFrame extends JFrame {
    /**
     * The simulation panel.
     */
    public static ContentPanel contentPanel;
    /**
     * The list of nodes.
     */
    public NodesPanel nodesPanel;
    /**
     * The list of communities.
     */
    public SectionsPanel sectionsPanel;

    /**
     * The menu bar of the frame.
     */
    private JMenuBar menuBar;
    /**
     * The nodes menu of the frame.
     */
    private JMenu nodesMenu;
    /**
     * The add node menu item.
     */
    private JMenuItem addNodeMenuItem;

    /**
     * The identity menu of the frame.
     */
    private JMenu identityMenu;
    /**
     * The change identity menu item.
     */
    private JMenuItem identityChangeItem;

    /**
     * The resource menu of the frame.
     */
    private JMenu resourceMenu;
    /**
     * The resource upload menu item.
     */
    private JMenuItem resourceUploadItem;


    public AppFrame() {
        super("UnderNet");
        setSize(800, 600);

        centerWindow();

        setLayout(new GridBagLayout());
        addMenus();
        addPanels();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Centers the window on the screen.
     */
    private void centerWindow() {
        //Getting the center of the screen.
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        setLocation(screenWidth/2 - getSize().width/2, screenHeight/2 - getSize().height/2);
    }

    /**
     * Adds menus to the frame.
     */
    private void addMenus() {
        //Adding the menus.
        menuBar = new JMenuBar();
        add(menuBar);

        //Nodes menu.
        nodesMenu = new JMenu("Nodes");
        menuBar.add(nodesMenu);
        addNodeMenuItem = new JMenuItem("Add node");
        addNodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                nodesPanel.openNodeAddDialog();
            }
        });
        nodesMenu.add(addNodeMenuItem);

        //Identity menu
        identityMenu = new JMenu("Identity");
        menuBar.add(identityMenu);
        identityChangeItem = new JMenuItem("Change identity");
        identityChangeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JDialog identityChangeDialog = new ChangeIdentityDialog(UnderNetStandalone.mainAppFrame);
                identityChangeDialog.setVisible(true);
            }
        });
        identityMenu.add(identityChangeItem);

        //Resource menu
        resourceMenu = new JMenu("Resource");
        menuBar.add(resourceMenu);
        resourceUploadItem = new JMenuItem("Upload resource");
        resourceUploadItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JDialog resourceUploadDialog = new UploadResourceDialog(UnderNetStandalone.mainAppFrame);
                resourceUploadDialog.setVisible(true);
            }
        });
        resourceMenu.add(resourceUploadItem);

        setJMenuBar(menuBar);
    }
    /**
     * Adds the app panels.
     */
    private void addPanels() {
        //Adding the content panel, nodes list and communities list.
        contentPanel = new ContentPanel();
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        nodesPanel = new NodesPanel();
        nodesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        sectionsPanel = new SectionsPanel();
        sectionsPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        add(sectionsPanel, new GridBagConstraints(0, 0, 1, 1, 0.125, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(contentPanel, new GridBagConstraints(1, 0, 1, 1, 0.75, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        add(nodesPanel, new GridBagConstraints(2, 0, 1, 1, 0.125, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //Connect button.
        final JButton connectButton = new JButton("Connect");
        final ActionListener connectActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UnderNet.connect(UnderNetStandalone.networkIdentity);
            }
        };
        final ActionListener disconnectActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UnderNet.disconnect();
            }
        };
        connectButton.addActionListener(connectActionListener);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                RouterStatusEvent statusEvent = (RouterStatusEvent)e;

                switch(statusEvent.newStatus) {
                    case STOPPED:
                        connectButton.setEnabled(true);
                        connectButton.setText("Connect");
                        for( ActionListener al : connectButton.getActionListeners() ) {
                            connectButton.removeActionListener( al );
                        }
                        connectButton.addActionListener(connectActionListener);
                        break;
                    case STARTING:
                        connectButton.setEnabled(false);
                        break;
                    case STARTED:
                        connectButton.setText("Disconnect");
                        connectButton.setEnabled(true);
                        for( ActionListener al : connectButton.getActionListeners() ) {
                            connectButton.removeActionListener( al );
                        }
                        connectButton.addActionListener(disconnectActionListener);
                        break;
                    case STOPPING:
                        connectButton.setEnabled(false);
                        break;
                }
            }
        }, RouterStatusEvent.class);

        add(connectButton, new GridBagConstraints(0, 1, 3, 1, 1, 0.05, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
}
