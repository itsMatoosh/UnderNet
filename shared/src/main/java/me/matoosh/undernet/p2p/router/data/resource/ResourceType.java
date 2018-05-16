package me.matoosh.undernet.p2p.router.data.resource;

/**
 * Different types of resources.
 */
public enum ResourceType {
    FILE((byte)0),
    RM_FILE_FLAG((byte)1);

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
}
