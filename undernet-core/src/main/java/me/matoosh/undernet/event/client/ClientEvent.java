package me.matoosh.undernet.event.client;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Event concerning a client.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public abstract class ClientEvent extends Event {
    /**
     * The client the event is about.
     */
    public Client client;

    public ClientEvent(Client c) {
        this.client = c;
    }
}
