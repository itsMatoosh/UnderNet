package me.matoosh.undernet.p2p.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
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
     * The router of this manager.
     */
    public Router router;

    /**
     * Sets up the manager.
     */
    public void setup(Router router) {
        this.router = router;
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
                NodeInfoMessage message = (NodeInfoMessage)NetworkMessage.deserializeMessage(messageReceivedEvent.message.data.array());
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

    /**
     * Returns the neighboring node closest to the given id.
     * @param id
     * @return
     */
    public Node getClosestTo(NetworkID id) {
        Node closest = null;
        BigInteger closestDist = null;
        for (int i = 0; i < router.connectedNodes.size(); i++) {
            Node n = router.connectedNodes.get(i);
            BigInteger distance = n.getIdentity().getNetworkId().distanceTo(id);

            if(closestDist == null || closest == null || distance.compareTo(closestDist) < 0) {
                closest = n;
                closestDist = distance;
            }
        }

        return closest;
    }
}
