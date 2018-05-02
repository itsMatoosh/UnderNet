package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;

/**
 * Represents a flag resource.
 * Flag resources have a set expiration and can contain routing information.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FlagResource extends Resource {

    /**
     * The data of the flag.
     */
    public byte data;

    /**
     * Calculates the network id of the resource based on its contents.
     */
    @Override
    public void calcNetworkId() {
        return; //TODO
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

    @Override
    public void send(Node recipient, IResourceActionListener resourceActionListener) {

    }

    @Override
    public void receive(Node sender, IResourceActionListener resourceActionListener) {

    }
}
