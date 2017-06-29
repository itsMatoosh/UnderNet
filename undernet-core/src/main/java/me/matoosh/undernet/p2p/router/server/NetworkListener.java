package me.matoosh.undernet.p2p.router.server;

import java.io.IOException;
import java.net.ServerSocket;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerErrorEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.NetworkConnection;

/**
 * Server module listening for incoming network connections.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class NetworkListener extends NodeListener {
    /**
     * The port on which the listener is working.
     */
    public int port = 42069;
    /**
     * The listening socket.
     */
    public ServerSocket listenSocket;
    /**
     * The thread we're listening on.
     */
    public Thread listenThread;
    /**
     * The owner server.
     */
    public Server server;

    public NetworkListener(Server server) {
        this.server = server;
    }

    /**
     * Starts listening.
     */
    @Override
    public void start() {
        //Creating a new thread.
        listenThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Starting the listen socket.
                try {
                    listenSocket = new ServerSocket(port);
                } catch (IOException e) {
                    EventManager.callEvent(new ServerErrorEvent(NetworkListener.this.server, new ServerIOException(NetworkListener.this.server)));
                }

                //Running the loop until we need to stop.
                while (!listenThread.isInterrupted()) {
                    //Creating a new NetworkConnection instance and accepting connections.
                    NetworkConnection connection = new NetworkConnection();
                    connection.receive(server, new Node());
                }
            }
        });
        listenThread.start();
    }

    /**
     * Stops listening.
     */
    @Override
    public void stop() {
        /**
         * Interrupting the thread.
         */
        listenThread.interrupt();
    }
}
