package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

/**
 * Called when a resource transfer finishes.
 */
public class ResourceTransferFinishedEvent extends ResourceTransferEvent {

    private String reason;

    public ResourceTransferFinishedEvent(ResourceTransferHandler transferHandler, String reason) {
        super(transferHandler);
        this.reason = reason;
    }

    @Override
    public void onCalled() {
        if(this.transferHandler.getTransferType() == ResourceTransferType.INBOUND) {
            logger.info("Transfer of resource {}, from {}, finished!, {}", this.transferHandler.getResource(), this.transferHandler.getTunnel().getDestination(), this.reason);
        } else {
            logger.info("Transfer of resource {}, to {}, finished!, {}", this.transferHandler.getResource(), this.transferHandler.getTunnel().getDestination(), this.reason);
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
