package me.matoosh.undernet.p2p.router.data.resource;

/**
 * Represents a flag resource.
 * Flag resources have a set expiration and can contain routing information.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FlagResource extends Resource {
    /**
     * Calculates the network id of the resource based on its contents.
     */
    @Override
    public void calcNetworkId() {

    }

    /**
     * Returns the type of the resource. E.g file resource.
     *
     * @return
     */
    @Override
    public byte getResourceType() {
        return 1;
    }
}
