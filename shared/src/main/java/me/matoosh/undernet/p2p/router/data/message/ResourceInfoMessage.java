package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.resource.ResourceInfo;

/**
 * A message with resource info.
 */
public class ResourceInfoMessage extends MsgBase {
    /**
     * The resource info.
     */
    private ResourceInfo resourceInfo;

    /**
     * The transfer id.
     */
    private byte transferId;

    public ResourceInfoMessage(ResourceInfo resourceInfo, byte transferId) {
        this.resourceInfo = resourceInfo;
        this.transferId = transferId;
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_INFO;
    }

    public ResourceInfo getResourceInfo() {
        return resourceInfo;
    }

    public byte getTransferId() {
        return transferId;
    }
}
