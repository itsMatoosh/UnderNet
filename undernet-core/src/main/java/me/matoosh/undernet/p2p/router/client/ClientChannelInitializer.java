package me.matoosh.undernet.p2p.router.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessageDecoder;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessageEncoder;
import me.matoosh.undernet.p2p.router.server.Server;
import me.matoosh.undernet.p2p.router.server.ServerChannelHandler;

/**
 * Used to initialize client-side channels.
 * Created by Mateusz Rębacz on 21.09.2017.
 */

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel>{

    /**
     * The client of this channel initializer.
     */
    public Client client;

    public ClientChannelInitializer(Client client) {
        this.client = client;
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
    protected void initChannel(SocketChannel ch) throws Exception {
        //Registering the client channel handler.
        ch.pipeline().addLast(new NetworkMessageEncoder());
        ch.pipeline().addLast(new NetworkMessageDecoder());
        ch.pipeline().addLast(new ClientChannelHandler(client));
    }

    /**
     * Handle the {@link Throwable} by logging and closing the {@link Channel}. Sub-classes may override this.
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Client.logger.error("An error occured while initializing the connection to: " + ctx.channel().remoteAddress(), cause);
        EventManager.callEvent(new ChannelErrorEvent(ctx.channel(), false, cause));

        //Closing the connection.
        ctx.close();
    }
}
