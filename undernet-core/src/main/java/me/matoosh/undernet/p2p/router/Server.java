package me.matoosh.undernet.p2p.router;

import io.netty.bootstrap.ServerBootstrap;

/**
 * Server part of the router.
 *
 * Created by Mateusz Rębacz on 30.01.2017.
 */

public class Server {
    private static final ServerBootstrap bootstrap = new ServerBootstrap(
      new NioServerSocketChannelFactory(


      )
    );
}
