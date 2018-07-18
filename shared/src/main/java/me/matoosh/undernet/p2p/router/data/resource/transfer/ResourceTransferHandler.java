package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
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
     * The other of the resource.
     */
    public NetworkID other;

    /**
     * The transfer id of the transfer.
     */
    public byte transferId;

    /**
     * The direction for messages.
     */
    public NetworkMessage.MessageDirection messageDirection;


    public ResourceTransferHandler(Resource resource, ResourceTransferType transferType, NetworkMessage.MessageDirection messageDirection, NetworkID other, byte transferId, Router router) {
        this.resource = resource;
        this.transferType = transferType;
        this.messageDirection = messageDirection;
        this.other = other;
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
