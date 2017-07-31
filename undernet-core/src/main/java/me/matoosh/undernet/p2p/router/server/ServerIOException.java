package me.matoosh.undernet.p2p.router.server;

/**
 * Occurs when a server IO problem happens.
 * Created by Mateusz Rębacz on 29.06.2017.
 */

public class ServerIOException extends ServerException {
    public ServerIOException(Server server) {
        super(server);
    }
}
