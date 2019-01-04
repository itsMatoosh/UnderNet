package me.matoosh.undernet.p2p.node;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.ConnectionEstablishedEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.NodeInfoMessage;
import me.matoosh.undernet.p2p.router.data.message.NodeNeighborsMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

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
     * The percentage of neighbors that will be shared with requesting nodes.
     */
    public static final float NEIGHBOR_SHARE_PERCENT = 0.3333f;
    /**
     * Max amount of neighbor addresses shared.
     */
    public static final int MAX_NEIGHBORS_MSG_LENGTH = 5;

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
        EventManager.registerEvent(ConnectionEstablishedEvent.class);
    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        //Message received event.
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
        EventManager.registerHandler(this, ChannelCreatedEvent.class);
        EventManager.registerHandler(this, MessageReceivedEvent.class);
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
            sendSelfNodeInfo(channelCreatedEvent.remoteNode);
        } else if(e instanceof ChannelMessageReceivedEvent) {
            ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;

            if(messageReceivedEvent.remoteNode.getIdentity() == null) {
                if (messageReceivedEvent.message.verify()) {
                    messageReceivedEvent.message.deserialize();
                    if(messageReceivedEvent.message.getContent().getType() == MsgType.NODE_INFO) {
                        NodeInfoMessage nodeInfoMessage = (NodeInfoMessage)messageReceivedEvent.message.getContent();

                        logger.debug("Received node info for {}", nodeInfoMessage.getNetworkMessage().getOrigin());
                        NetworkIdentity networkIdentity = new NetworkIdentity(nodeInfoMessage.getNetworkMessage().getOrigin());
                        messageReceivedEvent.remoteNode.setIdentity(networkIdentity);

                        EventManager.callEvent(new ConnectionEstablishedEvent(messageReceivedEvent.remoteNode));
                    }
                }
            }
        } else if (e instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) e;
            NetworkMessage netMsg = messageReceivedEvent.networkMessage;
            if (netMsg.getContent().getType() == MsgType.NODE_NEIGHBORS_REQUEST && netMsg.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
                //responding with neighbors.
                int shareableNeighbors = (int) (this.router.connectedNodes.size() * NEIGHBOR_SHARE_PERCENT);
                if (shareableNeighbors == 0) shareableNeighbors = 1;
                if (shareableNeighbors >= MAX_NEIGHBORS_MSG_LENGTH) shareableNeighbors = MAX_NEIGHBORS_MSG_LENGTH;

                logger.info("Sending {} neighbor infos...", shareableNeighbors);

                InetSocketAddress addresses[] = new InetSocketAddress[shareableNeighbors];
                for (int i = 0; i < shareableNeighbors; i++) {
                    addresses[i] = this.router.connectedNodes.get(i).address;
                }

                netMsg.getTunnel().sendMessage(new NodeNeighborsMessage(addresses));
            } else if (netMsg.getContent().getType() == MsgType.NODE_NEIGHBORS && netMsg.getDirection() == NetworkMessage.MessageDirection.TO_ORIGIN) {
                //node infos received
                NodeNeighborsMessage neighborsMessage = (NodeNeighborsMessage) netMsg.getContent();

                if (neighborsMessage.getAddresses() == null || neighborsMessage.getAddresses().length == 0) {
                    logger.info("No new node infos received...");
                    return;
                }

                logger.info("{} new node infos received! Connecting...", neighborsMessage.getAddresses().length);
                for (String host :
                        neighborsMessage.getAddresses()) {
                    //Adds node to the cache.
                    Node saved = EntryNodeCache.addNode(host);

                    //Connecting if started.
                    if (UnderNet.router.status.equals(InterfaceStatus.STARTED)) {
                        UnderNet.router.connectNode(saved);
                    }
                }
            }
        }
    }

    /**
     * Sends a node info message about infoFrom, to infoTo.
     * @param infoTo
     */
    public void sendSelfNodeInfo(Node infoTo) {
        logger.info("Sending [self] node info to: {}", infoTo.toString());
        NetworkMessage message = new NetworkMessage(Node.self.getIdentity().getNetworkId(), Node.self.getIdentity().getNetworkId(), new NodeInfoMessage(), NetworkMessage.MessageDirection.TO_DESTINATION);
        message.serialize();
        message.sign();
        infoTo.sendRaw(message);
    }

    /**
     * Returns the neighboring node closest to the given id.
     * @param id
     * @return
     */
    public Node getClosestTo(NetworkID id) {
        Node closest = Node.self;
        byte[] closestDist = Node.self.getIdentity().getNetworkId().distanceTo(id);
        for (int i = 0; i < router.connectedNodes.size(); i++) {
            Node n = router.connectedNodes.get(i);
            if(n == null) continue;
            if(n.getIdentity() == null) {
                continue;
            }
            byte[] distance = n.getIdentity().getNetworkId().distanceTo(id);

            if(NetworkID.compare(closestDist, distance) < 0) {
                closest = n;
                closestDist = distance;
            }
        }

        return closest;
    }
}
