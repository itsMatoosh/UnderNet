package me.matoosh.undernet.p2p.router.server;

import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;

/**
 * Handles data transfered over a channel.
 * Created by Mateusz Rębacz on 21.09.2017.
 */

public class ServerMessageHandler extends ChannelInboundHandlerAdapter {
    /**
     * The logger of the class.
     */
    public static org.slf4j.Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);

    /**
     * Called when a channel is registered.
     *
     * @param ctx
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    /**
     * Called when a channel is unregistered.
     *
     * @param ctx
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    /**
     * Called when data has been received from the channel.
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
        logger.error("Error with connection: " + ctx.name(), cause);
        EventManager.callEvent(new ChannelErrorEvent());

        //Closing the connection.
        ctx.close();
    }
}
