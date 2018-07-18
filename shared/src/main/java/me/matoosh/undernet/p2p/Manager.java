package me.matoosh.undernet.p2p;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.p2p.router.Router;

/**
 * Class for all the P2P managers to inherit.
 * Makes  managers easier to write.
 * Created by Mateusz Rębacz on 30.09.2017.
 */

public abstract class Manager extends EventHandler {
    /**
     * The router of the manager.
     */
    public Router router;

    /**
     * Router specification is mandatory.
     * @param router
     */
    public Manager(Router router) {
        this.router = router;
    }

    /**
     * Sets up the manager.
     */
    public void setup() {
        UnderNet.logger.info("Setting up {}...", this.getClass().getSimpleName());
        registerEvents();
        registerHandlers();
    }

    /**
     * Registers the events of the manager.
     */
    protected abstract void registerEvents();
    /**
     * Registers the handlers of the manager.
     */
    protected abstract void registerHandlers();
}
