package me.matoosh.undernet.event.cache;

import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;

/**
 * Called when a node is removed from the node cache.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class NodeCacheRemovedEvent extends NodeCacheEvent {
    /**
     * The node.
     */
    public Node node;

    public NodeCacheRemovedEvent(Node node) {
        this.node = node;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        NodeCache.logger.info("Removed node " + node.address + " from cache");
    }
}
