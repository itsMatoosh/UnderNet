package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.ResourceInfoMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents a stored resource.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public abstract class Resource implements Serializable {
    /**
     * The network id of this resource.
     */
    private NetworkID networkID;

    /**
     * The owner of the resource.
     */
    private NetworkID owner;

    /**
     * The router of this resource.
     */
    private Router router;

    /**
     * The attributes of the resource.
     */
    public HashMap<Integer, String> attributes = new HashMap<>();

    /**
     * Creates a new resource instance.
     *
     * @param router
     */
    public Resource(Router router) {
        this.router = router;
    }

    /**
     * Calculates the network id of the resource based on its contents.
     */
    public abstract void calcNetworkId();

    /**
     * Gets the network id.
     *
     * @return
     */
    public NetworkID getNetworkID() {
        return this.networkID;
    }

    /**
     * Sets the network id.
     *
     * @param id
     */
    public void setNetworkID(NetworkID id) {
        if (id.isValid()) {
            this.networkID = id;
        } else {
            ResourceManager.logger.info("Can't set resource id to: {}, invalid ID", id);
        }
    }

    /**
     * Gets the owner.
     */
    public NetworkID getOwner() {
        return this.owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner
     */
    public void setOwner(NetworkID owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "networkID=" + networkID +
                '}';
    }

    /**
     * Checks whether the resource is present in the self node.
     *
     * @return
     */
    public abstract boolean isLocal();

    /**
     * Gets the type of the resource.
     *
     * @return
     */
    abstract ResourceType getResourceType();

    /**
     * Gets the attributes of the resource.
     *
     * @return
     */
    abstract void updateAttributes();

    /**
     * Gets the resource transfer handler.
     */
    public abstract ResourceTransferHandler getTransferHandler(ResourceTransferType resourceTransferType, MessageTunnel tunnel, int transferId, Router router);

    /**
     * Gets the router.
     *
     * @return
     */
    public Router getRouter() {
        return this.router;
    }

    /**
     * Gets the resource info.
     *
     * @return
     */
    public ResourceInfo getInfo() {
        return new ResourceInfo(this);
    }

    /**
     * Sends resource info the destination.
     *
     * @param tunnel
     * @param transferId
     */
    public void sendInfo(MessageTunnel tunnel, int transferId) {
        tunnel.sendMessage(new ResourceInfoMessage(getInfo(), transferId));
    }

    /**
     * Clears the resource if it's available locally.
     */
    public abstract void clear();
}
