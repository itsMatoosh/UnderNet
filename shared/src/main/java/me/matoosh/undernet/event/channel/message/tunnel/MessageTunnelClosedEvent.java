package me.matoosh.undernet.event.channel.message.tunnel;

import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;

/**
 * Called when a message tunnel closes.
 */
public class MessageTunnelClosedEvent extends MessageTunnelEvent{
    public MessageTunnelClosedEvent(MessageTunnel messageTunnel) {
        super(messageTunnel);
    }

    @Override
    public void onCalled() {
        logger.info("Tunnel {} closed", messageTunnel);
    }
}
