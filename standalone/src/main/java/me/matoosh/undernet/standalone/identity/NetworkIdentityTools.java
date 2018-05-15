package me.matoosh.undernet.standalone.identity;

import me.matoosh.undernet.identity.NetworkIdentity;

import java.io.*;

/**
 * Tools for managing network identities.
 */
public class NetworkIdentityTools {

    /**
     * Writes the specified identity info to the specified path.
     * @param identity
     * @param saveFile
     */
    public static void writeIdentityToFile(NetworkIdentity identity, File saveFile) {
        //Checking if the file exists.
        if(saveFile.exists()) {
            saveFile.delete();
        }

        //Writing cache.
        try {
            FileOutputStream fos = new FileOutputStream(saveFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(identity);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads identity info from the specified file.
     * @param file
     */
    public static NetworkIdentity readIdentityFromFile(File file) {
        //Checking if the cache exists.
        if(!file.exists()) {
            return null;
        }

        //Reading the cache.
        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            NetworkIdentity identity = (NetworkIdentity) ois.readObject();
            ois.close();
            return identity;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
