package me.matoosh.undernet.standalone.resource;

import me.matoosh.undernet.file.FileManager;
import me.matoosh.undernet.standalone.UnderNetStandalone;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Manages the resources within the JAR.
 * Created by Mateusz Rębacz on 18.09.2017.
 */

public class ResourceManager {
    /**
     * Export a resource embedded into a Jar file to the local file path.
     *
     * @param resourceName ie.: "/SmartLibrary.dll"
     * @return The path to the exported resource
     * @throws Exception
     */
    static public String exportResource(String resourceName, FileManager fileManager) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        try {
            stream = UnderNetStandalone.class.getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];

            resStreamOut = new FileOutputStream(fileManager.getAppFolder() + resourceName);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }

        return fileManager.getAppFolder() + resourceName;
    }
}
