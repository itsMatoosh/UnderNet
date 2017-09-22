package me.matoosh.undernet.event.server;

import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Called when an exception occurs on the server.
 * Created by Mateusz Rębacz on 29.06.2017.
 */

public class ServerExceptionEvent extends ServerEvent {
    /**
     * The exception.
     */
    public Exception exception;

    public ServerExceptionEvent(Server server, Exception e) {
        super(server);
        this.exception = e;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
