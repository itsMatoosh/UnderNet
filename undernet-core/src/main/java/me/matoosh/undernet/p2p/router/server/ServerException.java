package me.matoosh.undernet.p2p.router.server;

/**
 * Occurs when an error happens on the server.
 * Created by Mateusz Rębacz on 29.06.2017.
 */

public class ServerException extends Exception {
    /**
     * The server.
     */
    public Server server;

    public ServerException(Server server) {
        this.server = server;
    }
}
