package me.matoosh.undernet.event.server;

import me.matoosh.undernet.p2p.router.server.ServerConnection;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Called when a server gets a connection from a client.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public class ServerConnectionEvent extends ServerEvent {

    public ServerConnectionEvent(Server server, ServerConnection serverConnection) {
        super(server);
    }

    @Override
    public void onCalled() {

    }
}
