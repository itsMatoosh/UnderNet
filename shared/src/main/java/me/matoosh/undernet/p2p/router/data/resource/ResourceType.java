package me.matoosh.undernet.p2p.router.data.resource;

/**
 * Different types of resources.
 */
public enum ResourceType {
    FILE((byte)0),
    RM_FILE_FLAG((byte)1),
    UNKNOWN((byte)-1);

    /**
     * The type value of the resource.
     */
    private byte value;

    ResourceType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }

    /**
     * Gets a message type given its id.
     * @param value
     * @return
     */
    public static ResourceType getByValue(short value) {
        for(ResourceType e : values()) {
            if(e.value == value) return e;
        }
        return UNKNOWN;
    }
}
