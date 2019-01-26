package me.matoosh.undernet.event.channel.message.tunnel;

import me.matoosh.undernet.p2p.router.data.message.NetworkMessageManager;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;

public class MessageTunnelEstablishedEvent extends MessageTunnelEvent {
    public MessageTunnelEstablishedEvent(MessageTunnel messageTunnel) {
        super(messageTunnel);
    }

    @Override
    public void onCalled() {
        NetworkMessageManager.logger.info("Tunnel {} established!", messageTunnel);
    }
}
