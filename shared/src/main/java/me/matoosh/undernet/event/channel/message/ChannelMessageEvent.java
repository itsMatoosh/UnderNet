package me.matoosh.undernet.event.channel.message;

import io.netty.channel.Channel;
import me.matoosh.undernet.event.channel.ChannelEvent;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;

/**
 * Represents events regarding network messages.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public abstract class ChannelMessageEvent extends ChannelEvent {
    /**
     * The network content.
     */
    public NetworkMessage message;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     */
    public ChannelMessageEvent(Channel c, boolean isServer, NetworkMessage msg) {
        super(c, isServer);
        this.message = msg;
        this.message.content.sender = this.remoteNode;
    }
}
