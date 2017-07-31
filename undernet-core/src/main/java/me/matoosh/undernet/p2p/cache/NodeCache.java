package me.matoosh.undernet.p2p.cache;

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
import me.matoosh.undernet.p2p.node.Node;

/**
 * Cached nodes storage.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class NodeCache {
    /**
     * All the loaded cached nodes.
     */
    public static ArrayList<Node> cachedNodes;

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
    public static void addNode(me.matoosh.undernet.p2p.node.Node node) {
        cachedNodes.add(node);
        save();
        UnderNet.logger.info("Added node " + node.address + " to cache");
    }

    /**
     * Loading the node cache from disk.
     */
    public static void load() {
        //Checking whether the file exists.
        File nodesCacheFile = new File(UnderNet.fileManager.getAppFolder() + "/nodesCache.unc");
        try {
            UnderNet.logger.info("Loading the node cache from: " + nodesCacheFile.toString());
            FileInputStream fileIn = new FileInputStream(nodesCacheFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            cachedNodes = (ArrayList<Node>) in.readObject();
            //Log.i("palval", "dir.exists()");
            in.close();
            fileIn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            UnderNet.logger.warn("Node cache file not found, creating a new one...");
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
            FileOutputStream fileOut = new FileOutputStream(UnderNet.fileManager.getAppFolder() + "/nodesCache.uni");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(cachedNodes);
            out.close();
            fileOut.close();
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
    }
}
