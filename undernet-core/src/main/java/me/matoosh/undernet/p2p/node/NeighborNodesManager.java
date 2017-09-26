package me.matoosh.undernet.p2p.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.NodeInfoMessage;

/**
 * Manages neighboring nodes connected to the router.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NeighborNodesManager extends EventHandler {
    /**
     * Logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NeighborNodesManager.class);

    /**
     * Sets up the manager.
     */
    public void setup() {
        registerHandlers();
    }

    /**
     * Registers the event handlers.
     */
    private void registerHandlers() {
        //Message received event.
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
        EventManager.registerHandler(this, ChannelCreatedEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        if(e instanceof ChannelCreatedEvent) {
            //Sending node info to the connected node.
            ChannelCreatedEvent channelCreatedEvent = (ChannelCreatedEvent)e;
            sendNodeInfo(Node.self, channelCreatedEvent.remoteNode);

        } else if(e instanceof  ChannelMessageReceivedEvent) {
            ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
            if(messageReceivedEvent.message.msgId == MsgType.NODE_INFO.ordinal()) {
                NodeInfoMessage message = new NodeInfoMessage();
                message.fromByte(messageReceivedEvent.message.data.array());
                //TODO: Check the generated id with the database and update.
                logger.info("Received node info for " + messageReceivedEvent.remoteNode + ": " + message.networkID);
                NetworkIdentity networkIdentity = new NetworkIdentity();
                networkIdentity.setNetworkId(message.networkID);
                messageReceivedEvent.remoteNode.setIdentity(networkIdentity);
            }
        }
    }

    /**
     * Sends a message info about infoFrom to infoTo.
     * @param infoFrom
     * @param infoTo
     */
    public void sendNodeInfo(Node infoFrom, Node infoTo) {
        logger.info("Sending " + infoFrom.toString() + " node info to: " + infoTo.toString());
        infoTo.send(new NetworkMessage(MsgType.NODE_INFO, new NodeInfoMessage(infoFrom)));
    }
}
