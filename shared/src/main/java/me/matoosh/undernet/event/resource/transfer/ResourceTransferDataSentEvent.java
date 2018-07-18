package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;

/**
 * Called when a resource transfer sends data.
 */
public class ResourceTransferDataSentEvent extends ResourceTransferDataEvent {
    public ResourceTransferDataSentEvent(ResourceTransferHandler transferHandler, ResourceDataMessage dataMessage) {
        super(transferHandler, dataMessage);
    }

    @Override
    public void onCalled() {

    }
}
