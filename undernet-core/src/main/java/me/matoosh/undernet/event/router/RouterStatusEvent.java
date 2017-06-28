package me.matoosh.undernet.event.router;

import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.RouterStatus;

/**
 * Called when the status of a router changes.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class RouterStatusEvent extends RouterEvent {
    /**
     * The new status of the router.
     */
    public RouterStatus newStatus;

    public RouterStatusEvent(Router r, RouterStatus newStatus) {
        super(r);
        this.newStatus = newStatus;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        router.status = newStatus;
    }
}
