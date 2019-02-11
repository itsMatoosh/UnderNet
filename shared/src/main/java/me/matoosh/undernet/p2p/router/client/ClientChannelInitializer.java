package me.matoosh.undernet.p2p.router.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageDecoder;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageEncoder;

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
     * Called when a channel is being initialized.
     * @param ch the initialized channel.
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //Registering the client channel handler.
        ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
        ch.pipeline().addLast(new LengthFieldPrepender(3));
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 3, 0, 3));
        ch.pipeline().addLast(new NetworkMessageEncoder());
        ch.pipeline().addLast(new NetworkMessageDecoder());
        ch.pipeline().addLast(new ClientNetworkMessageHandler(client));
    }

    /**
     * Handles exceptions.
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Client.logger.error("An error occurred while initializing the connection to: " + ctx.channel().remoteAddress(), cause);
        EventManager.callEvent(new ChannelErrorEvent(ctx.channel(), false, cause));

        //Closing the connection.
        ctx.close();
    }
}
