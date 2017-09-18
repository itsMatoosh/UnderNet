package me.matoosh.undernet.standalone.config;

import java.io.File;

import me.matoosh.undernet.file.FileManager;
import me.matoosh.undernet.standalone.resource.ResourceManager;

/**
 * Manages the config files.
 * Created by Mateusz Rębacz on 15.09.2017.
 */

public class StandaloneConfigManager {
    /**
     * The network config file.
     */
    public static File networkConfig;
    /**
     * The platform specific config file.
     */
    public static File standaloneConfig;

    /**
     * The temporary file manager.
     */
    private static FileManager tmpFileMgr;

    /**
     * Checks whether the the configs for the app are present in the working directory.
     * Creates the files if they're absent.
     */
    public static void checkConfigs(FileManager tmpFileMgr) {
        //Caching the file manager.
        StandaloneConfigManager.tmpFileMgr = tmpFileMgr;

        //Caching the paths.
        networkConfig = new File(tmpFileMgr.getAppFolder() + "/network.yaml");
        standaloneConfig = new File(tmpFileMgr.getAppFolder() + "/standalone.yaml");

        if(!networkConfig.exists()) {
            createNetworkConfig();
        }
        if(!standaloneConfig.exists()) {
            createStandaloneConfig();
        }
    }

    /**
     * Creates a copy of the default network config in the working directory.
     */
    private static void createNetworkConfig() {
        //Exporting the default config resource.
        try {
            ResourceManager.ExportResource("/network.yaml", tmpFileMgr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a copy of the default platform specific config in the working directory.
     */
    private static void createStandaloneConfig() {
        //Exporting the default config resource.
        try {
            ResourceManager.ExportResource("/standalone.yaml", tmpFileMgr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
