package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;

import java.io.Serializable;
import java.util.concurrent.Future;

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
