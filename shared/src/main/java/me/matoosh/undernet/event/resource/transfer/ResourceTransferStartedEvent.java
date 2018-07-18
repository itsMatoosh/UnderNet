package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

/**
 * Called when a resource transfer starts.
 */
public class ResourceTransferStartedEvent extends ResourceTransferEvent {
    public ResourceTransferStartedEvent(ResourceTransferHandler transferHandler) {
        super(transferHandler);
    }

    @Override
    public void onCalled() {
        if(this.transferHandler.transferType == ResourceTransferType.INBOUND) {
            logger.info("Transfer of resource {}, from {}, started!", this.transferHandler.resource, this.transferHandler.other);
        } else {
            logger.info("Transfer of resource {}, to {}, started!", this.transferHandler.resource, this.transferHandler.other);
        }
    }
}
