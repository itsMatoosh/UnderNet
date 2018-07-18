package me.matoosh.undernet.event.channel.message.tunnel;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;

/**
 * Events concerning a message tunnel.
 */
public abstract class MessageTunnelEvent extends Event {
    /**
     * The message tunnel.
     */
    public MessageTunnel messageTunnel;

    public MessageTunnelEvent(MessageTunnel messageTunnel) {
        this.messageTunnel = messageTunnel;
    }
}
