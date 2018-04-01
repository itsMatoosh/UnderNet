package me.matoosh.undernet.file;

import java.io.File;

/**
 * Used to manage files in the app.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public abstract class FileManager {
    /**
     * Returns the main app folder.
     * @return the main app folder.
     */
    public abstract File getAppFolder();

    /**
     * Gets the folder where the network content is stored.
     * @return the content folder of the app.
     */
    public abstract File getContentFolder();

    /**
     * Gets the cache folder of the app.
     * @return the cache folder of the app.
     */
    public abstract File getCacheFolder();
}
