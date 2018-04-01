package me.matoosh.undernet.event.router;

import me.matoosh.undernet.p2p.router.Router;

/**
 * Called when an error occurs with the router.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class RouterErrorEvent extends RouterEvent {
    /**
     * The error.
     */
    public Throwable exception;
    /**
     * Whether the router handler should attempt to reconnect.
     */
    public boolean shouldReconnect;

    public RouterErrorEvent(Router r, Throwable e, boolean shouldReconnect) {
        super(r);
        this.exception = e;
        this.shouldReconnect = shouldReconnect;
    }
    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
