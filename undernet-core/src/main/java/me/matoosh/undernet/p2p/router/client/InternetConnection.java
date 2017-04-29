package me.matoosh.undernet.p2p.router.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.ConnectionException;
import me.matoosh.undernet.p2p.router.connection.ConnectionSide;
import me.matoosh.undernet.p2p.router.connection.SessionException;

/**
 * Connection made over the Internet.
 * Created by Mateusz Rębacz on 20.02.2017.
 */

public class InternetConnection extends Connection {

    /**
     * Default constructor
     * @param self
     * @param other
     * @param thread
     * @param connectionSide
     * @throws Exception
     */
    public InternetConnection(Node self, Node other, Thread thread, ConnectionSide connectionSide) throws Exception {
        super(self, other, thread, connectionSide);
    }

    @Override
    public void init() throws ConnectionException, IOException {
        if(connectionSide == ConnectionSide.CLIENT) {
            //Connecting to the node.
            UnderNet.logger.info("Connecting to: " + other.address);
            self.client.clientSocket.connect(new InetSocketAddress(other.address, new Random().nextInt(49151)));
            UnderNet.logger.info("Connected to: " + other.address);
            inputStream = self.client.clientSocket.getInputStream();
            outputStream = self.client.clientSocket.getOutputStream();

        } else if(connectionSide == ConnectionSide.SERVER) {
            //Listen and accept the connection.
            UnderNet.logger.info("Listening for connections on: " + self.server.port);
            Socket clientSocket = self.server.serverSocket.accept();
            UnderNet.logger.info("Accepted connection from: " + clientSocket.getInetAddress());
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
        }
    }

    /**
     * A single connection session of the client.
     */
    @Override
    public void clientSession() throws SessionException {

    }

    /**
     * A single connection session of the server.
     *
     * @throws SessionException
     */
    @Override
    public void serverSession() throws SessionException {

    }


    @Override
    public void drop() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}
