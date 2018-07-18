package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

/**
 * Called when a resource transfer finishes.
 */
public class ResourceTransferFinishedEvent extends ResourceTransferEvent {
    public ResourceTransferFinishedEvent(ResourceTransferHandler transferHandler) {
        super(transferHandler);
    }

    @Override
    public void onCalled() {
        if(this.transferHandler.transferType == ResourceTransferType.INBOUND) {
            logger.info("Transfer of resource {}, from {}, finished!", this.transferHandler.resource, this.transferHandler.other);
        } else {
            logger.info("Transfer of resource {}, to {}, finished!", this.transferHandler.resource, this.transferHandler.other);
        }
    }
}
