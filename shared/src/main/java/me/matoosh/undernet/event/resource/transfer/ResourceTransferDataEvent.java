package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;

/**
 * Resource transfer event concerning resource data.
 */
public abstract class ResourceTransferDataEvent extends ResourceTransferEvent {

    /**
     * The received data.
     */
    public ResourceDataMessage resourceDataMessage;

    public ResourceTransferDataEvent(ResourceTransferHandler transferHandler, ResourceDataMessage dataMessage) {
        super(transferHandler);
        this.resourceDataMessage = dataMessage;
    }
}
