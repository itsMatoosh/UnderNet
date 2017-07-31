package me.matoosh.undernet.event.router;

import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.InterfaceStatus;

import static me.matoosh.undernet.UnderNet.logger;

/**
 * Called when the status of a router changes.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class RouterStatusEvent extends RouterEvent {
    /**
     * The new status of the router.
     */
    public InterfaceStatus newStatus;

    public RouterStatusEvent(Router r, InterfaceStatus newStatus) {
        super(r);
        this.newStatus = newStatus;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        //Sets the server status to the new status.
        logger.info("Router status changed to: " + newStatus);
        router.status = newStatus;
    }
}
