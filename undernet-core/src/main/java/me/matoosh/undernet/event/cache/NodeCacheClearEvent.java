package me.matoosh.undernet.event.cache;

import me.matoosh.undernet.p2p.cache.EntryNodeCache;

/**
 * Called when the node cache is cleared.
 * Created by Mateusz Rębacz on 11.09.2017.
 */

public class NodeCacheClearEvent extends NodeCacheEvent {
    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        EntryNodeCache.logger.info("Node cache has been cleared");
    }
}
