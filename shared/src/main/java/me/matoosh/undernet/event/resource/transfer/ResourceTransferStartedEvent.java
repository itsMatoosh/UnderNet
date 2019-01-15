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
        if(this.getTransferHandler().getTransferType() == ResourceTransferType.INBOUND) {
            logger.info("Transfer of resource {}, from {}, started!", this.getTransferHandler().getResource(), this.getTransferHandler().getTunnel().getDestination());
        } else {
            logger.info("Transfer of resource {}, to {}, started!", this.getTransferHandler().getResource(), this.getTransferHandler().getTunnel().getDestination());
        }
    }
}
