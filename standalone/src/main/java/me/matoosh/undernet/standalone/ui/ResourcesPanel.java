package me.matoosh.undernet.standalone.ui;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferStartedEvent;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;
import me.matoosh.undernet.standalone.serialization.SerializationTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * List of resources owned by the node within the current community.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class ResourcesPanel extends JPanel {
    /**
     * The list of nodes.
     */
    private JList resourcesList;

    /**
     * The resources currently owned by self.
     */
    public ArrayList<Resource> resourceCache = new ArrayList<>();

    public ResourcesPanel() {
        //Loading the resource cache.
        loadResourceCache();
        if(resourceCache == null) {
            resourceCache = new ArrayList<>();
        }

        //Setting to gridbaglayout.
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Adding the label
        add(new JLabel("My Resources"));

        //Adding the list.
        resourcesList = new JList(new Resource[] {});
        resourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resourcesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    //Double-click detected
                    if(UnderNet.router.status.equals(InterfaceStatus.STARTED)) {
                        int index = list.locationToIndex(evt.getPoint());
                        if(index < 0) return;
                        Resource resource = (Resource) resourcesList.getModel().getElementAt(index);

                        if(resource.isLocal()) return; //Already pulled
                        //Pulling the resource.
                        UnderNet.router.resourceManager.pull(resource.getNetworkID());
                    }
                }
            }
        });
        add(new JScrollPane(resourcesList));

        //Setting cell renderer for the nodes list.
        resourcesList.setCellRenderer(new ResourcesListCellRenderer(this));

        //Adding the buttons on the bottom.
        JPanel buttonsDrawer = new JPanel();
        buttonsDrawer.setMaximumSize(new Dimension(100 ,50));
        buttonsDrawer.setLayout(new GridLayout());


        //Refreshing the nodes list.
        refreshResourcesList();
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(me.matoosh.undernet.event.Event e) {
                ResourceTransferStartedEvent transferStartedEvent = (ResourceTransferStartedEvent) e;
                if(transferStartedEvent.transferHandler.transferType == ResourceTransferType.OUTBOUND) {
                    addCachedResource(transferStartedEvent.transferHandler.resource);
                    refreshResourcesList();
                }
            }
        }, ResourceTransferStartedEvent.class);
    }

    /**
     * Refreshes the resources list.
     */
    private void refreshResourcesList() {
        if(resourceCache != null) {
            resourcesList.setListData(resourceCache.toArray());
        }
    }
    private void addCachedResource(Resource resource) {
        for (int i = 0; i < resourceCache.size(); i++) {
            if(resource.getNetworkID().equals(resourceCache.get(i).getNetworkID())) {
                return;
            }
        }

        resourceCache.add(resource);

        saveResourceCache();
    }
    private void removeCachedResource(Resource resource) {
        resourceCache.remove(resource);

        saveResourceCache();
    }
    private void loadResourceCache() {
        File resourcesFile = new File(UnderNet.fileManager.getCacheFolder() + "/owned.resources");
        this.resourceCache = (ArrayList<Resource>) SerializationTools.readObjectFromFile(resourcesFile);
        if(this.resourceCache == null && resourcesFile.exists()) {
            resourcesFile.delete();
        }
    }
    private void saveResourceCache() {
        SerializationTools.writeObjectToFile(resourceCache, new File(UnderNet.fileManager.getCacheFolder() + "/owned.resources"));
    }
}

/**
 * Renders the resource cells.
 */
class ResourcesListCellRenderer extends DefaultListCellRenderer
{
    private ResourcesPanel resourcesPanel;

    public ResourcesListCellRenderer(ResourcesPanel panel) {
        this.resourcesPanel = panel;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof Resource) {
            Resource resource = (Resource) value;
            setText(resource.getNetworkID().toString());
            if(resource.isLocal()) {
                setBackground(Color.GREEN);
            } else {
                setBackground(Color.CYAN);
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
