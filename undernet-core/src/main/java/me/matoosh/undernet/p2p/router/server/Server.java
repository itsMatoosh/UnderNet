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
     * Whether the server is accpeting serverConnections.
     */
    private boolean acceptingConnections = false;
    /**
     * List of the active serverConnections.
     */
    public ArrayList<ServerConnection> serverConnections = new ArrayList<ServerConnection>();

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
                    if (serverSocket != null) {
                        UnderNet.logger.error("Server socket already bound?!?!");
                    }
                    serverSocket = new ServerSocket(42069);
                    EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.RUNNING));

                    while(!shouldStop) {
                        //If no new serverConnections are awaiting, continue the loop.
                        if(!acceptingConnections) continue;

                        //Set the pending connection flag to false.
                        acceptingConnections = false;

                        //Listening for the incoming connection and accepting it on a separate thread.
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    serverConnections.add(new ServerConnection(Server.this, Thread.currentThread()));
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
                    //Closing the socket.
                    try {
                        if(serverSocket != null) {
                            serverSocket.close();
                            serverSocket = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //Changing the status of the server.
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

        //Interrupting all the serverConnections.
        for (ServerConnection c:
                serverConnections) {
            c.drop();
        }
    }
    //Events

    /**
     * Called when a connection has been established.
     * @param c
     */
    public void onConnectionEstablished(ServerConnection c) {
        UnderNet.logger.info("New connection established with " + c.node);
        //Accepting new serverConnections.
        acceptingConnections = true;
    }
}
