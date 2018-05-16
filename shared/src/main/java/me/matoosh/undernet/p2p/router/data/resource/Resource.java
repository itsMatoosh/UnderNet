package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

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
    public abstract ResourceType getResourceType();

    /**
     * Handles the sending of a resource.
     */
    public abstract void send(Node recipient, IResourceActionListener resourceActionListener);

    /**
     * Handles the receiving of a resource.
     * @param sender
     */
    public abstract void receive(Node sender, IResourceActionListener resourceActionListener);

    @Override
    public String toString() {
        return "Resource{" +
                "networkID=" + networkID +
                '}';
    }

    /**
     * Checks whether the resource is present in the self node.
     * @return
     */
    public abstract boolean isLocal();

    /**
     * Gets the resource's friendly display name.
     * @return
     */
    public abstract String getDisplayName();

    /**
     * Listens for the finishing of a resource action.
     */
    public interface IResourceActionListener {
        /**
         * Called when the action is finished.
         * @param other the node associated with the action.
         */
        public void onFinished(Node other);
    }
}
