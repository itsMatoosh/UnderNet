package me.matoosh.undernet.p2p.router.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.p2p.node.Node;

/**
 * Handles data transfered over a channel.
 * Created by Mateusz Rębacz on 21.09.2017.
 */

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    //Attribute ids.
    /**
     * Defines the server node attribute id.
     */
    protected static final AttributeKey<Node> ATTRIBUTE_KEY_SERVER_NODE = AttributeKey
            .valueOf("ServerNode");

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(ClientChannelHandler.class);

    /**
     * Calls {@link ChannelHandlerContext#fireChannelActive()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //Adding a node object to the connection.
        Node serverNode = new Node();
        serverNode.address = ctx.channel().remoteAddress(); //Setting the node's address.
        ctx.channel().attr(ATTRIBUTE_KEY_SERVER_NODE).set(serverNode);
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelInactive()} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    /**
     * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward
     * to the next {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //Reading the incoming message as a string.
        ByteBuf in = (ByteBuf) msg;
        try {
            System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
        } finally {
            in.release(); //Releasing the buffer from memory.
        }
    }

    /**
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to forward
     * to the next {@link ChannelHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //Logging the exception.
        logger.error("Error with connection to: " + ctx.channel().remoteAddress(), cause);
        EventManager.callEvent(new ChannelErrorEvent(ctx.channel(), cause));

        //Closing the connection.
        ctx.close();
    }
}
