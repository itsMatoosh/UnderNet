package me.matoosh.undernet.p2p.cache;

import java.util.ArrayList;

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
        ArrayList<Node> resultList = cachedNodes;

        //Excluding nodes.
        if(exclude != null) {
            resultList.removeAll(exclude);
        }

        //Removing nodes with lowest reliability until reached the amount.
        for(int i = 0; i < amount; i++) {
            //Getting the most reliable node in the remaining set.
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
        //TODO: ADD NODE LOGIC

        save();
    }

    /**
     * Loading the node cache from disk.
     */
    public static void load() {

    }

    /**
     * Saves the node cache to disk.
     */
    public static void save() {

    }
}
