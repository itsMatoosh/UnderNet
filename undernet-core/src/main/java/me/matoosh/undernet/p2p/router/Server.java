package me.matoosh.undernet.p2p.router;

import com.jcabi.aspects.Async;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import me.matoosh.undernet.p2p.node.Node;
import sun.rmi.runtime.Log;

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
     * List of the active connections.
     */
    public ArrayList<Node> connections = new ArrayList<Node>();

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

        try {
            serverSocket = new ServerSocket(port);

            while(running) {
                //Listening for the incoming connections and accepting them on a separate thread.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            session();
                        } catch (Exception e) {
                            Logger.getGlobal().info("Connection error: " + e.toString());
                        }
                    }
                }).start();
            }
        }
        finally {
            //Server stopped.
            running = false;
        }
    }

    /**
     * A single connection session to the server.
     */
    private void session() throws Exception {
        //Listen and accept the connection.
        Logger.getGlobal().info("Listening for connections on: " + port);
        Socket clientSocket = serverSocket.accept();

    }

    /**
     * Stops the server.
     */
    public void stop() {
        running = false;
    }
}
