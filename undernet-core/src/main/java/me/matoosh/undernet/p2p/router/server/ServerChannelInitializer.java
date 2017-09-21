package me.matoosh.undernet.p2p.router.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;

/**
 * Used to initialize server-side channels.
 * Created by Mateusz Rębacz on 21.09.2017.
 */

public class ServerChannelInitializer extends ChannelInitializer {
    /**
     * The server behind this initializer.
     */
    public Server server;

    public ServerChannelInitializer(Server server) {
        this.server = server;
    }

    /**
     * This method will be called once the {@link Channel} was registered. After the method returns this instance
     * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
     *
     * @param ch the {@link Channel} which was registered.
     * @throws Exception is thrown if an error occurs. In that case it will be handled by
     *                   {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
     *                   the {@link Channel}.
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        //Registering the server message handler.
        ch.pipeline().addLast(new ServerChannelHandler(server));
    }

    /**
     * Handle the {@link Throwable} by logging and closing the {@link Channel}. Sub-classes may override this.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
