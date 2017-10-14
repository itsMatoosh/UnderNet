package me.matoosh.undernet.p2p.router.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileChunkMessage;

/**
 * Handles client side file transfer.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ClientFileTransferHandler extends ChannelInboundHandlerAdapter {
    /**
     * The client of the handler.
     */
    public Client client;

    public ClientFileTransferHandler(Client client) {
        this.client = client;
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
        if(msg instanceof FileChunkMessage) {
            FileChunkMessage chunk = (FileChunkMessage)msg;
            try {
                //TODO: Add data to file transfer.
            } finally {
                chunk = null; //Releasing the chunk from memory.
            }
        }
    }
}
