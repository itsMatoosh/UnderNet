package me.matoosh.undernet.p2p.router.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileChunkPacket;

/**
 * Handles server side file transfer.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ServerFileTransferHandler extends ChannelInboundHandlerAdapter {
    /**
     * The server of the handler.
     */
    public Server server;

    public ServerFileTransferHandler(Server server) {
        this.server = server;
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
        if(msg instanceof FileChunkPacket) {
            FileChunkPacket chunk = (FileChunkPacket)msg;
            try {
                //TODO: Add data to file transfer.
            } finally {
                chunk = null; //Releasing the chunk from memory.
            }
        }
    }
}
