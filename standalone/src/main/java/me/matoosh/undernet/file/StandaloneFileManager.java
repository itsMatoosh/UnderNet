package me.matoosh.undernet.file;

import java.io.File;

/**
 * File manager for the standalone version.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class StandaloneFileManager extends FileManager {
    /**
     * Returns the main app folder.
     *
     * @return the main app folder.
     */
    @Override
    public File getAppFolder() {
        return new File(System.getProperty("user.dir"));
    }

    /**
     * Gets the folder where the network content is stored.
     *
     * @return the content folder of the app.
     */
    @Override
    public File getContentFolder() {
        File content = new File(getAppFolder() + "/content");
        if(!content.exists()) {
            content.mkdir();
        }
        return content;
    }

    /**
     * Gets the cache folder of the app.
     *
     * @return the cache folder of the app.
     */
    @Override
    public File getCacheFolder() {
        File cache = new File(getAppFolder() + "/cache");
        if(!cache.exists()) {
            cache.mkdir();
        }
        return cache;
    }
}
