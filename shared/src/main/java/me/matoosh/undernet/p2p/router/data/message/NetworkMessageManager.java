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
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnelSide;
import org.bouncycastle.util.encoders.Base64;
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
    public void sendMessage(MsgBase content, NetworkID recipient) {
        //Getting the appropriate message tunnel.
        MessageTunnel messageTunnel = router.messageTunnelManager.getOrCreateTunnel(Node.self.getIdentity().getNetworkId(), recipient, MessageTunnelSide.ORIGIN);

        //Constructing the message.
        NetworkMessage message = constructMessage(messageTunnel, content, NetworkMessage.MessageDirection.TO_DESTINATION);

        //Checking if the tunnel has been created.
        switch(messageTunnel.getTunnelState()) {
            case ESTABLISHED:
                //Using an existing tunnel.
                messageTunnel.encryptMsgSymmetric(message);
                forwardMessage(message, Node.self);
                break;
            case HOSTED:
                logger.warn("Can't send messages through a hosted tunnel!");
                break;
            default:
                //Adding message to the queue.
                messageTunnel.messageQueue.add(message);
                if(messageTunnel.messageQueue.size() == 1) {
                    //Establishing the tunnel.
                    router.messageTunnelManager.establishTunnel(messageTunnel);
                }
                break;
        }
    }

    /**
     * Constructs a response message and starts the sending process to the origin node.
     * The response can only be sent as a response to the previously received message.
     * A message tunnel is needed for the message to be delivered.
     * @param content
     */
    public void sendResponse(MsgBase content, MessageTunnel tunnel) {
        if(tunnel == null) {
            logger.warn("Can't send a response, the tunnel is null!");
            return;
        }

        //Checking if the tunnel has been created.
        switch(tunnel.getTunnelState()) {
            case ESTABLISHED:
                //Using an existing tunnel.
                NetworkMessage message = constructMessage(tunnel, content, NetworkMessage.MessageDirection.TO_ORIGIN);
                tunnel.encryptMsgSymmetric(message);
                forwardMessage(message, Node.self);
                break;
            case HOSTED:
                logger.warn("Can't send responses through a hosted tunnel!");
                break;
            default:
                logger.warn("Can't send responses through a not-established tunnel!");
                break;
        }
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
        content.setNetworkMessage(message);
        return message;
    }



    /**
     * Forwards a message to the next closest node.
     * @param message the message to be forwarded.
     */
    public void forwardMessage(NetworkMessage message, Node forwarder) {
        //Ignore node info.
        if(message.getContent() != null && message.getContent().getType() == MsgType.NODE_INFO) {
            return;
        }

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
        if(nextNode == null) {
            logger.warn("Couldn't forward message: {}, next node unknown!", message);
            return;
        }

        if(forwarder == Node.self) {
            if(message.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
                logger.info("Forwarding message, ({}) -> ({}) -> (...) -> ({})", Node.self, tunnel.getNextNode(), message.getDestination());
            } else {
                logger.info("Forwarding message, ({}) <- (...) <- ({}) <- ({})", message.getOrigin(), tunnel.getPreviousNode(), Node.self);
            }
        } else {
            if(message.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
                logger.info("Forwarding message, ({}) -> (...) -> ({}) -> ({}) -> ({}) -> (...) -> ({})", message.getOrigin(), tunnel.getPreviousNode(), Node.self, tunnel.getNextNode(), message.getDestination());
            } else {
                logger.info("Forwarding message, ({}) <- (...) <- ({}) <- ({}) <- ({}) <- (...) <- ({})", message.getOrigin(), tunnel.getPreviousNode(), Node.self, forwarder, message.getDestination());
            }

        }

        //Checking if we are the last stop.
        if(nextNode == Node.self) {
            //Decrypting the message.
            //Checking if the message can be read by our node.
            //If can be read, call message received event.
            //If can't be read, ignore.
            if(decryptMessage(message, tunnel)) {
                //Message can be read.
                message.deserialize();

                //Set last message received on tunnel.
                message.getTunnel().setLastMessageTime(System.currentTimeMillis());

                //Calling received event.
                EventManager.callEvent(new MessageReceivedEvent(message, forwarder));
            } else {
                //Message can't be read.
                logger.warn("Couldn't read the incoming message! Signature: {}", Base64.encode(message.getSignature()));
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
            logger.info("Message: {}, doesn't need to be decrypted...", message);
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

            System.out.println("DD " + messageReceivedEvent.remoteNode);
            forwardMessage(message, messageReceivedEvent.remoteNode);
        } else if (e instanceof MessageTunnelEstablishedEvent) {
            MessageTunnelEstablishedEvent messageTunnelEstablishedEvent = (MessageTunnelEstablishedEvent)e;
            MessageTunnel tunnel = messageTunnelEstablishedEvent.messageTunnel;

            if(messageTunnelEstablishedEvent.establishDirection == NetworkMessage.MessageDirection.TO_DESTINATION) {
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
