package me.matoosh.undernet.event.client;

import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.client.ClientException;

/**
 * Called when an error occurs on the client.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class ClientErrorEvent extends ClientEvent {
    /**
     * The exception.
     */
    public ClientException exception;

    public ClientErrorEvent(Client c, ClientException e) {
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
