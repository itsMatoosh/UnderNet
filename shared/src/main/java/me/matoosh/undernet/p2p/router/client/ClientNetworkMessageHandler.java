package me.matoosh.undernet.p2p.router.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelClosedEvent;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Handles data transfered over a channel.
 * Created by Mateusz Rębacz on 21.09.2017.
 */

public class ClientNetworkMessageHandler extends ChannelInboundHandlerAdapter {

    /**
     * The client of this channel handler.
     */
    public Client client;

    //Attribute ids.
    /**
     * Defines the server node attribute id.
     */
    public static final AttributeKey<Node> ATTRIBUTE_KEY_SERVER_NODE = AttributeKey
            .valueOf("ServerNode");

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(ClientNetworkMessageHandler.class);

    public ClientNetworkMessageHandler(Client client) {
        this.client = client;
    }

    /**
     * Called when a channel becomes active.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //Checking if connected already.
        for (Node n :
                client.router.getRemoteNodes()) {
            if (n.getAddress().equals(ctx.channel().remoteAddress())) {
                ctx.disconnect();
                return;
            }
        }

        //Adding the channel to the client list.
        client.channels.add(ctx.channel());

        //Adding a node object to the connection.
        Node serverNode = new Node();
        serverNode.setAddress((InetSocketAddress) ctx.channel().remoteAddress()); //Setting the node's address.
        serverNode.channel = ctx.channel();
        ctx.channel().attr(ATTRIBUTE_KEY_SERVER_NODE).set(serverNode);

        //Adding the server node to the connected nodes list.
        client.router.addConnectedNode(serverNode);

        //Calling the channel created event.
        EventManager.callEvent(new ChannelCreatedEvent(ctx.channel(), false));
    }

    /**
     * Called when a channel becomes inactive.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //Removing the channel from the client list.
        client.channels.remove(ctx.channel());

        //Removing the server node from the connectedNodes list.
        Node serverNode = ctx.channel().attr(ATTRIBUTE_KEY_SERVER_NODE).get();
        serverNode.channel = null;
        client.router.removeConnectedNode(serverNode);

        //Removing tunnels with node.
        client.router.messageTunnelManager.closeTunnelsOnDisconnect(serverNode);

        //Calling the channel closed event.
        EventManager.callEvent(new ChannelClosedEvent(ctx.channel(), false));
    }

    /**
     * Called when a channel is being read.
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof NetworkMessage) {
            //Reading the incoming content as a NetworkMessage.
            NetworkMessage networkMessage = (NetworkMessage) msg;
            try {
                EventManager.callEvent(new ChannelMessageReceivedEvent(ctx.channel(), false, networkMessage));
            } finally {
                networkMessage = null; //Releasing the msg from memory.
            }
        }
    }

    /**
     * Called when an exception is called.
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Logging the exception.
        logger.error("Error with connection to: " + ctx.channel().remoteAddress(), cause);
        EventManager.callEvent(new ChannelErrorEvent(ctx.channel(), false, cause));

        //Closing the connection.
        ctx.close();
    }
}
