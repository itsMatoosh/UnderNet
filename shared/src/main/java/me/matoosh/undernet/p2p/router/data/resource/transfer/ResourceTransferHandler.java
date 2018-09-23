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
    public Router router;

    /**
     * The transferred resource.
     */
    public Resource resource;

    /**
     * The type of the resource transfer.
     */
    public ResourceTransferType transferType;

    /**
     * The tunnel of the resource.
     */
    public MessageTunnel tunnel;

    /**
     * The transfer id of the transfer.
     */
    public byte transferId;


    public ResourceTransferHandler(Resource resource, ResourceTransferType transferType, MessageTunnel tunnel, byte transferId, Router router) {
        this.resource = resource;
        this.transferType = transferType;
        this.tunnel = tunnel;
        this.router = router;
        this.transferId = transferId;
    }

    /**
     * Starts the sending process.
     */
    public abstract void startSending();

    /**
     * Releases all the resources associated with the handler.
     */
    public abstract void close();

    /**
     * Called when a resource data message is sent.
     * @param dataMessage
     */
    public abstract void onResourceMessage(ResourceDataMessage dataMessage);
}
