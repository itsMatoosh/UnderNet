package me.matoosh.undernet.p2p.router.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
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
     * Called when a channel is being initialized.
     * @param ch the initialized channel.
     * @throws Exception
     */
    @Override
    protected void initChannel(Channel ch) throws Exception {
        //Registering the server channel handler.
        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        ch.pipeline().addLast(new LengthFieldPrepender(3));
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 3, 0, 3));
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
