package me.matoosh.undernet.p2p.router.data.resource;

import java.io.Serializable;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;

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
    public abstract void onPush(ResourcePushMessage msg, Node pushTo);
    /**
     * Called after the resource push has been received.
     * @param receivedFrom
     */
    public abstract void onPushReceive(ResourcePushMessage msg, Node receivedFrom);
}
