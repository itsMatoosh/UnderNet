package me.matoosh.undernet.event.server;

import me.matoosh.undernet.p2p.router.server.Server;
import me.matoosh.undernet.p2p.router.server.ServerException;

/**
 * Called when an error occurs on the server.
 * Created by Mateusz Rębacz on 29.06.2017.
 */

public class ServerErrorEvent extends ServerEvent {
    /**
     * The exception.
     */
    public ServerException exception;

    public ServerErrorEvent(Server server, ServerException e) {
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
