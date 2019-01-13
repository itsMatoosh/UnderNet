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
    private int transferId;

    public ResourceInfoMessage(ResourceInfo resourceInfo, int transferId) {
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

    public int getTransferId() {
        return transferId;
    }
}
