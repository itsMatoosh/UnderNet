package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferErrorEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferStartedEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Handles the receiving of a resource.
 */
public abstract class ResourceTransferHandler {

    /**
     * The router used.
     */
    private Router router;

    /**
     * The transferred resource.
     */
    private Resource resource;

    /**
     * The type of the resource transfer.
     */
    private ResourceTransferType transferType;

    /**
     * The tunnel of the resource.
     */
    private MessageTunnel tunnel;

    /**
     * The transfer id of the transfer.
     */
    private int transferId;

    /**
     * Time of the last message.
     */
    private long lastMessageTime;


    public ResourceTransferHandler(Resource resource, ResourceTransferType transferType, MessageTunnel tunnel, int transferId, Router router) {
        this.resource = resource;
        this.transferType = transferType;
        this.tunnel = tunnel;
        this.router = router;
        this.transferId = transferId;

        if(transferType == ResourceTransferType.OUTBOUND) {
            router.resourceManager.outboundHandlers.add(this);
        } else {
            router.resourceManager.inboundHandlers.add(this);
        }

        setLastMessageTime(System.currentTimeMillis() - 10000);

        EventManager.callEvent(new ResourceTransferStartedEvent(this));
    }

    /**
     * Prepares the transfer.
     */
    public abstract void prepare();

    /**
     * Sends chunk with id chunk id.
     *
     * @param chunkId
     */
    public void callSendChunk(int chunkId) {
        lastMessageTime = System.currentTimeMillis();
        sendChunk(chunkId);
    }

    /**
     * Sends chunk with id chunk id.
     *
     * @param chunkId
     */
    public abstract void sendChunk(int chunkId);

    /**
     * Releases all the resources associated with the handler.
     */
    public void close() {
        //Handling close.
        onClose();

        //Closing tunnel.
        router.messageTunnelManager.closeTunnel(this.getTunnel());

        //Removing from the list.
        if (this.getTransferType() == ResourceTransferType.INBOUND) {
            router.resourceManager.inboundHandlers.remove(this);
        } else {
            router.resourceManager.outboundHandlers.remove(this);
        }

        EventManager.callEvent(new ResourceTransferFinishedEvent(this ));
    }

    /**
     * Called when the transfer handler is closed.
     */
    public abstract void onClose();

    /**
     * Called when a resource data message is received.
     * @param dataMessage
     */
    public void callDataReceived(ResourceDataMessage dataMessage) {
        setLastMessageTime(System.currentTimeMillis());
        onDataReceived(dataMessage);
    }

    /**
     * Called when a resource data message is received.
     *
     * @param dataMessage
     */
    public abstract void onDataReceived(ResourceDataMessage dataMessage);

    /**
     * Calls error for the handler.
     */
    public void callError(Exception e) {
        //Handling error.
        onError(e);

        EventManager.callEvent(new ResourceTransferErrorEvent(this , e));

        //Closing the streams.
        this.close();
    }

    /**
     * Called when a resource transfer error occurs.
     */
    public abstract void onError(Exception e);

    public Router getRouter() {
        return router;
    }

    public Resource getResource() {
        return resource;
    }

    public ResourceTransferType getTransferType() {
        return transferType;
    }

    public MessageTunnel getTunnel() {
        return tunnel;
    }

    public void setTunnel(MessageTunnel tunnel) {
        this.tunnel = tunnel;
    }

    public int getTransferId() {
        return transferId;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    @Override
    public String toString() {
        return transferType + " transfer (" + transferId + ") -> " + getResource().getNetworkID().getStringValue();
    }
}
