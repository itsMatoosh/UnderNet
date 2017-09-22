package me.matoosh.undernet.event.channel.message;

import io.netty.channel.Channel;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessage;

/**
 * Called when a message is received on connection.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public class ChannelMessageReceivedEvent extends ChannelMessageEvent {
    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     */
    public ChannelMessageReceivedEvent(Channel c, boolean isServer) {
        super(c, isServer);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        NetworkMessage.logger.info("A network message received on channel: " + channel.toString());
    }
}
