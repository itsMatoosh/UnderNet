package me.matoosh.undernet.p2p.router.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import me.matoosh.undernet.UnderNet;

/**
 * Represents a connection over Internet.
 * Created by Mateusz Rębacz on 26.06.2017.
 */

public class NetworkConnection extends Connection {

    /**
     * The local socket.
     * Used only in client mode.
     */
    public Socket connectionSocket;

    /**
     * Called when the connecton is being established.
     */
    @Override
    protected void onEstablishingConnection() {
        //Starting the connection thread.
        Thread connectionThread = new Thread(() -> {
            //Connecting to the node.
            try {
                UnderNet.logger.info("Connecting to: " + other.address);
                connectionSocket.connect(new InetSocketAddress(other.address, new Random().nextInt(49151)));
            } catch (Exception e) {
                //ConnectionErrorEvent
                onConnectionError((ConnectionException)e);
            }
            try {
                UnderNet.logger.info("Connected to: " + other.address);
                inputStream = connectionSocket.getInputStream();
                outputStream = connectionSocket.getOutputStream();
            }
            catch (IOException e) {
                e.printStackTrace();
                onConnectionError(new ConnectionIOException(this));
            }

            //Starting the connection session.
            runSession();
        });
    }

    /**
     * Called when the connection is being received.
     */
    @Override
    protected void onReceivingConnection() {
        //Accepting the server connection.
        try {
            UnderNet.logger.info("Listening for connections on: " + server.networkListener.port);
            connectionSocket = server.networkListener.listenSocket.accept();
            UnderNet.logger.info("Accepted connection from: " + connectionSocket.getInetAddress());
        } catch (Exception e) {
            //ConnectionErrorEvent
            onConnectionError((ConnectionException)e);
        }
        try {
            inputStream = connectionSocket.getInputStream();
            outputStream = connectionSocket.getOutputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
            onConnectionError(new ConnectionIOException(this));
        }

        //Starting the session.
        runSession();
    }

    /**
     * Called when a connection error occurs.
     *
     * @param e
     */
    @Override
    public void onConnectionError(ConnectionException e) {
        //TODO
    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    public void onConnectionDropped() {
        //Closing the socket.
        try {
            connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * A single connection session.
     */
    @Override
    protected void session() {
        if(side == ConnectionSide.CLIENT) {
            //Client
        } else {
            //Server
        }
    }
}
