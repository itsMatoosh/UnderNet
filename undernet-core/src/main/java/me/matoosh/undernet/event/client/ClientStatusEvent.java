package me.matoosh.undernet.event.client;

import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Called when the status of a client changes.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public class ClientStatusEvent extends ClientEvent {
    /**
     * The new status of the client.
     */
    public InterfaceStatus newStatus;

    public ClientStatusEvent(Client c, InterfaceStatus status) {
        super(c);
        this.newStatus = status;
    }

    @Override
    public void onCalled() {
        client.status = newStatus;
        Client.logger.info("Client status changed to: " + newStatus);
    }
}
