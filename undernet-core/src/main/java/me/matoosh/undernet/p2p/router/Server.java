package me.matoosh.undernet.p2p.router;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Logger;

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
     * Whether the server is running.
     */
    public boolean running = false;
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
        running = true;
        acceptingConnections = true;

        try {
            serverSocket = new ServerSocket(port);

            //The server loop.
            while(running) {
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
                            Logger.getGlobal().info("Connection error: " + e.toString());
                        }
                    }
                });

                t.start();
            }
        }
        finally {
            //Server stopped.
            running = false;
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        //Stopping the server loop.
        running = false;

        //Interrupting all the connections.
        for (Connection c:
             connections) {
            if (c.thread != null
            && c.thread.isAlive()) {
                c.thread.interrupt();
            }
        }
    }

    //Events

    /**
     * Called when a connection has been established.
     * @param c
     */
    public void onConnectionEstablished(Connection c) {
        //Accepting new connections.
        acceptingConnections = true;
    }
}
