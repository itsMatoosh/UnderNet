package me.matoosh.undernet.p2p.router.data.resource;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Information about a resource.
 */
public class ResourceInfo implements Serializable {
    /**
     * The type of the resource.
     */
    public ResourceType resourceType;

    /**
     * The max size for resource attributes.
     */
    public static final int MAX_ATTRIBUTES_SIZE = 5;

    /**
     * Custom attributes of the resource.
     * MAX 5
     */
    public HashMap<Integer, String> attributes;

    /**
     * Constructs a resource info.
     * @param resource
     */
    public ResourceInfo(Resource resource) {
        this.resourceType = resource.getResourceType();
        if(resource.attributes.size() > MAX_ATTRIBUTES_SIZE) {
            ResourceManager.logger.warn("Too many attributes for resource: {}", resource.getNetworkID());
        } else {
            this.attributes = resource.attributes;
        }
    }
}
