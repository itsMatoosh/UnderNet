package me.matoosh.undernet.event.channel.message;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageManager;

/**
 * Called when a NetworkMessage is received on this node and this is its final stop.
 * Messages retrieved in this event are decrypted and ready to use.
 */
public class MessageReceivedEvent extends Event {

    /**
     * The network message.
     */
    public NetworkMessage networkMessage;

    /**
     * The node which forwarded the message to the client.
     */
    public Node forwarder;

    /**
     * Creates a new message received event.
     * @param message
     * @param forwarder
     */
    public MessageReceivedEvent(NetworkMessage message, Node forwarder) {
        this.networkMessage = message;
        this.forwarder = forwarder;
    }

    @Override
    public void onCalled() {
        NetworkMessageManager.logger.debug("A network message of type: {}, received.", networkMessage.getContent().getType());
    }
}
