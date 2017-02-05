package me.matoosh.undernet.p2p.router;

import com.jcabi.aspects.Async;

import java.net.ServerSocket;
import java.net.Socket;

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
        try {
            serverSocket = new ServerSocket(port);

            while(running) {
                //Listening for the incoming connections and accepting them on a separate thread.

                session();
            }
        } finally {
            running = false;
        }
    }

    /**
     * A single connection session to the server.
     */
    @Async
    private void session() {
        LOG
    }

    /**
     * Stops the server.
     */
    public void stop() {

    }
}
