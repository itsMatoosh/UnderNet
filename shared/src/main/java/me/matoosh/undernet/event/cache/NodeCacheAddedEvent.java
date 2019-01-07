package me.matoosh.undernet.event.cache;

import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;

/**
 * Called when a node is added to the node cache.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class NodeCacheAddedEvent extends NodeCacheEvent {
    /**
     * The node.
     */
    public Node node;

    public NodeCacheAddedEvent(Node node) {
        this.node = node;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        EntryNodeCache.logger.info("Added node " + node.getAddress() + " to cache");
    }
}
