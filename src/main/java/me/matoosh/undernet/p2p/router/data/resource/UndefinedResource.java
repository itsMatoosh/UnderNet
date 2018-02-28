package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Represents a resource whose type is undefined.
 * Created by Mateusz Rębacz on 12.11.2017.
 */

public class UndefinedResource extends Resource {
    @Override
    public void calcNetworkId() {
        throw new NotImplementedException();
    }

    @Override
    public byte getResourceType() {
        return -1;
    }

    @Override
    public void onPush(ResourcePushMessage msg, Node pushTo) {
        
    }

    @Override
    public void onPushReceive(ResourcePushMessage msg, Node receivedFrom) {

    }
}
