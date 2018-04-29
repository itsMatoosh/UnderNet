package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;

import java.io.Serializable;

/**
 * Represents a stored resource.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public abstract class Resource implements Serializable {
    /**
     * The network id of this resource.
     */
    public NetworkID networkID;

    /**
     * Calculates the network id of the resource based on its contents.
     */
    public abstract void calcNetworkId();
    /**
     * Returns the type of the resource. E.g file resource.
     * @return
     */
    public abstract byte getResourceType();

    /**
     * Called before the resource is pushed.
     */
    public abstract void onPush(ResourceMessage msg, Node pushTo);
    /**
     * Called after the resource push has been received.
     * @param receivedFrom
     */
    public abstract void onPushReceive(ResourceMessage msg, Node receivedFrom);
    /**
     * Called when the resource is ready to be pushed.
     */
    public void onPushReady() {
        UnderNet.router.resourceManager.pushForward(new ResourceMessage(this));
    }

    /**
     * Called before the resource is pull from the next closest node.
     * @param msg
     * @param pullFrom
     */
    public abstract void onPull(ResourceMessage msg, Node pullFrom);
    /**
     * Called when a pull request is received.
     * @param msg
     * @param receivedFrom
     */
    public abstract void onPullReceived(ResourceMessage msg, Node receivedFrom);

    @Override
    public String toString() {
        return "Resource{" +
                "networkID=" + networkID +
                '}';
    }
}
