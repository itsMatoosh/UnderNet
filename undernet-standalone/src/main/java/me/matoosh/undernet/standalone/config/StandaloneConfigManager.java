package me.matoosh.undernet.standalone.config;

import java.io.File;
import java.io.IOException;

import me.matoosh.undernet.file.StandaloneFileManager;

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
     * Checks whether the the configs for the app are present in the working directory.
     * Creates the files if they're absent.
     */
    public static void checkConfigs() {
        //Caching the paths.
        StandaloneFileManager tmpFileMgr = new StandaloneFileManager();
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
        try {
            networkConfig.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a copy of the default platform specific config in the working directory.
     */
    private static void createStandaloneConfig() {
        try {
            standaloneConfig.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
