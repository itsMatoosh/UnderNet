package me.matoosh.undernet.p2p.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import me.matoosh.undernet.p2p.Node;
import sun.util.resources.cldr.zh.CalendarData_zh_Hans_CN;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;

/**
 * Cached nodes storage.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class NodeCache {
    /**
     * All the loaded cached nodes.
     */
    public static ArrayList<CachedNode> cachedNodes;

    /**
     * Returns a specific number of the most reliable nodes.
     * @param amount
     * @return
     */
    public static ArrayList<CachedNode> getMostReliable(int amount, ArrayList<CachedNode> exclude) {
        ArrayList<CachedNode> resultList = cachedNodes;

        //Excluding nodes.
        if(exclude != null) {
            resultList.removeAll(exclude);
        }

        //Removing nodes with lowest reliability until reached the amount.
        for(int i = 0; i < amount; i++) {
            //Getting the most reliable node in the remaining set.
            CachedNode lowestRel = resultList.get(0);
            for(CachedNode node : resultList) {
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
    public static void addNode(me.matoosh.undernet.p2p.Node node) {
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
