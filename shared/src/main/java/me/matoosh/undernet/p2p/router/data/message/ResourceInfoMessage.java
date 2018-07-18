package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.resource.ResourceInfo;

/**
 * A message with resource info.
 */
public class ResourceInfoMessage extends MsgBase {
    /**
     * The resource info.
     */
    public ResourceInfo resourceInfo;

    /**
     * The transfer id.
     */
    public byte transferId;

    public ResourceInfoMessage(ResourceInfo resourceInfo, byte transferId) {
        this.resourceInfo = resourceInfo;
        this.transferId = transferId;
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_INFO;
    }
}
