package me.matoosh.undernet.p2p.router;

/**
 * Excpetion with the router.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class RouterException extends Exception {
    /**
     * The router.
     */
    public Router router;
    public RouterException(Router r) {
        this.router = r;
    }
}
