package me.matoosh.undernet.p2p.node;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.ConnectionEstablishedEvent;
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
