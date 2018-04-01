package me.matoosh.undernet.p2p.router.client;

/**
 * Exception regarding a client.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class ClientException extends Exception {
    /**
     * The client.
     */
    public Client client;

    public ClientException(Client c) {
        this.client = c;
    }
}
