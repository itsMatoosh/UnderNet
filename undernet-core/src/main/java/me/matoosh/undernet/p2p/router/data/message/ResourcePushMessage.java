package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Message containing resource and its publish info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ResourcePushMessage implements MsgBase {
    /**
     * The resource to be pushed.
     */
    public Resource resource;
    /**
     * The type of the resource.
     */
    public byte resourceType;


    /**
     * Creates a new resource push message given the pushed resource.
     * @param resource
     */
    public ResourcePushMessage(Resource resource) {
        this.resource = resource;
        this.resource.pushMessage = this;
        this.resourceType = resource.getResourceType();
    }
}
