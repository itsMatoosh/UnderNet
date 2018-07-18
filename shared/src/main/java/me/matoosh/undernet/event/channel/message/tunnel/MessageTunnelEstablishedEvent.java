package me.matoosh.undernet.event.channel.message.tunnel;

import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageManager;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;

public class MessageTunnelEstablishedEvent extends MessageTunnelEvent {
    /**
     * The direction in which the tunnel was established.
     */
    public NetworkMessage.MessageDirection establishDirection;

    public MessageTunnelEstablishedEvent(MessageTunnel messageTunnel, NetworkMessage.MessageDirection establishDirection) {
        super(messageTunnel);
        this.establishDirection = establishDirection;
    }

    @Override
    public void onCalled() {
        NetworkMessageManager.logger.info("Tunnel {} established!", messageTunnel);
    }
}
