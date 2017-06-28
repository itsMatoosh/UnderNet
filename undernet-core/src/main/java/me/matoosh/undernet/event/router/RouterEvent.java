package me.matoosh.undernet.event.router;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.Router;

/**
 * Event concerning the router.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public abstract class RouterEvent extends Event {
    /**
     * The router.
     */
    public Router router;

    public RouterEvent(Router r) {
        this.router = r;
    }
}
