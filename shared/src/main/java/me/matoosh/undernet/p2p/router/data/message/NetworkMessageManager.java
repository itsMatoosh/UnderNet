package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.tunnel.MessageTunnelEstablishedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnelState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the sending of network messages.
 */
public class NetworkMessageManager extends Manager {
    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkMessageManager.class);

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public NetworkMessageManager(Router router) {
        super(router);
    }

    @Override
    public void setup() {
        super.setup();
    }

    /**
     * Constructs a message and starts the sending process to the other node.
     * If a tunnel is not yet established with the other node, it will be established now.
     * If a tunnel is already active with the other node, the message will be sent through it.
     * @param content
     * @param recipient
     */
    public void sendMessage(me.matoosh.undernet.p2p.router.data.message.MsgBase content, NetworkID recipient) {
        //Getting the appropriate message tunnel.
        MessageTunnel messageTunnel = router.messageTunnelManager.getOrCreateTunnel(Node.self.getIdentity().getNetworkId(), recipient);

        //Constructing the message.
        NetworkMessage message = constructMessage(messageTunnel, content, NetworkMessage.MessageDirection.TO_DESTINATION);

        //Checking if the tunnel has been created.
        if(messageTunnel.getTunnelState() == MessageTunnelState.NOT_ESTABLISHED && messageTunnel.messageQueue.size() == 0) {
            //Adding message to the queue.
            messageTunnel.messageQueue.add(message);

            //Establishing the tunnel.
            router.messageTunnelManager.establishTunnel(messageTunnel);
        }
        else if (messageTunnel.getTunnelState() == MessageTunnelState.ESTABLISHING || messageTunnel.getTunnelState() == MessageTunnelState.NOT_ESTABLISHED) {
            //Adding message to the queue.
            messageTunnel.messageQueue.add(message);
        }
        else if(messageTunnel.getTunnelState() == MessageTunnelState.ESTABLISHED) {
            //Using an existing tunnel.
            messageTunnel.encryptMsgSymmetric(message);
            forwardMessage(message, Node.self);
        }
    }

    /**
     * Constructs a response message and starts the sending process to the origin node.
     * The response can only be sent as a response to the previously received message.
     * A message tunnel is needed for the message to be delivered.
     * @param content
     * @param origin
     */
    public void sendResponse(MsgBase content, NetworkID origin) {
        //Getting the appropriate message tunnel.
        MessageTunnel messageTunnel = router.messageTunnelManager.getTunnelByOrigin(origin);

        if(messageTunnel == null) {
            logger.warn("Can't send a response to: {}, the tunnel doesn't exist!", origin);
            return;
        }

        //Using an existing tunnel.
        NetworkMessage message = constructMessage(messageTunnel, content, NetworkMessage.MessageDirection.TO_ORIGIN);
        messageTunnel.encryptMsgSymmetric(message);
        forwardMessage(message, Node.self);
    }

    /**
     * Constructs a new network message.
     * @param direction the direction in which the message should be routed.
     * @param content the content of the network message.
     * @param tunnel the message tunnel of the message. Has the origin and destination parameters.
     */
    public NetworkMessage constructMessage(MessageTunnel tunnel, MsgBase content, NetworkMessage.MessageDirection direction) {
        NetworkMessage message;
        message = new NetworkMessage(tunnel.getOrigin(), tunnel.getDestination(), content, direction);

        message.serialize();
        message.sign();
        content.networkMessage = message;
        return message;
    }



    /**
     * Forwards a message to the next closest node.
     * @param message the message to be forwarded.
     */
    public void forwardMessage(NetworkMessage message, Node forwarder) {
        //Ignore node info.
        if(message.content != null && message.content.getType() == MsgType.NODE_INFO) {
            return;
        }

        logger.info("A network message to: {}, received from: {}", message.getDestination(), forwarder);
        message.updateDetails();

        if(!message.isValid()) {
            logger.warn("Message: {} is invalid, the message won't be forwarded!", message);
            return;
        }

        //Getting the next node in the tunnel.
        MessageTunnel tunnel = router.messageTunnelManager.getOrCreateTunnel(message.getOrigin(), message.getDestination());
        Node nextNode;

        if(message.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
            //Getting the closest neighbor to forward the message to.
            tunnel.setPreviousNode(forwarder);
            tunnel.setNextNode(router.neighborNodesManager.getClosestTo(message.getDestination()));
            nextNode = tunnel.getNextNode();
        } else {
            nextNode = tunnel.getPreviousNode();
        }

        //Checking if we are the last stop.
        if(nextNode == null) {
            logger.warn("Couldn't forward message: {}, next node unknown!", message);
            return;
        }
        if(nextNode == Node.self) {
            //Decrypting the message.
            //Checking if the message can be read by our node.
            //If can be read, call message received event.
            //If can't be read, ignore.
            if(decryptMessage(message, tunnel)) {
                //Message can be read.
                message.deserialize();
                EventManager.callEvent(new MessageReceivedEvent(message, forwarder));
            }
        } else {
            //Forwarding.
            nextNode.sendRaw(message);
        }
    }

    /**
     * Decrypts the received message.
     * @param message
     */
    private boolean decryptMessage(NetworkMessage message, MessageTunnel messageTunnel) {
        //Checking whether the message was encrypted at all.
        if(message.verify()) {
            return true;
        }

        //Decrypting with sender public key first.
        messageTunnel.decryptMsgSharedSecret(message);
        return message.verify();
    }

    @Override
    protected void registerEvents() {
        EventManager.registerEvent(MessageReceivedEvent.class);
        EventManager.registerEvent(ChannelMessageReceivedEvent.class);
    }

    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class); //Message received event.
        EventManager.registerHandler(this, MessageTunnelEstablishedEvent.class); //Message tunnel established event.
    }

    @Override
    public void onEventCalled(Event e) {
        if(e instanceof ChannelMessageReceivedEvent) { //A network message was received.
            ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
            NetworkMessage message = messageReceivedEvent.message;

            forwardMessage(message, messageReceivedEvent.remoteNode);
        } else if (e instanceof MessageTunnelEstablishedEvent) {
            MessageTunnelEstablishedEvent messageTunnelEstablishedEvent = (MessageTunnelEstablishedEvent)e;
            MessageTunnel tunnel = messageTunnelEstablishedEvent.messageTunnel;

            if(messageTunnelEstablishedEvent.establishDirection == NetworkMessage.MessageDirection.TO_ORIGIN) {
                logger.info("Sending {} queued messages through the established tunnel: \n{}", tunnel.messageQueue.size(),
                        NetworkID.getStringValue(tunnel.getOtherPublicKey().getEncoded()));

                //Sending messages through the tunnel.
                for (NetworkMessage msg :
                        tunnel.messageQueue) {
                    tunnel.encryptMsgSymmetric(msg);
                    forwardMessage(msg, Node.self);
                }
            }
        }
    }
}
