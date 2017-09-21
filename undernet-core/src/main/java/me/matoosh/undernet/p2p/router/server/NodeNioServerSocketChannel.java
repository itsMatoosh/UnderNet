package me.matoosh.undernet.p2p.router.server;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import me.matoosh.undernet.p2p.node.Node;

/**
 * @link NioServerSocketChannel with Node information.
 * Created by Mateusz Rębacz on 21.09.2017.
 */

public class NodeNioServerSocketChannel extends NioServerSocketChannel {
    /**
     * The node this channel is connected to.
     */
    public Node other;
}
