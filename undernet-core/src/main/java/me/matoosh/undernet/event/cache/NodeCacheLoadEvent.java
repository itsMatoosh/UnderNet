package me.matoosh.undernet.event.cache;

import me.matoosh.undernet.p2p.cache.EntryNodeCache;

/**
 * Called when the node cache is loaded from the disk.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class NodeCacheLoadEvent extends NodeCacheEvent {
    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        EntryNodeCache.logger.info("Node cache has been loaded from disk");
    }
}
