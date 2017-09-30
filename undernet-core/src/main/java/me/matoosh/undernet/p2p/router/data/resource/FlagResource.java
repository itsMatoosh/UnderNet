package me.matoosh.undernet.p2p.router.data.resource;

import java.util.concurrent.Callable;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a flag resource.
 * Flag resources have a set expiration and can contain routing information.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FlagResource extends Resource {
    /**
     * Calculates the network id of the resource based on its contents.
     */
    @Override
    public void calcNetworkId() {
        throw new NotImplementedException();
    }

    /**
     * Returns the type of the resource. E.g file resource.
     *
     * @return
     */
    @Override
    public byte getResourceType() {
        return 1;
    }

    /**
     * Called before the resource is pushed.
     *
     * @param msg
     * @param pushTo
     */
    @Override
    public Callable onPush(ResourcePushMessage msg, Node pushTo) {
        throw new NotImplementedException();
    }

    /**
     * Called after the resource push has been received.
     *
     * @param msg
     * @param receivedFrom
     */
    @Override
    public Callable onPushReceive(ResourcePushMessage msg, Node receivedFrom) {
        throw new NotImplementedException();
    }
}
