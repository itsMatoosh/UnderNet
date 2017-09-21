package me.matoosh.undernet.p2p.router.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.bytestream.ChannelBytestreamReceivedEvent;
import me.matoosh.undernet.p2p.router.data.messages.MessageBase;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessageSerializer;

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
     * The remote socket.
     * Used only in server mode.
     */
    public Socket clientSocket;
    /**
     * The logger of the class.
     */
    public Logger logger = LoggerFactory.getLogger(NetworkConnection.class);

    /**
     * Called when the connecton is being established.
     */
    @Override
    protected void onEstablishingConnection() {
        //Setting the connection establishing thread.
        Thread establishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Connecting to the node.
                try {
                    logger.info("Connecting to: " + other.address);
                    connectionSocket = new Socket();

                    connectionSocket.connect(new InetSocketAddress(other.address, other.port));
                } catch (IOException e) {
                    e.printStackTrace();
                    onConnectionError(new ConnectionIOException(NetworkConnection.this, ConnectionThreadType.ESTABLISH));
                    return;
                }
                try {
                    logger.info("Connected to: " + other.address);
                    inputStream = connectionSocket.getInputStream();
                    outputStream = connectionSocket.getOutputStream();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    onConnectionError(new ConnectionIOException(NetworkConnection.this, ConnectionThreadType.ESTABLISH));
                    return;
                }

                //Setting the so timeout.
                try {
                    connectionSocket.setSoTimeout(0);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                //Initiating connection.
                try {
                    handshake();
                } catch (IOException e) {
                    e.printStackTrace();
                    onConnectionError(new ConnectionHandshakeException(NetworkConnection.this, ConnectionThreadType.ESTABLISH));
                    return;
                } catch (ConnectionHandshakeException e) {
                    e.printStackTrace();
                    onConnectionError(e);
                    return;
                }

                //Setting the so timeout.
                try {
                    connectionSocket.setSoTimeout(client.router.networkTickTime/1000 - 1);
                } catch (SocketException e) {
                    e.printStackTrace();
                }

                //Calling the connection established event.
                EventManager.callEvent(new ChannelCreatedEvent(NetworkConnection.this, other));
            }
        });
        establishThread.start();
    }

    /**
     * Called when the connection is being received.
     */
    @Override
    protected void onAcceptingConnection() {
        //Accepting the server connection.
        try {
            UnderNet.logger.info("Listening for connections on: " + server.networkListener.listenSocket.getLocalPort());
            clientSocket = server.networkListener.listenSocket.accept();
            other.setAddress(clientSocket.getInetAddress());
            EventManager.callEvent(new ChannelAcceptedEvent(this));
        } catch (IOException e) {
            e.printStackTrace();
            onConnectionError(new ConnectionIOException(this, ConnectionThreadType.ESTABLISH));
            return;
        }
        try {
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
            onConnectionError(new ConnectionIOException(this, ConnectionThreadType.ESTABLISH));
            return;
        }

        //Setting the so timeout.
        try {
            clientSocket.setSoTimeout(0);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Handshake.
        try {
            //Initiating connection.
            handshake();
        } catch (IOException e) {
            e.printStackTrace();
            onConnectionError(new ConnectionHandshakeException(this, ConnectionThreadType.ESTABLISH));
            return;
        } catch (ConnectionHandshakeException e) {
            e.printStackTrace();
            onConnectionError(e);
            return;
        }

        //Setting the so timeout.
        try {
            clientSocket.setSoTimeout(server.router.networkTickTime/1000 - 1);
        } catch (SocketException e) {
            e.printStackTrace();
        }

        //Calling the connection established event.
        EventManager.callEvent(new ChannelCreatedEvent(this, other));
    }

    /**
     * Initiates the handshake process.
     */
    private void handshake() throws IOException, ConnectionHandshakeException {
        switch (side) {
            case CLIENT:
                //Reading the server stream.
                if(inputStream.read() != 1) {
                    throw new ConnectionHandshakeException(this, ConnectionThreadType.SEND);
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
        logger.error("An error occured with the connection: " + e.connection.id + " on thread " + e.connectionThreadType);
        //TODO: Recovery procedure.
    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    public void onConnectionDropped() {
        //Closing the socket.
        switch (side) {
            case CLIENT:
                try {
                    connectionSocket.shutdownInput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case SERVER:
                try {
                    clientSocket.shutdownInput();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }

        //Closing the streams.
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receiving logic.
     *
     * @throws ConnectionSessionException
     */
    @Override
    public void receive() throws ConnectionSessionException {
        try {
            //TODO: Use cached message info.

            //Checking if the stream has at least 2 bytes available (message Id and lenght ints).
            if(inputStream.available() > 1) {
                //Reading the message id from the stream.
                inputStream.read(); //Checking if any data is available for reading.
                int messageId = inputStream.read();

                //Reads the message length from the stream.
                int messageLength = inputStream.read();
                if (messageLength <= 0) {
                    this.drop();
                    return;
                }

                byte[] messagePayload = new byte[messageLength];
                inputStream.read(messagePayload, 0, messageLength);

                //Checking if the received byte is a message.
                if(messageId > 0) {
                    //Message
                    //Deserializing.
                    MessageBase deserialisedMessage = NetworkMessageSerializer.read(messageId, messagePayload);
                    //EventManager.callEvent(new ChannelMessageReceivedEvent(this, deserialisedMessage));
                } else if (messageId == -1) {
                    //Skipping
                } else {
                    //Byte stream
                    EventManager.callEvent(new ChannelBytestreamReceivedEvent(this, messagePayload));
                }
            }
        } catch (SocketTimeoutException timeout) {
            return;
        } catch(EOFException eofException){
            eofException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            this.drop();
            return;
        }
    }


    /**
     * Sending logic.
     *
     * @throws ConnectionSessionException
     */
    @Override
    public void send() throws ConnectionSessionException {

    }
}
