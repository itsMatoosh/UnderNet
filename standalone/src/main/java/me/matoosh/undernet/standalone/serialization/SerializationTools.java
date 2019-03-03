package me.matoosh.undernet.standalone.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Toolkit for serializing objects.
 */
public class SerializationTools {

    public static Logger logger = LoggerFactory.getLogger(SerializationTools.class);
    /**
     * Writes the specified object to the specified path.
     * @param saveFile
     */
    public static void writeObjectToFile(Object obj, File saveFile) {
        //Checking if the file exists.
        if(saveFile.exists()) {
            saveFile.delete();
        }

        //Writing cache.
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(obj);
            oos.close();
        } catch (Exception e) {
            logger.error("Error when serializing object!", e);
        }
    }

    /**
     * Reads object from the specified file.
     * @param file
     */
    public static Object readObjectFromFile(File file) {
        //Checking if the cache exists.
        if(!file.exists()) {
            return null;
        }

        //Reading the cache.
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            ois.close();
            return object;
        } catch (Exception e) {
            logger.error("Error while reading object from " + file, e);
        }
        return null;
    }
}
