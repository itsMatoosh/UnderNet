package me.matoosh.undernet.event.channel.message;

import io.netty.channel.Channel;
import me.matoosh.undernet.event.channel.ChannelEvent;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessage;

/**
 * Represents events regarding network messages.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public abstract class ChannelMessageEvent extends ChannelEvent {
    /**
     * The network message.
     */
    public NetworkMessage message;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     */
    public ChannelMessageEvent(Channel c, boolean isServer) {
        super(c, isServer);
    }
}
