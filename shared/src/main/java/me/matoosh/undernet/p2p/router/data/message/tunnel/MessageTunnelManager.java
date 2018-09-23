package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
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
     * Gets the message tunnel of a particular other.
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
     * Gets the message tunnel of a particular origin.
     * @param origin
     * @return
     */
    public MessageTunnel getTunnelByOrigin(NetworkID origin) {
        for (int i = 0; i < messageTunnels.size(); i++) {
            MessageTunnel tunnel = messageTunnels.get(i);
            if(tunnel.getOrigin().equals(origin)) {
                return tunnel;
            }
        }
        return null;
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
     * Closes the given message tunnel.
     * @param tunnel
     */
    public void closeTunnel(MessageTunnel tunnel) {
        //TODO
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
        NetworkMessage tunnelRequest = router.networkMessageManager.constructMessage(tunnel, new TunnelEstablishResponseMessage(Node.self.getIdentity().getPublicKey()), NetworkMessage.MessageDirection.TO_ORIGIN);
        router.networkMessageManager.forwardMessage(tunnelRequest, Node.self);
    }
    /**
     * Gets or creates a message tunnel.
     * @return
     */
    public MessageTunnel getOrCreateTunnel(NetworkID origin, NetworkID destination) {
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

    @Override
    protected void registerEvents() {
        EventManager.registerEvent(MessageTunnelEstablishedEvent.class);
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
            if(messageReceivedEvent.networkMessage.content.getType() == MsgType.TUNNEL_ESTABLISH_REQUEST) {
                TunnelEstablishRequestMessage tunnelEstablishRequestMessage = (TunnelEstablishRequestMessage) messageReceivedEvent.networkMessage.content;
                MessageTunnel tunnel = getOrCreateTunnel(tunnelEstablishRequestMessage.networkMessage.getOrigin(), tunnelEstablishRequestMessage.networkMessage.getDestination());

                //Checking if we need to reply with our public key.
                if(messageReceivedEvent.networkMessage.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
                    //Setting the other's public key.
                    tunnel.setOtherPublicKey(tunnelEstablishRequestMessage.networkMessage.getOrigin().getPublicKey());

                    //Calculating the shared secret.
                    tunnel.calcSharedSecret();

                    sendTunnelResponse(tunnel);

                    //Calling the event.
                    EventManager.callEvent(new MessageTunnelEstablishedEvent(tunnel, NetworkMessage.MessageDirection.TO_DESTINATION));
                }
            } else if(messageReceivedEvent.networkMessage.content.getType() == MsgType.TUNNEL_ESTABLISH_RESPONSE) {
                TunnelEstablishResponseMessage tunnelEstablishResponseMessage = (TunnelEstablishResponseMessage) messageReceivedEvent.networkMessage.content;
                MessageTunnel tunnel = getOrCreateTunnel(tunnelEstablishResponseMessage.networkMessage.getOrigin(), tunnelEstablishResponseMessage.networkMessage.getDestination());

                //Setting the other's public key.
                try {
                    tunnel.setOtherPublicKey(KeyTools.fromUncompressedPoint(tunnelEstablishResponseMessage.publicKey));

                    //Calculating the shared secret.
                    tunnel.calcSharedSecret();

                    //Calling the event.
                    EventManager.callEvent(new MessageTunnelEstablishedEvent(tunnel, NetworkMessage.MessageDirection.TO_ORIGIN));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}
