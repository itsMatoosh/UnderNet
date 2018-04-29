package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Message containing a resource and its info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ResourceMessage extends MsgBase {
    /**
     * The resource to be pushed.
     */
    public Resource resource;
    /**
     * The type of the resource.
     */
    public byte resourceType;


    /**
     * Creates a new resource content given the resource.
     * @param resource
     */
    public ResourceMessage(Resource resource) {
        this.resource = resource;
        this.resourceType = resource.getResourceType();
    }
}
