package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;

/**
 * Called when a resource transfer receives data.
 */
public class ResourceTransferDataReceivedEvent extends ResourceTransferDataEvent {
    public ResourceTransferDataReceivedEvent(ResourceTransferHandler transferHandler, ResourceDataMessage dataMessage) {
        super(transferHandler, dataMessage);
    }

    @Override
    public void onCalled() {

    }
}
