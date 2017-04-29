package me.matoosh.undernet.p2p.router.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.InternetConnection;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.ConnectionSide;
import me.matoosh.undernet.p2p.router.messages.NetworkMessage;

/**
 * Server part of the router.
 *
 * Created by Mateusz Rębacz on 30.01.2017.
 */

public class Server {
    /**
     * Port used by the server.
     */
    public int port;
    /**
     * Server socket of the server.
     */
    public ServerSocket serverSocket;
    /**
     * Current status of the server.
     */
    public ServerStatus status = ServerStatus.NOT_STARTED;
    /**
     * Whether the server should stop.
     */
    private boolean shouldStop = false;
    /**
     * Whether the server is accpeting clientConnections.
     */
    private boolean acceptingConnections = false;
    /**
     * List of the active clientConnections.
     */
    public ArrayList<Connection> connections = new ArrayList<Connection>();

    /**
     * Creates a server instance using a specified port.
     * @param port
     */
    public Server(int port) {
        this.port = port;
    }

    /**
     * Starts the server.
     * @throws Exception
     */
    public void start() throws Exception {
        //Setting this as the currently used server.
        Node.self.server = this;

        //Registering events
        registerEvents();

        //The server loop.
        Thread connectionAssignmentThread = new Thread(new Runnable() {
            @Override
            public void run() {
                EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.STARTING));
                acceptingConnections = true;

                try {
                    //Creating and binding a server socket.
                    if (serverSocket != null) {
                        UnderNet.logger.error("Server socket already bound");
                    }
                    serverSocket = new ServerSocket(42069);
                    EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.RUNNING));

                    //Connection accepting loop.
                    while(!shouldStop) {
                        //If no new clientConnections are awaiting, continue the loop.
                        if(!acceptingConnections) continue;

                        //Set the pending connection flag to false.
                        acceptingConnections = false;

                        //Listening for the incoming connection and accepting it on a separate thread.
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    connections.add(new InternetConnection(Node.self, null, Thread.currentThread(), ConnectionSide.SERVER));
                                } catch (Exception e) {
                                    UnderNet.logger.error("Error handling incoming connection: " + e.toString());
                                }
                            }
                        });

                        t.start();

                        acceptingConnections = true;
                    }
                } catch (IOException e) {
                    //And error occurred in the server logic.
                    e.printStackTrace();
                    EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.ERROR));
                } finally {
                    //Server stopped.
                    if(status != ServerStatus.ERROR) {
                        EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.STOPPED));
                    }
                    shouldStop = false;
                }
            }
        });
        connectionAssignmentThread.start();
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        //ServerStatusEvent
        EventManager.registerEvent(ServerStatusEvent.class);
    }

    /**
     * Stops the server.
     */
    public void stop() {
        //Stopping the server loop.
        shouldStop = true;

        //Interrupting all the clientConnections.
        for (ServerConnection c:
             connections) {
            c.drop();
        }

        //Closing the socket.
        try {
            if(serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to a connection.
     * @param message
     * @param connection
     */
    public void sendMessage(NetworkMessage message, ServerConnection connection) {

    }

    /**
     * Called when a message has been received.
     * @param sender
     */
    private void onMessageReceived(NetworkMessage message, ServerConnection sender) {

    }

    //Events

    /**
     * Called when a connection has been established.
     * @param c
     */
    public void onConnectionEstablished(ServerConnection c) {
        UnderNet.logger.info("New connection established with " + c.node);
        //Accepting new clientConnections.
        acceptingConnections = true;
    }
}
