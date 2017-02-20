package me.matoosh.undernet.p2p.router.client.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Random;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Created by Mateusz Rębacz on 20.02.2017.
 */

public class InternetConnection extends Connection {
    public InternetConnection(Client client, Node node, Thread thread) throws Exception {
        super(client, node, thread);
    }

    @Override
    public void init() throws ConnectionException, IOException {
        //Connecting to the node.
        client.clientSocket.connect(new InetSocketAddress(node.address, new Random().nextInt()));
    }

    @Override
    public void session() {
        //TODO: Session logic.
    }

    @Override
    public void drop() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
