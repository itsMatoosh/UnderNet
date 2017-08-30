package me.matoosh.undernet.p2p.router.connection;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.connection.ConnectionAcceptedEvent;
import me.matoosh.undernet.event.connection.ConnectionEstablishedEvent;

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
     * The logger of the class.
     */
    public Logger logger;

    /**
     * Called when the connecton is being established.
     */
    @Override
    protected void onEstablishingConnection() {
        //Starting the connection thread.
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
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
                    onConnectionError(new ConnectionIOException(NetworkConnection.this));
                }

                //Initiating connection.
                try {
                    handshake();
                } catch (IOException e) {
                    e.printStackTrace();
                    onConnectionError(new ConnectionHandshakeException(NetworkConnection.this));
                } catch (ConnectionHandshakeException e) {
                    e.printStackTrace();
                    onConnectionError(e);
                }

                //Calling the connection established event.
                EventManager.callEvent(new ConnectionEstablishedEvent(NetworkConnection.this, other));

                //Starting the connection session.
                runSession();
            }
        });
        thread.start();
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
            other.setAddress(connectionSocket.getInetAddress());
            EventManager.callEvent(new ConnectionAcceptedEvent(this));
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

        //Handshake.
        try {
            //Initiating connection.
            handshake();
        } catch (IOException e) {
            e.printStackTrace();
            onConnectionError(new ConnectionHandshakeException(this));
        } catch (ConnectionHandshakeException e) {
            e.printStackTrace();
            onConnectionError(e);
        }

        //Calling the connection established event.
        EventManager.callEvent(new ConnectionEstablishedEvent(this, other));

        //Starting the session.
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runSession();
            }
        });
        thread.run();
    }

    /**
     * Initiates the handshake process.
     */
    private void handshake() throws IOException, ConnectionHandshakeException {
        switch (side) {
            case CLIENT:
                //Reading the server stream.
                if(inputStream.read() != 1) {
                    throw new ConnectionHandshakeException(this);
                }
                break;
            case SERVER:
                //Writing a positive byte to check connection.
                outputStream.write(1);
                break;
        }
    }

    /**
     * Called when a connection error occurs.
     *
     * @param e
     */
    @Override
    public void onConnectionError(ConnectionException e) {
        logger.error("An error occured with the connection: " + e.getMessage());
        //TODO: Recovery procedure.
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
        try {
            if(inputStream.read() == 1) {
                //Message
                switch(side) {
                    case CLIENT:

                        break;
                    case SERVER:

                        break;
                }
            } else {
                //Byte stream
                switch(side) {
                    case CLIENT:

                        break;
                    case SERVER:

                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
