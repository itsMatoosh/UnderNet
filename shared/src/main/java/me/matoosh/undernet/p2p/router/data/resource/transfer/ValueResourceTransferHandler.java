package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

public class ValueResourceTransferHandler extends ResourceTransferHandler {

    public ValueResourceTransferHandler(Resource resource, ResourceTransferType transferType, MessageTunnel tunnel, int transferId, Router router) {
        super(resource, transferType, tunnel, transferId, router);
    }

    @Override
    public void prepare() {

    }

    @Override
    void doStartSending() {

    }

    @Override
    void doStopSending() {

    }

    @Override
    public void onClose() {

    }

    @Override
    public void doDataReceived(ResourceDataMessage dataMessage) {

    }

    @Override
    public void onTransferControlMessage(int message) {

    }

    @Override
    public void onError(Exception e) {

    }
}
