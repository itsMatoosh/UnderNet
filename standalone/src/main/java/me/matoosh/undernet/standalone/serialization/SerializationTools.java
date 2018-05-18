package me.matoosh.undernet.standalone.serialization;

import java.io.*;

/**
 * Toolkit for serializing objects.
 */
public class SerializationTools {
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (FileNotFoundException e) {} catch (IOException e) {} catch (ClassNotFoundException e) {}
        return null;
    }
}
