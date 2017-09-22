package me.matoosh.undernet.event.client;

import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Called when an error occurs on the client.
 * Connections are closed automatically due to an error.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class ClientExceptionEvent extends ClientEvent {
    /**
     * The exception.
     */
    public Throwable exception;

    public ClientExceptionEvent(Client c, Throwable e) {
        super(c);
        this.exception = e;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
