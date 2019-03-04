package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.resource.ResourceInfo;

/**
 * A message with resource info.
 */
public class ResourceInfoMessage extends StandardSerializedMsgBase {
    /**
     * The resource info.
     */
    private ResourceInfo resourceInfo;

    /**
     * The transfer id.
     */
    private int transferId;

    public ResourceInfoMessage(){}

    public ResourceInfoMessage(ResourceInfo resourceInfo, int transferId) {
        this.resourceInfo = resourceInfo;
        this.transferId = transferId;
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_INFO;
    }

    @Override
    void restoreValues(StandardSerializedMsgBase serializedMsgBase) {
        ResourceInfoMessage infoMessage = (ResourceInfoMessage) serializedMsgBase;
        this.resourceInfo = infoMessage.resourceInfo;
        this.transferId = infoMessage.transferId;
        if(resourceInfo == null) System.out.println("NULL");
        System.out.println("Restored resource info: " + transferId);
    }

    public ResourceInfo getResourceInfo() {
        return resourceInfo;
    }

    public int getTransferId() {
        return transferId;
    }
}
