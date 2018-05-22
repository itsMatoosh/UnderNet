package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.tunnel.MessageTunnelEstablishedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Manages the sending of network messages.
 * Will keep track of active message tunnels, both incoming and outcoming.
 */
public class NetworkMessageManager extends Manager {
    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkMessageManager.class);

    /**
     * The currently active message tunnels.
     */
    public ArrayList<MessageTunnel> messageTunnels = new ArrayList<MessageTunnel>();

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public NetworkMessageManager(Router router) {
        super(router);
    }

    /**
     * Constructs a message and starts the sending process to the recipient node.
     * If a tunnel is not yet established with the recipient node, it will be established now.
     * If a tunnel is already active between with the recipient node, the message will be sent through it.
     * @param content
     * @param recipient
     */
    public void sendMessage(MsgBase content, NetworkID recipient) {
        //Getting the appropriate message tunnel.
        MessageTunnel messageTunnel = getOrCreateTunnel(Node.self.getIdentity().getNetworkId(), recipient);

        //Checking if the tunnel has been created.
        if(messageTunnel == null) {
            //Constructing a new tunnel and sending the message after the tunnel has been established.
            sendTunnelRequest(messageTunnel);
            EventManager.registerHandler(new EventHandler() {
                @Override
                public void onEventCalled(Event e) {
                    //Sending the message through the tunnel.
                    NetworkMessageManager.this.sendMessage(content, recipient);

                    EventManager.unregisterHandler(this, MessageTunnelEstablishedEvent.class);
                }
            }, MessageTunnelEstablishedEvent.class);
        } else {
            //Using an existing tunnel.
            NetworkMessage message = constructMessage(recipient, content, NetworkMessage.MessageDirection.TO_DESTINATION);
            message.encrypt(messageTunnel.getRecipientPublicKey());
            encryptWithSelf(message);
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
        MessageTunnel messageTunnel = getTunnel(origin);

        if(messageTunnel == null) {
            logger.warn("Can't send a response to: {}, the tunnel doesn't exist!", origin);
            return;
        }

        //Using an existing tunnel.
        NetworkMessage message = constructMessage(origin, content, NetworkMessage.MessageDirection.TO_ORIGIN);
        message.encrypt(messageTunnel.getRecipientPublicKey());
        encryptWithSelf(message);
        forwardMessage(message, Node.self);
    }

    /**
     * Gets the message tunnel of a particular recipient.
     * @param origin
     * @param destination
     * @return
     */
    public MessageTunnel getTunnel(NetworkID origin, NetworkID destination) {
        for (int i = 0; i < messageTunnels.size(); i++) {
            MessageTunnel tunnel = messageTunnels.get(i);
            if(tunnel.getDestination().equals(origin) && tunnel.getOrigin().equals(destination)) {
                return tunnel;
            }
        }
        return null;
    }
    /**
     * Gets the message tunnel of a particular recipient.
     * @param origin
     * @return
     */
    public MessageTunnel getTunnel(NetworkID origin) {
        for (int i = 0; i < messageTunnels.size(); i++) {
            MessageTunnel tunnel = messageTunnels.get(i);
            if(tunnel.getDestination().equals(origin)) {
                return tunnel;
            }
        }
        return null;
    }

    /**
     * Sends a tunnel creation request to the destination of the tunnel.
     * @param tunnel
     */
    public void sendTunnelRequest(MessageTunnel tunnel) {
        //Sending a tunnel request.
        try {
            tunnel.generateSelfKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        NetworkMessage tunnelRequest = constructMessage(tunnel.getDestination(), new TunnelEstablishRequestMessage(tunnel.getSelfPublicKey()), NetworkMessage.MessageDirection.TO_DESTINATION);
        encryptWithSelf(tunnelRequest);
        forwardMessage(tunnelRequest, Node.self);
    }

    /**
     * Sends a tunnel creation response to the origin of the tunnel.
     * @param tunnel
     */
    public void sendTunnelResponse(MessageTunnel tunnel) {
        //Sending a tunnel request.
        try {
            tunnel.generateSelfKeys();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        NetworkMessage tunnelRequest = constructMessage(tunnel.getOrigin(), new TunnelEstablishRequestMessage(tunnel.getSelfPublicKey()), NetworkMessage.MessageDirection.TO_ORIGIN);
        encryptWithSelf(tunnelRequest);
        forwardMessage(tunnelRequest, Node.self);
    }

    /**
     * Constructs a new network message.
     * @param direction the direction in which the message should be routed.
     * @param content the content of the network message.
     * @param recipient the recipient of the message. Resource ID if direction = 0, Node id if direction = 1 & a tunnel is active with the destination.
     * @return
     */
    private NetworkMessage constructMessage(NetworkID recipient, MsgBase content, NetworkMessage.MessageDirection direction) {
        NetworkMessage message;
        if(direction == NetworkMessage.MessageDirection.TO_DESTINATION) {
            message = new NetworkMessage(Node.self.getIdentity().getNetworkId(), recipient, content, direction.value);
        } else {
            message = new NetworkMessage(recipient, Node.self.getIdentity().getNetworkId(), content, direction.value);
        }

        message.serialize();
        message.calcChecksum();
        content.networkMessage = message;
        return message;
    }

    /**
     * Encrypts a network message with the self private key.
     * @param message message to be encrypted.
     */
    private void encryptWithSelf(NetworkMessage message) {
        message.encrypt(Node.self.getIdentity().getPrivateKey());
    }

    /**
     * Gets or creates a message tunnel.
     * @return
     */
    private MessageTunnel getOrCreateTunnel(NetworkID origin, NetworkID destination) {
        //Finding an existing tunnel.
        for (MessageTunnel tunnel :
                messageTunnels) {
            if (tunnel.getOrigin().equals(origin) && tunnel.getDestination().equals(destination)) {
                return tunnel;
            }
        }

        //Creating a tunnel if doesn't exist already.
        MessageTunnel tunnel = new MessageTunnel(origin, destination);
        messageTunnels.add(tunnel);
        return tunnel;
    }

    /**
     * Forwards a message to the next closest node.
     * @param message the message to be forwarded.
     */
    private void forwardMessage(NetworkMessage message, Node forwarder) {
        if(!message.isValid()) {
            logger.warn("Message: {} is invalid, the message won't be forwarded!", message);
            return;
        }

        //Getting the next node in the tunnel.
        MessageTunnel tunnel = getOrCreateTunnel(message.getOrigin(), message.getDestination());
        tunnel.setPreviousNode(forwarder);

        Node nextNode;

        if(message.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
            //Getting the closest neighbor to forward the message to.
            tunnel.setNextNode(router.neighborNodesManager.getClosestTo(message.getDestination()));
            nextNode = tunnel.getNextNode();
        } else {
            nextNode = tunnel.getPreviousNode();
        }


        //Checking if we are the last stop.
        if(nextNode == Node.self) {
            //Decrypting the message.
            //Checking if the message can be read by our node.
            //If can be read, call message received event.
            //If can't be read, ignore.
            if(decryptMessage(message, tunnel)) {
                //Message can be read.
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
        if(message.checkIntegrity()) {
            return true;
        }

        //Decrypting with sender public key first.
        message.decrypt(message.getOrigin().getPublicKey());
        if(!message.checkIntegrity() && messageTunnel.getSelfPrivateKey() != null) {
            //Looking for an appropriate tunnel.
            message.decrypt(messageTunnel.getSelfPrivateKey());
            if(message.checkIntegrity()) {
                return true;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void registerEvents() {
        EventManager.registerEvent(MessageTunnelEstablishedEvent.class);
        EventManager.registerEvent(MessageReceivedEvent.class);
    }

    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class); //Message received event.
    }

    @Override
    public void onEventCalled(Event e) {
        if(e instanceof ChannelMessageReceivedEvent) { //A network message was received.
            ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
            NetworkMessage message = messageReceivedEvent.message;

            forwardMessage(message, messageReceivedEvent.remoteNode);
        } else if(e instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent)e;
            if(messageReceivedEvent.networkMessage.content.getType() == MsgType.TUNNEL_ESTABLISH_REQUEST) {
                TunnelEstablishRequestMessage tunnelEstablishRequestMessage = (TunnelEstablishRequestMessage) messageReceivedEvent.networkMessage.content;
                MessageTunnel tunnel = getTunnel(tunnelEstablishRequestMessage.networkMessage.getOrigin(), tunnelEstablishRequestMessage.networkMessage.getDestination());

                //Setting the recipient's public key.
                tunnel.setRecipientPublicKey(tunnelEstablishRequestMessage.encryptionKey);

                //Checking if we need to reply with our public key.
                if(messageReceivedEvent.networkMessage.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
                    sendTunnelResponse(tunnel);
                }
            }
        }
    }
}
