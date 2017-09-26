package me.matoosh.undernet;

import org.cfg4j.provider.ConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.matoosh.undernet.file.FileManager;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.config.NetworkConfig;
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
     * Currently used config provider.
     */
    private static ConfigurationProvider configProvider;
    /**
     * The current network config of the app.
     * Is bound with the config system.
     */
    public static NetworkConfig networkConfig;

    /**
     * The currently used router.
     */
    public static Router router;

    /**
     * Sets up UnderNet.
     */
    public static void setup(FileManager fileManager, ConfigurationProvider configProvider) {
        //Writing the init message.
        writeInitMessage();

        //Setting the file manager.
        UnderNet.fileManager = fileManager;

        //Setting the config provider.
        UnderNet.configProvider = configProvider;
        networkConfig = configProvider.bind("network", NetworkConfig.class);

        //Loading up the node cache.
        EntryNodeCache.registerEvents();
        EntryNodeCache.load();

        //Setting up the self node.
        Node.self = new Node();

        //Setting up the router.
        router = new Router();
        router.setup();
    }

    /**
     * Connects to one of the known nodes.
     */
    public static void connect(NetworkIdentity identity) {
        //Connecting the client to the network.
        logger.info("Connecting to UnderNet as " + identity.getUsername() + "...");

        //Starting the router.
        Node.self.router.start(identity);
    }

    /**
     * Disconnects from the network.
     */
    public static void disconnect() {
        //Disconnecting from the network.
        logger.info("Disconnecting from UnderNet...");
        Node.self.router.stop();
    }

    /// <summary>
    /// Writes the initial message to the console.
    /// </summary>
    private static void writeInitMessage()
    {
        silentLogger.info("");
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
