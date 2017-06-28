package me.matoosh.undernet.p2p.router.client;

/**
 * Occurs when the client tries to start without enough nodes cached.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class ClientNoNodesCachedException extends ClientException {
    public ClientNoNodesCachedException(Client c) {
        super(c);
    }
}
