package me.matoosh.undernet.p2p.node;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.NodeInfoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages neighboring nodes connected to the router.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NeighborNodesManager extends Manager {
    /**
     * Logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NeighborNodesManager.class);

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public NeighborNodesManager(Router router) {
        super(router);
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {

    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
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
            if(messageReceivedEvent.message.msgType == MsgType.NODE_INFO) {
                NodeInfoMessage message = (NodeInfoMessage)messageReceivedEvent.message.content;
                //TODO: Check the generated id with the database and update.
                logger.info("Received node info for {}: {}", messageReceivedEvent.remoteNode, message.networkID);
                NetworkIdentity networkIdentity = new NetworkIdentity();
                networkIdentity.setNetworkId(message.networkID);
                messageReceivedEvent.remoteNode.setIdentity(networkIdentity);
            }
        }
    }

    /**
     * Sends a node info message about infoFrom, to infoTo.
     * @param infoFrom
     * @param infoTo
     */
    public void sendNodeInfo(Node infoFrom, Node infoTo) {
        logger.info("Sending {} node info to: {}", infoFrom, infoTo.toString());
        infoTo.send(new NetworkMessage(MsgType.NODE_INFO, new NodeInfoMessage(infoFrom)));
    }

    /**
     * Returns the neighboring node closest to the given id.
     * @param id
     * @return
     */
    public Node getClosestTo(NetworkID id) {
        Node closest = Node.self;
        byte[] closestDist = Node.self.getIdentity().getNetworkId().distanceTo(id);
        for (Node n : router.connectedNodes) {
            byte[] distance = n.getIdentity().getNetworkId().distanceTo(id);

            if(NetworkID.compare(closestDist, distance) < 0) {
                closest = n;
                closestDist = distance;
            }
        }

        return closest;
    }
}
