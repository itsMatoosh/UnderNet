package me.matoosh.undernet.standalone.ui;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.client.ClientExceptionEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.event.server.ServerExceptionEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.standalone.UnderNetStandalone;
import me.matoosh.undernet.standalone.serialization.SerializationTools;
import me.matoosh.undernet.standalone.ui.dialog.PullResourceDialog;
import me.matoosh.undernet.standalone.ui.dialog.UploadResourceDialog;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

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
    public ResourcesPanel resourcesPanel;

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
     * The generate identity menu item.
     */
    private JMenuItem identityNewItem;

    /**
     * The resource menu of the frame.
     */
    private JMenu resourceMenu;
    /**
     * The resource upload menu item.
     */
    private JMenuItem resourcePublishItem;
    /**
     * The resource pull menu item.
     */
    private JMenuItem resourcePullItem;


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
                //Opening file open dialog.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choose the identity to use");
                fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
                FileFilter filter = new FileNameExtensionFilter("Identity Files", "id");
                fileChooser.setFileFilter(filter);
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setCurrentDirectory(UnderNet.fileManager.getAppFolder());
                if(fileChooser.showSaveDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
                    //Reading the identity file.
                    NetworkIdentity identity = (NetworkIdentity)SerializationTools.readObjectFromFile(fileChooser.getSelectedFile());

                    if(identity == null) {
                        //Incorrect file.
                        JOptionPane.showMessageDialog(AppFrame.this, String.format("Couldn't read the identity file %s", fileChooser.getSelectedFile()),"Can't read identity!", JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    //User chose the save option.
                    SerializationTools.writeObjectToFile(identity, fileChooser.getSelectedFile());

                    //Setting the new id.
                    UnderNetStandalone.setNetworkIdentity(identity, fileChooser.getSelectedFile());
                }
            }
        });
        identityMenu.add(identityChangeItem);
        identityNewItem = new JMenuItem("New identity");
        identityNewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Generating the new identity.
                NetworkIdentity newIdentity = new NetworkIdentity();

                //Opening file save dialog.
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save the new identity");
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                FileFilter filter = new FileNameExtensionFilter("Identity Files", "id");
                fileChooser.setFileFilter(filter);
                fileChooser.addChoosableFileFilter(filter);
                fileChooser.setCurrentDirectory(UnderNet.fileManager.getAppFolder());
                if(fileChooser.showSaveDialog(AppFrame.this) == JFileChooser.APPROVE_OPTION) {
                    //User chose the save option.
                    File file = fileChooser.getSelectedFile();
                    if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("id")) {
                        file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName())+".id"); //Remove the extension (if any) and replace it with ".id"
                    }

                    SerializationTools.writeObjectToFile(newIdentity, file);

                    //Setting the new id.
                    UnderNetStandalone.setNetworkIdentity(newIdentity, file);
                }
            }
        });
        identityMenu.add(identityNewItem);

        //Resource menu
        resourceMenu = new JMenu("Resource");
        menuBar.add(resourceMenu);
        resourcePublishItem = new JMenuItem("Publish resource");
        resourcePublishItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JDialog resourceUploadDialog = new UploadResourceDialog(UnderNetStandalone.mainAppFrame);
                resourceUploadDialog.setVisible(true);
            }
        });
        resourcePublishItem.setEnabled(false);
        resourceMenu.add(resourcePublishItem);

        resourcePullItem = new JMenuItem("Pull resource");
        resourcePullItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JDialog resourcePullDialog = new PullResourceDialog(UnderNetStandalone.mainAppFrame);
                resourcePullDialog.setVisible(true);
            }
        });
        resourcePullItem.setEnabled(false);
        resourceMenu.add(resourcePullItem);

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
        resourcesPanel = new ResourcesPanel();
        resourcesPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        add(resourcesPanel, new GridBagConstraints(0, 0, 1, 1, 0.125, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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

        //Status event handler.
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

                        resourcePublishItem.setEnabled(false);
                        resourcePullItem.setEnabled(false);
                        break;
                    case STARTING:
                        connectButton.setEnabled(false);
                        resourcePublishItem.setEnabled(false);
                        resourcePullItem.setEnabled(false);
                        break;
                    case STARTED:
                        connectButton.setText("Disconnect");
                        connectButton.setEnabled(true);
                        for( ActionListener al : connectButton.getActionListeners() ) {
                            connectButton.removeActionListener( al );
                        }
                        connectButton.addActionListener(disconnectActionListener);

                        resourcePublishItem.setEnabled(true);
                        resourcePullItem.setEnabled(true);
                        break;
                    case STOPPING:
                        connectButton.setEnabled(false);
                        resourcePublishItem.setEnabled(false);
                        resourcePullItem.setEnabled(false);
                        break;
                }
            }
        }, RouterStatusEvent.class);

        //Error events handlers.
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                JOptionPane.showMessageDialog(AppFrame.this, ((ClientExceptionEvent)e).exception.getMessage() + "\nThe router will stop.", "Error with client", JOptionPane.ERROR_MESSAGE);
            }
        }, ClientExceptionEvent.class);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                JOptionPane.showMessageDialog(AppFrame.this, ((ClientExceptionEvent)e).exception.getMessage() + "\nThe router will stop.", "Error with server", JOptionPane.ERROR_MESSAGE);
            }
        }, ServerExceptionEvent.class);

        add(connectButton, new GridBagConstraints(0, 1, 3, 1, 1, 0.05, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
}
