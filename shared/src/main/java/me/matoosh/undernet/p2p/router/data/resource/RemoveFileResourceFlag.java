package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

import java.io.File;

/**
 * Flag for removing a specific resource.
 */
public class RemoveFileResourceFlag extends FlagResource {

    /**
     * The id of the resource to remove.
     */
    public NetworkID toRemove;

    /**
     * Creates a new remove resource flag.
     * @param resourceToRemove
     */
    public RemoveFileResourceFlag(NetworkID resourceToRemove) {
        this.toRemove = resourceToRemove;
    }

    @Override
    public void calcNetworkId() {}

    @Override
    public ResourceType getResourceType() {
        return ResourceType.RM_FILE_FLAG;
    }

    @Override
    public void send(Node recipient, IResourceActionListener resourceActionListener) {
        resourceActionListener.onFinished(recipient);
    }

    @Override
    public void receive(Node sender, IResourceActionListener resourceActionListener) {
        //Removing the resource from [self]
        for (FileResource file : UnderNet.router.resourceManager.getStoredFileResources()) {
            if(file.getNetworkID().equals(this.toRemove)) {
                new File(UnderNet.fileManager.getContentFolder() + "/" + file.fileInfo.fileName).delete();
            }
        }
        resourceActionListener.onFinished(sender);
    }

    /**
     * Returns the type of the resource and net id of the deleted object..
     * @return
     */
    @Override
    public String getDisplayName() {
        return "RM_FILE: " + this.toRemove.getStringValue();
    }
}
