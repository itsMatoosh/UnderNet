package me.matoosh.undernet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.matoosh.undernet.file.FileManager;
import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;

/**
 * Core of the UnderNet package.
 */
public class UnderNet
{
    /**
     * Currently used logger.
     */
    public static Logger logger = LoggerFactory.getLogger("undernet.core");
    /**
     * The silent logger.
     */
    private static Logger silentLogger = LoggerFactory.getLogger("");
    /**
     * File manager of the current platform.
     */
    public static FileManager fileManager;

    /**
     * Sets up UnderNet.
     */
    public static void setup(FileManager fileManager) {
        //Writing the init message.
        writeInitMessage();

        //Setting the file manager.
        UnderNet.fileManager = fileManager;

        //Loading up the node cache.
        NodeCache.registerEvents();
        NodeCache.load();

        //Setting up the self node.
        Node.self = new Node();

        //Setting up the router.
        new Router().setup();
    }

    /**
     * Connects to one of the known nodes.
     */
    public static void connect() {
        //Connecting the client to the network.
        logger.info("Connecting to UnderNet...");

        //Starting the router.
        Node.self.router.start();
    }

    /**
     * Disconnects from the network.
     */
    public static void disconnect() {
        Node.self.router.stop();
    }

    /// <summary>
    /// Writes the initial message to the console.
    /// </summary>
    private static void writeInitMessage()
    {
        silentLogger.info("   xx                 xx  ");
        silentLogger.info("   x:x               x:x  ");
        silentLogger.info("   x::x             x::x  ");
        silentLogger.info("   x:::x           x:::x  ");
        silentLogger.info("   x::::x         x::::x  ");
        silentLogger.info("   x:::::x Under x:::::x  ");
        silentLogger.info("   x:::::x  Net  x:::::x  ");
        silentLogger.info("   x:::::x       x:::::x  ");
        silentLogger.info("   x:::::x       x:::::x  ");
        silentLogger.info("    x:::::x     x:::::x   ");
        silentLogger.info("     x:::::x   x:::::x    ");
        silentLogger.info("      x::::::::::::x      ");
        silentLogger.info("         xxxxxxxxx        ");

        silentLogger.info("");
        silentLogger.info(" Copyright Mateusz Rebacz");
        silentLogger.info("");
        silentLogger.info("");
    }
}
