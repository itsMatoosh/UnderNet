package me.matoosh.undernet.event.server;

import me.matoosh.undernet.p2p.router.server.Server;
import me.matoosh.undernet.p2p.router.server.ServerStatus;

/**
 * Called when the server status changes.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public class ServerStatusEvent extends ServerEvent {

    /**
     * New status of the server.
     */
    public ServerStatus newStatus;

    public ServerStatusEvent(Server server, ServerStatus newStatus) {
        super(server);
        this.newStatus = newStatus;
    }

    @Override
    public void onCalled() {
        //Sets the server status to the new status.
        server.status = newStatus;
        Server.logger.info("Server status changed to: " + newStatus);
    }
}
