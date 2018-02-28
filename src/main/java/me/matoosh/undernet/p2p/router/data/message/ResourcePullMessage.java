package me.matoosh.undernet.p2p.router.data.message;

import java.util.ArrayList;
import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Message containing resource id and its pull info.
 * Created by Mateusz Rębacz on 12.11.2017.
 */

public class ResourcePullMessage implements MsgBase {
    /**
     * The id of the pulled resource.
     */
    public NetworkID resourceId;

    /**
     * Creates a new resource pull message based on the id of the pulled resource.
     * @param resourceId
     */
    public ResourcePullMessage(NetworkID resourceId) {
        this.resourceId = resourceId;
    }
}
