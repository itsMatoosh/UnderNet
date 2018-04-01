package me.matoosh.undernet.event.server;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Represents a server event.
 * Created by Mateusz Rębacz on 22.02.2017.
 */

public abstract class ServerEvent extends Event {
    /**
     * The server the event is about.
     */
    public Server server;

    public ServerEvent(Server server) {
        this.server = server;
    }
}
