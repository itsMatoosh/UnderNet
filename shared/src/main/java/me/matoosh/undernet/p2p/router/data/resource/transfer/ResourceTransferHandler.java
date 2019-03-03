package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferErrorEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferStartedEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;

/**
 * Handles the receiving of a resource.
 */
public abstract class ResourceTransferHandler<T extends Resource> implements AutoCloseable {

    /**
     * The router used.
     */
    private Router router;

    /**
     * The transferred resource.
     */
    private T resource;

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


    public ResourceTransferHandler(T resource, ResourceTransferType transferType, MessageTunnel tunnel, int transferId, Router router) {
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
     * Starts sending the resource.
     */
    public void startSending() {
        if(getTransferType() != ResourceTransferType.OUTBOUND) return;
        ResourceManager.logger.info("Sending {}...", this.getResource());
        setLastMessageTime(System.currentTimeMillis());
        doStartSending();
    }

    /**
     * Implementation of starting to send a resource.
     */
    abstract void doStartSending();

    /**
     * Stops the sending of a resource.
     */
    public void stopSending() {
        if(getTransferType() != ResourceTransferType.OUTBOUND) return;
        ResourceManager.logger.info("Stopping sending {}...", this.getResource());
        doStopSending();
    }

    /**
     * Implementation of stopping the sending of a resource.
     */
    abstract void doStopSending();

    /**
     * Releases all the resources associated with the handler.
     */
    @Override
    public void close() {
        //Handling close.
        onClose();

        //Closing tunnel.
        if(transferType == ResourceTransferType.INBOUND)
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
    public void receiveData(ResourceDataMessage dataMessage) {
        if(getTransferType() != ResourceTransferType.INBOUND) return;
        setLastMessageTime(System.currentTimeMillis());
        doDataReceived(dataMessage);
    }

    /**
     * Called when a resource data message is received.
     *
     * @param dataMessage
     */
    abstract void doDataReceived(ResourceDataMessage dataMessage);

    /**
     * Called when a transfer control message is received for this transfer.
     * @param message
     */
    public abstract void onTransferControlMessage(int message);

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

    public T getResource() {
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
