package me.matoosh.undernet.p2p.router.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageDecoder;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageEncoder;

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
        //Registering the server channel handler.
        ch.pipeline().addLast(new NetworkMessageEncoder());
        ch.pipeline().addLast(new NetworkMessageDecoder());
        ch.pipeline().addLast(new ServerNetworkMessageHandler(server));
    }

    /**
     * Handle the {@link Throwable} by logging and closing the {@link Channel}. Sub-classes may override this.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Server.logger.error("An error occured while initializing the connection from: " + ctx.channel().remoteAddress(), cause);
        EventManager.callEvent(new ChannelErrorEvent(ctx.channel(), true, cause));

        //Closing the connection.
        ctx.close();
    }
}
