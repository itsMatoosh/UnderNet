package me.matoosh.undernet.p2p.router.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerStatusEvent;

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
     * Whether the server is accpeting connections.
     */
    private boolean acceptingConnections = false;
    /**
     * List of the active connections.
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
                    serverSocket = new ServerSocket(42069);
                    EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.RUNNING));

                    while(!shouldStop) {
                        //If no new connections are awaiting, continue the loop.
                        if(!acceptingConnections) continue;

                        //Set the pending connection flag to false.
                        acceptingConnections = false;

                        //Listening for the incoming connection and accepting it on a separate thread.
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    connections.add(new Connection(Server.this, Thread.currentThread()));
                                } catch (Exception e) {
                                    UnderNet.logger.error("Error handling incoming connection: " + e.toString());
                                }
                            }
                        });

                        t.start();
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

        //Interrupting all the connections.
        for (Connection c:
             connections) {
            c.drop();
        }
    }

    //Events

    /**
     * Called when a connection has been established.
     * @param c
     */
    public void onConnectionEstablished(Connection c) {
        UnderNet.logger.info("New connection established with " + c.node);
        //Accepting new connections.
        acceptingConnections = true;
    }
}
