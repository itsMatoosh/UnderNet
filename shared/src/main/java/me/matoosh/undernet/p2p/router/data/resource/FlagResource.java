package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.Router;

/**
 * Represents a flag resource.
 * Flag resources contain temporary information.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public abstract class FlagResource extends Resource {
    /**
     * Creates a new resource instance.
     *
     * @param router
     */
    public FlagResource(Router router) {
        super(router);
    }

    @Override
    public boolean isLocal() {
        for (FlagResource flag :
                UnderNet.router.resourceManager.flagResources) {
            if (flag == this) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears the flag resource.
     */
    @Override
    public void clear() {
        if(getRouter().resourceManager.flagResources.contains(this)) {
            getRouter().resourceManager.flagResources.remove(this);
        }
    }
}
