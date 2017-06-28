package me.matoosh.undernet.p2p.router.connection;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.KnownNode;
import me.matoosh.undernet.p2p.node.Node;

import static me.matoosh.undernet.p2p.node.Node.self;

/**
 * Represents a connection over Internet.
 * Created by Mateusz Rębacz on 26.06.2017.
 */

public class NetworkConnection extends Connection {

    /**
     * The local socket.
     */
    public Socket selfSocket;

    /**
     * Establishes the connection with the specified node.
     * Needs to call runSession().
     *
     * @param other the node to connect to.
     * @return whether the connection was established correctly.
     */
    @Override
    public void establish(Node other) {
        //Starting the connection thread.
        Thread connectionThread = new Thread(() -> {
            //Connecting to the node.
            try {
                UnderNet.logger.info("Connecting to: " + other.address);
                selfSocket.connect(new InetSocketAddress(other.address, new Random().nextInt(49151)));
                UnderNet.logger.info("Connected to: " + other.address);
                inputStream = selfSocket.getInputStream();
                outputStream = selfSocket.getOutputStream();

                //TODO: Start the connection session.
            } catch (Exception e) {
                //Debugging
                if(other.getClass() == KnownNode.class){
                    UnderNet.logger.error("Error while connecting to node: " + ((KnownNode) other).username + " - " + other.address + " over Internet.");
                } else {
                    UnderNet.logger.error("Error while connecting to node: " + other.address + " by Internet.");
                }
                e.printStackTrace();
                UnderNet.logger.info("Connection error: " + e.toString());
            }
        });
    }

    /**
     * Handles the received connection.
     * Needs to call runSession().
     *
     * @param other
     * @throws Exception
     */
    @Override
    public void receive(Node other) {
        //Listen and accept the connection.
        UnderNet.logger.info("Listening for connections on: " + self.server.port);
        Socket clientSocket = self.server.serverSocket.accept();
        UnderNet.logger.info("Accepted connection from: " + clientSocket.getInetAddress());
        inputStream = clientSocket.getInputStream();
        outputStream = clientSocket.getOutputStream();
    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    protected void onConnectionDropped() {

    }

    /**
     * A single connection session.
     */
    @Override
    protected void session() throws Exception {

    }
}
