package me.matoosh.undernet.p2p.cache;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.cache.*;
import me.matoosh.undernet.p2p.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;

/**
 * Cached nodes storage.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class EntryNodeCache {
    /**
     * All the loaded cached entry nodes.
     */
    public static ArrayList<Node> cachedNodes;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(EntryNodeCache.class);

    /**
     * Returns a specific number of the most reliable nodes.
     * @param amount
     * @return
     */
    public static ArrayList<Node> getMostReliable(int amount, ArrayList<Node> exclude) {
        //Skipping if there are no cached nodes.
        if(cachedNodes == null || cachedNodes.size() == 0) {
            return null;
        }

        ArrayList<Node> resultList = (ArrayList<Node>) cachedNodes.clone();

        //Excluding nodes.
        if(exclude != null) {
            resultList.removeAll(exclude);
        }

        //Adjusting the amount.
        if(amount > resultList.size()) {
            amount = resultList.size();
        }

        //Removing nodes with lowest reliability until reached the amount.
        for(int i = 0; i < amount; i++) {
            if(amount == resultList.size()) break;

            //Getting the least reliable node in the remaining set.
            Node lowestRel = resultList.get(0);
            /*for(Node node : resultList) {
                if(node.reliability < lowestRel.reliability) {
                    lowestRel = node;
                }
            }*/

            resultList.remove(lowestRel);
        }

        return resultList;
    }

    /**
     * Adds a node to the node cache.
     * @param node
     */
    public static void addNode(Node node) {
        cachedNodes.add(node);
        save();
        EventManager.callEvent(new NodeCacheAddedEvent(node));
    }

    /**
     * Removes a node from the cache.
     * @param node
     */
    public static void removeNode(Node node) {
        cachedNodes.remove(node);
        save();
        EventManager.callEvent(new NodeCacheRemovedEvent(node));
    }

    /**
     * Registering the node cache events.
     */
    public static void registerEvents() {
        EventManager.registerEvent(NodeCacheAddedEvent.class);
        EventManager.registerEvent(NodeCacheRemovedEvent.class);
        EventManager.registerEvent(NodeCacheLoadEvent.class);
        EventManager.registerEvent(NodeCacheSaveEvent.class);
        EventManager.registerEvent(NodeCacheClearEvent.class);
    }
    /**
     * Loading the node cache from disk.
     */
    public static void load() {
        //Checking whether the file exists.
        File nodesCacheFile = new File(UnderNet.fileManager.getCacheFolder() + "/entry.nodes");
        logger.info("Loading entry node cache from: " + nodesCacheFile.getAbsolutePath());
        try {
            logger.info("Loading the node cache from: " + nodesCacheFile.toString());
            FileInputStream fileIn = new FileInputStream(nodesCacheFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            cachedNodes = (ArrayList<Node>) in.readObject();
            in.close();
            fileIn.close();
            EventManager.callEvent(new NodeCacheLoadEvent());
        } catch (InvalidClassException e) {
            logger.error("Entry node cache, contains old or incompatible data, resetting...");
            clear();
        } catch (ClassNotFoundException e) {
            logger.error("Couldn't deserialize the entry node cache file!", e);
            clear();
        } catch (FileNotFoundException e) {
            logger.error("Entry node cache file not found!");
            clear();
        } catch (EOFException e) {
            logger.error("Entry node cache corrupted, resetting...", e);
            clear();
        } catch (IOException e) {
            logger.error("Exception occured while loading the entry node cache file!",e);
        }
    }

    /**
     * Saves the entry node cache to disk.
     */
    public static void save() {
        File saveFile = new File(UnderNet.fileManager.getCacheFolder() + "/entry.nodes");
        try {
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            logger.info("Saving the entry node cache to: " + saveFile.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(cachedNodes);
            out.close();
            fileOut.close();
            EventManager.callEvent(new NodeCacheSaveEvent());
        } catch (FileNotFoundException e) {
            logger.error("The entry cache file has been deleted! Creating a new one...", e);
            try {
                saveFile.createNewFile();
            } catch (IOException e1) {
                logger.error("Error creating the entry node cache file!", e1);
            }
            save();
        } catch (IOException e) {
            logger.error("An error occured while writing the entry node cache!",e);
        }
    }

    /**
     * Clears the entry node cache.
     */
    public static void clear() {
        logger.info("Resetting the entry node cache...");
        File cacheFile = new File(UnderNet.fileManager.getCacheFolder() + "/entry.nodes");
        cacheFile.delete();
        try {
            cacheFile.createNewFile();
        } catch (IOException e) {
            logger.error("Error creating the entry node cache file!", e);
        }
        if(cachedNodes != null) {
            cachedNodes.clear();
        }
        cachedNodes = new ArrayList<>();
        save();
        EventManager.callEvent(new NodeCacheClearEvent());
    }
}
