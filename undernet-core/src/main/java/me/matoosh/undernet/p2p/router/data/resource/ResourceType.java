package me.matoosh.undernet.p2p.router.data.resource;

/**
 * Type of a resource on the network.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public enum ResourceType {
    FILE, //A file
    FLAG //A temporary flag containing information (is not stored on disk). Can be used as a middle man indicator.
}
