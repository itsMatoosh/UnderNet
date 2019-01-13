package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.tunnel.MessageTunnelClosedEvent;
import me.matoosh.undernet.event.channel.message.tunnel.MessageTunnelEstablishedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Manages message tunnels.
 */
public class MessageTunnelManager extends Manager {
    /**
     * The currently active message tunnels.
     */
    public ArrayList<MessageTunnel> messageTunnels = new ArrayList<>();

    /**
     * The class logger.
     */
    public static final Logger logger = LoggerFactory.getLogger(MessageTunnelManager.class);

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public MessageTunnelManager(Router router) {
        super(router);
    }

    /**
     * Creates a new message tunnel instance and adds it to the tunnel list.
     * @return
     */
    public MessageTunnel createTunnel(NetworkID origin, NetworkID destination, MessageTunnelSide side) {
        MessageTunnel tunnel = new MessageTunnel(origin, destination, side);
        messageTunnels.add(tunnel);
        return tunnel;
    }

    /**
     * Establishes a tunnel.
     * @param tunnel
     */
    public void establishTunnel(MessageTunnel tunnel) {
        logger.info("[ Establishing tunnel {} ]", tunnel);

        //Sending a tunnel creation request to the other.
        sendTunnelRequest(tunnel);
    }

    /**
     * Closes tunnels when a node disconnects.
     * @param disconnected
     */
    public void closeTunnelsOnDisconnect(Node disconnected) {
        ArrayList<MessageTunnel> toClose = new ArrayList<>();
        for (int i = 0; i < messageTunnels.size(); i++) {
            MessageTunnel tunnel = messageTunnels.get(i);
            if (tunnel.getNextNode() == disconnected || tunnel.getPreviousNode() == disconnected) {
                toClose.add(tunnel);
            }
        }

        for (MessageTunnel tunnel :
                toClose) {
            closeTunnel(tunnel);
        }
    }

    /**
     * Closes the given message tunnel.
     * Doesn't send the close message, for that use the tunnel close() message.
     * @param tunnel
     */
    public void closeTunnel(MessageTunnel tunnel) {
        logger.info("Closing tunnel: {}", tunnel);
        messageTunnels.remove(tunnel);
        if(tunnel.getSide() == MessageTunnelSide.ORIGIN) {
            for (MessageTunnel tunn :
                    messageTunnels) {
                if(tunn.getDestination().equals(Node.self)) {
                    messageTunnels.remove(tunn);
                }
            }
        } else if(tunnel.getSide() == MessageTunnelSide.DESTINATION) {
            for (MessageTunnel tunn :
                    messageTunnels) {
                if(tunn.getOrigin().equals(Node.self)) {
                    messageTunnels.remove(tunn);
                }
            }
        }

        EventManager.callEvent(new MessageTunnelClosedEvent(tunnel));
    }


    /**
     * Sends a tunnel creation request to the destination of the tunnel.
     * @param tunnel
     */
    public void sendTunnelRequest(MessageTunnel tunnel) {
        //Sending a tunnel request.
        NetworkMessage tunnelRequest = router.networkMessageManager.constructMessage(tunnel, new TunnelEstablishRequestMessage(), NetworkMessage.MessageDirection.TO_DESTINATION);
        router.networkMessageManager.forwardMessage(tunnelRequest, Node.self);
    }

    /**
     * Sends a tunnel creation response to the origin of the tunnel.
     * @param tunnel
     */
    public void sendTunnelResponse(MessageTunnel tunnel) {
        //Sending a tunnel request.
        System.out.println("Sending tunnel response");
        NetworkMessage tunnelRequest = router.networkMessageManager.constructMessage(tunnel, new TunnelEstablishResponseMessage(Node.self.getIdentity().getPublicKey()), NetworkMessage.MessageDirection.TO_ORIGIN);
        router.networkMessageManager.forwardMessage(tunnelRequest, Node.self);
    }
    /**
     * Gets or creates a message tunnel.
     * @return
     */
    public MessageTunnel getTunnel(NetworkID nodeA, NetworkID nodeB) {
        //Finding an existing tunnel.
        for (MessageTunnel tunnel :
                messageTunnels) {
            if(tunnel.getOrigin().equals(nodeA) && tunnel.getDestination().equals(nodeB)) {
                return tunnel;
            }
            if(tunnel.getOrigin().equals(nodeB) && tunnel.getDestination().equals(nodeA)) {
                return tunnel;
            }
        }
        return null;
    }

    @Override
    protected void registerEvents() {
        EventManager.registerEvent(MessageTunnelEstablishedEvent.class);
        EventManager.registerEvent(MessageTunnelClosedEvent.class);
    }

    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, MessageReceivedEvent.class); //Message received event.
    }

    /**
     * Handles events.
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        if(e instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent)e;
            if(messageReceivedEvent.networkMessage.getContent().getType() == MsgType.TUNNEL_ESTABLISH_REQUEST) {
                //Called on the destination of the tunnel.
                TunnelEstablishRequestMessage tunnelEstablishRequestMessage = (TunnelEstablishRequestMessage) messageReceivedEvent.networkMessage.getContent();
                //Creating a new tunnel object.
                MessageTunnel tunnel = getTunnel(tunnelEstablishRequestMessage.getNetworkMessage().getOrigin(), tunnelEstablishRequestMessage.getNetworkMessage().getDestination());
                tunnel.setSide(MessageTunnelSide.DESTINATION);

                //Setting the other's public key.
                tunnel.setOtherPublicKey(tunnelEstablishRequestMessage.getNetworkMessage().getOrigin().getPublicKey());

                //Calculating the shared secret.
                tunnel.calcSharedSecret();

                //Sending response.
                sendTunnelResponse(tunnel);

                //Calling the established event.
                EventManager.callEvent(new MessageTunnelEstablishedEvent(messageReceivedEvent.networkMessage.getTunnel(), NetworkMessage.MessageDirection.TO_ORIGIN));
            } else if(messageReceivedEvent.networkMessage.getContent().getType() == MsgType.TUNNEL_ESTABLISH_RESPONSE) {
                //Called on the origin of the tunnel.
                TunnelEstablishResponseMessage tunnelEstablishResponseMessage = (TunnelEstablishResponseMessage) messageReceivedEvent.networkMessage.getContent();
                MessageTunnel tunnel = getTunnel(tunnelEstablishResponseMessage.getNetworkMessage().getOrigin(), tunnelEstablishResponseMessage.getNetworkMessage().getDestination());
                tunnel.setSide(MessageTunnelSide.ORIGIN);

                try {
                    //Setting the other's public key.
                    tunnel.setOtherPublicKey(KeyTools.fromUncompressedPoint(tunnelEstablishResponseMessage.publicKey));
                    //Calculating the shared secret.
                    tunnel.calcSharedSecret();
                } catch (Exception e1) {
                    logger.error("Couldn't decode the received public key for tunnel!", e1);
                }

                //Calling the event.
                EventManager.callEvent(new MessageTunnelEstablishedEvent(tunnel, NetworkMessage.MessageDirection.TO_DESTINATION));
            } else if (messageReceivedEvent.networkMessage.getContent().getType() == MsgType.TUNNEL_CLOSE_REQUEST) {
                TunnelCloseRequestMessage closeRequestMessage = (TunnelCloseRequestMessage) messageReceivedEvent.networkMessage.getContent();
                closeTunnel(closeRequestMessage.getNetworkMessage().getTunnel());
            }
        }
    }
}
