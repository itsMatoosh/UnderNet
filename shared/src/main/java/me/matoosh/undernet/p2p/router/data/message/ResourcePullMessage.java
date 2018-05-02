package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Message used to pull resources.
 */
public class ResourcePullMessage extends MsgBase {
    /**
     * The id of the pulled resource.
     */
    public NetworkID resourceId;

    public ResourcePullMessage(NetworkID resourceId) {
        this.resourceId = resourceId;
    }
}
