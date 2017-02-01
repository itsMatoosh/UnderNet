package me.matoosh.undernet.p2p.router.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by Mateusz Rebacz on 01.02.2017.
 */

public class ResourceReceiveHandler extends ChannelHandlerAdapter {
    /**
     * Called when a message is received from a neighboring node.
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    /**
     * Called when an exception occurs.
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

        //TODO: Add automatic node reconnecting, or in case a node is offline connect to a different node.
    }
}
