package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Message containing a resource and its info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ResourceMessage extends MsgBase {
    /**
     * The resource to be pushed.
     */
    public byte[] resource;
    /**
     * The type of the resource.
     */
    public byte resourceType;


    /**
     * Creates a new resource content given the resource.
     * @param resource
     */
    public ResourceMessage(Resource resource) {
        this.resource = resource.send();
        this.resourceType = resource.getResourceType().getValue();
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_PUSH;
    }
}
