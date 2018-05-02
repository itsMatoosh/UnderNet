package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;

/**
 * Represents a resource whose type is undefined.
 * The undefined resource is used during resource pull, before the type of the pulled resource is determined.
 * Created by Mateusz Rębacz on 12.11.2017.
 */

public class AbstractResource extends Resource {

    /**
     * Creates an undefined resource given its network id.
     * @param networkID
     */
    public AbstractResource(NetworkID networkID) {
        this.networkID = networkID;
    }

    @Override
    public void calcNetworkId() {
        return;
    }

    @Override
    public byte getResourceType() {
        return -1;
    }

    @Override
    public void onPushSend(ResourceMessage msg, Node pushTo) {
        //Won't be used. Can't push an abstract resource.
    }

    @Override
    public void onPushReceive(ResourceMessage msg, Node receivedFrom) {
        //Won't be used. Can't push an abstract resource.
    }

    @Override
    public void onPullSend(ResourceMessage msg, Node pullFrom) {

    }

    @Override
    public void onPullReceived(ResourceMessage msg, Node receivedFrom) {
        System.out.println("Resource pull received.");
        onPullReady();
    }
}
