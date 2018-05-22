package me.matoosh.undernet.event.channel.message.tunnel;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageManager;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;

public class MessageTunnelEstablishedEvent extends Event {
    /**
     * The message tunnel
     */
    public MessageTunnel messageTunnel;

    public MessageTunnelEstablishedEvent(MessageTunnel messageTunnel) {
        this.messageTunnel = messageTunnel;
    }

    @Override
    public void onCalled() {
        NetworkMessageManager.logger.info("Tunnel with {} was established with key {}", messageTunnel.getRecipient().getStringValue(), messageTunnel.getRecipientPublicKey());
    }
}
