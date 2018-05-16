package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;

/**
 * Represents a flag resource.
 * Flag resources contain temporary information.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public abstract class FlagResource extends Resource {
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
}
