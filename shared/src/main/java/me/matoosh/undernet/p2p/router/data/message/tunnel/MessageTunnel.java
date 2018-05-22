package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

import java.security.*;

/**
 * Represents a message tunnel.
 * A message tunnel contains information about the recipient node and its public key.
 * The public key will be used to encrypt the messages so that only the recipient node can decrypt them.
 */
public class MessageTunnel {
    //This part is present on all the part taking nodes.
    /**
     * The network id of the origin node.
     */
    private NetworkID origin;
    /**
     * The network id of the recipient.
     */
    private NetworkID destination;

    /**
     * The next neighboring node to the destination.
     */
    private Node nextNode;
    /**
     * The next neighboring node to the origin.
     */
    private Node previousNode;

    //This part is present only on the receiving ends of the tunnel.
    /**
     * The public key of the recipient.
     * Used by self to encrypt messages for the other side of the tunnel.
     */
    private PublicKey recipientPublicKey;

    /**
     * The self public key.
     * Used by the other side of the tunnel to encrypt the messages.
     */
    private PublicKey selfPublicKey;
    /**
     * The self private key.
     * Used by self to decrypt the messages encrypted by the other side of the tunnel.
     */
    private PrivateKey selfPrivateKey;

    /**
     * Creates a message tunnel.
     * @param destination
     * @param origin
     */
    public MessageTunnel(NetworkID origin, NetworkID destination) {
        this.destination = destination;
        this.origin = origin;
    }

    /**
     * Generates a private/public key pair for this tunnel.
     */
    public void generateSelfKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(NetworkID.NETWORK_ID_LENGTH *8 - 30*8);
        KeyPair generatedKeyPair = keyGen.genKeyPair();
        this.selfPublicKey = generatedKeyPair.getPublic();
        this.selfPrivateKey = generatedKeyPair.getPrivate();
    }
    public PublicKey getSelfPublicKey() {
        return selfPublicKey;
    }
    public PrivateKey getSelfPrivateKey() {
        return selfPrivateKey;
    }
    public PublicKey getRecipientPublicKey() {
        return recipientPublicKey;
    }
    public void setRecipientPublicKey(PublicKey publicKey) {
        this.recipientPublicKey = publicKey;
    }

    /**
     * Gets the recipient.
     * @return
     */
    public NetworkID getDestination() {
        return this.destination;
    }
    /**
     * Gets the origin.
     * @return
     */
    public NetworkID getOrigin() {return this.origin;}

    /**
     * Gets the previous node.
     * @return
     */
    public Node getPreviousNode() {return this.previousNode; }
    /**
     * Gets the next node.
     * @return
     */
    public Node getNextNode() {return this.nextNode; }
    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }
    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }
}
