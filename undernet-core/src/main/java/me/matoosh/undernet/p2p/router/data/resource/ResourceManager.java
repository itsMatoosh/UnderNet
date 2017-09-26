package me.matoosh.undernet.p2p.router.data.resource;

import java.util.ArrayList;

/**
 * Manages network resources locally.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class ResourceManager {
    /**
     * List of the stored resources.
     */
    public ArrayList<Resource> resourcesStored;

    /**
     * Publishes a resource on the network.
     * This sends the resource to its closest node.
     * @param resource
     */
    public void publish(Resource resource) {
    }

    /**
     * Sets up the resource manager.
     */
    public void setup() {

    }

    /**
     * Registers the message handlers.
     */
    private void registerHandlers() {

    }
}
