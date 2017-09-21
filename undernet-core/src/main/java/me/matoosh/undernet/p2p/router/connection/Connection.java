package me.matoosh.undernet.p2p.router.connection;

import java.io.InputStream;
import java.io.OutputStream;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Represents a connection.
 * Created by Mateusz Rębacz on 22.03.2017.
 */

public abstract class Connection {
    /**
     * The server of this connection.
     * Only set if side == ConnectionSide.Server
     */
    public Server server;
    /**
     * The client of this connection.
     * Only set if side == ConnectionSide.Client
     */
    public Client client;
    /**
     * The information of the node on the other side.
     */
    public Node other;
    /**
     * The side of the connection.
     */
    public ConnectionSide side;
    /**
     * The id of this connection.
     */
    public int id;
    /**
     * Whether the connection is active.
     */
    public boolean active = false;

    /**
     * Input stream of this connection.
     * Assigned when the connection is established.
     */
    public InputStream inputStream;
    /**
     * Output stream of this connection.
     * Assigned when the connection is established.
     */
    public OutputStream outputStream;


    /**
     * Establishes the connection with the specified node on a new thread.
     * Needs to call runSession() AND onConnectionEstablished event.
     * @param client the client establishing the connection.
     * @param other the node to connect to.
     */
    public void establish(Client client, Node other) {
        this.side = ConnectionSide.CLIENT;
        this.client = client;
        this.other = other;

        onEstablishingConnection();
    }

    /**
     * Handles the incoming connection on a new thread.
     * Needs to call runSession() AND onConnectionEstablished event.
     * @param server the server receiving the connection.
     * @param other the node connecting.
     */
    public void accept(Server server, Node other) {
        this.side = ConnectionSide.SERVER;
        this.server = server;
        this.other = other;

        onAcceptingConnection();
    }

    /**
     * Drops the connection.
     * Interrupts the connection loop thread.
     */
    public void drop() {
        Router.logger.info("Dropping " + side + " connection. ID: " + id);

        //Interrupting the connection threads.
        active = false;

        //Running the connection drop logic.
        onConnectionDropped();

        //Setting all the variables to null.
        inputStream = null;
        outputStream = null;


        //EventManager.callEvent(new ChannelClosedEvent(this, other));
    }

    /**
     * Called when the connecton is being established.
     */
    protected abstract void onEstablishingConnection();
    /**
     * Called when an incoming connection is being accepted.
     */
    protected abstract void onAcceptingConnection();
    /**
     * Called when a connection error occurs.
     */
    public abstract void onConnectionError(ConnectionException e);
    /**
     * Called when the connection is dropped.
     */
    public abstract void onConnectionDropped();

    /**
     * Receives a single chunk of data.
     * @throws ConnectionSessionException
     */
    public abstract void receive() throws ConnectionSessionException;

    /**
     * Sends a single chunk of data.
     * @throws ConnectionSessionException
     */
    public abstract void send() throws ConnectionSessionException;
}
