package me.matoosh.undernet.event.server;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Created by Mateusz Rębacz on 22.02.2017.
 */

public abstract class ServerEvent extends Event {
    /**
     * Server generating the event.
     */
    public Server server;

    public ServerEvent(Server server) {
        this.server = server;
    }
}
