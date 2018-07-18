package me.matoosh.undernet.event.resource.transfer;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;

/**
 * Event regarding resource transfer.
 * Created by Mateusz Rębacz on 14.10.2017.
 */

public abstract class ResourceTransferEvent extends Event {
    /**
     * The file transfer.
     */
    public ResourceTransferHandler transferHandler;

    public ResourceTransferEvent(ResourceTransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }
}
