package me.matoosh.undernet.p2p.router.server;

/**
 * Base class for the listeners.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public abstract class NodeListener {
    /**
     * The thread that the listener is running on.
     */
    public Thread thread;

    /**
     * Starts listening.
     */
    public abstract void start();

    /**
     * Stops listening.
     */
    public abstract void stop();
}
