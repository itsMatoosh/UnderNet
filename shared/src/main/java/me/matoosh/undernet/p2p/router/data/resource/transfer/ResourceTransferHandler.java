package me.matoosh.undernet.p2p.router.data.resource.transfer;

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


    public ResourceTransferHandler(Resource resource, ResourceTransferType transferType, MessageTunnel tunnel, int transferId, Router router) {
        this.resource = resource;
        this.transferType = transferType;
        this.tunnel = tunnel;
        this.router = router;
        this.transferId = transferId;
    }

    /**
     * Prepares the transfer.
     */
    public abstract void prepare();

    /**
     * Sends chunk with id chunk id.
     * @param chunkId
     */
    public abstract void sendChunk(int chunkId);

    /**
     * Releases all the resources associated with the handler.
     */
    public abstract void close();

    /**
     * Called when a resource data message is sent.
     * @param dataMessage
     */
    public abstract void onDataReceived(ResourceDataMessage dataMessage);

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
}
