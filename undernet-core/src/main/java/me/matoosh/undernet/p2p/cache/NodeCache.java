package me.matoosh.undernet.p2p.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.cache.NodeCacheAddedEvent;
import me.matoosh.undernet.event.cache.NodeCacheClearEvent;
import me.matoosh.undernet.event.cache.NodeCacheLoadEvent;
import me.matoosh.undernet.event.cache.NodeCacheRemovedEvent;
import me.matoosh.undernet.event.cache.NodeCacheSaveEvent;
import me.matoosh.undernet.p2p.node.Node;

/**
 * Cached nodes storage.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class NodeCache {
    /**
     * All the loaded cached entry nodes.
     */
    public static ArrayList<Node> cachedNodes;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NodeCache.class);

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
            for(Node node : resultList) {
                if(node.reliability < lowestRel.reliability) {
                    lowestRel = node;
                }
            }

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
        File nodesCacheFile = new File(UnderNet.fileManager.getAppFolder() + "/known.nodes");
        logger.info("Loading the node cache from: " + nodesCacheFile.getAbsolutePath());
        try {
            logger.info("Loading the node cache from: " + nodesCacheFile.toString());
            FileInputStream fileIn = new FileInputStream(nodesCacheFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            cachedNodes = (ArrayList<Node>) in.readObject();
            in.close();
            fileIn.close();
            EventManager.callEvent(new NodeCacheLoadEvent());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            logger.warn("Node cache file not found, creating a new one...");
            try {
                cachedNodes = new ArrayList<Node>();
                nodesCacheFile.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (EOFException e) {
            cachedNodes = new ArrayList<Node>();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the node cache to disk.
     */
    public static void save() {
        try {
            File saveFile = new File(UnderNet.fileManager.getAppFolder() + "/known.nodes");
            FileOutputStream fileOut = new FileOutputStream(saveFile);
            logger.info("Saving the node cache to: " + saveFile.getAbsolutePath());
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(cachedNodes);
            out.close();
            fileOut.close();
            EventManager.callEvent(new NodeCacheSaveEvent());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the node cache.
     */
    public static void clear() {
        cachedNodes.clear();
        save();
        EventManager.callEvent(new NodeCacheClearEvent());
    }
}
