package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

/**
 * Represents a value resource.
 * Stores a string value, encrypted with the private key of the owner.
 * The value can be decrypted with the network id of the resource
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ValueResource extends Resource {

    private String encryptedValue;

    /**
     * Creates a new resource instance.
     *
     * @param router
     */
    public ValueResource(Router router, String encryptedValue) {
        super(router);
        this.encryptedValue = encryptedValue;
    }

    @Override
    public void calcNetworkId() {

    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    ResourceType getResourceType() {
        return null;
    }

    @Override
    void updateAttributes() {

    }

    @Override
    public ResourceTransferHandler getTransferHandler(ResourceTransferType resourceTransferType, MessageTunnel tunnel, int transferId, Router router) {
        return null;
    }

    @Override
    public void clear() {

    }
}
