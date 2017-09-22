package me.matoosh.undernet.standalone;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.file.StandaloneFileManager;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.standalone.config.StandaloneConfigManager;
import me.matoosh.undernet.standalone.ui.AppFrame;

/**
 * A graphical wrapper for the desktop platforms.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class UnderNetStandalone {
    /**
     * The main frame of the app.
     */
    public static AppFrame mainAppFrame;

    /**
     * The network identity to use when connecting.
     */
    public static NetworkIdentity networkIdentity;

    /**
     * Local temp file mgr.
     */
    private static StandaloneFileManager tmpFileMgr;

    public static void main (String[] args) {
        //Setting up the environment.
        setup();

        //Starting the ui.
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainAppFrame = new AppFrame();
                mainAppFrame.setVisible(true);

                //Creating network identity.
                NetworkIdentity identity = readNetworkIdentityCache();
                if(identity == null) {
                    identity = new NetworkIdentity();
                    identity.username = "Anon-" + new Random().nextInt(9) + "" + new Random().nextInt(9) + "" + new Random().nextInt(9);
                }
                UnderNetStandalone.setNetworkIdentity(identity);
            }
        });
    }

    /**
     * Sets up the standalone environment.
     */
    private static void setup() {
        //Creating a temp file manager.
        tmpFileMgr = new StandaloneFileManager();

        //Checking whether the config files exist.
        StandaloneConfigManager.checkConfigs(tmpFileMgr);

        //Getting the file configuration source.
        //Specify which files to load. Configuration from both files will be merged.
        System.out.println(tmpFileMgr.getAppFolder());
        ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
            @Override
            public Iterable<Path> getConfigFiles() {
                return Arrays.asList(Paths.get("network.yaml")/*, Paths.get("standalone.yaml")*/);
            }
        };

        // Use local files as configuration store
        ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);

        // Select path to use
        Environment environment = new ImmutableEnvironment(".");

        // (optional) Reload configuration every 5 seconds
        ReloadStrategy reloadStrategy = new PeriodicalReloadStrategy(5, TimeUnit.SECONDS);

        // Create provider
        ConfigurationProvider configProvider = new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .withEnvironment(environment)
                .withReloadStrategy(reloadStrategy)
                .build();

        //Setting up UnderNet.
        UnderNet.setup(new StandaloneFileManager(), configProvider);
    }

    /**
     * Reads the cached network identity data.
     */
    private static NetworkIdentity readNetworkIdentityCache() {
        //Checking if the cache exists.
        if(!new File(UnderNetStandalone.tmpFileMgr.getCacheFolder() + "/me.identity").exists()) {
            return null;
        }

        //Reading the cache.
        try {
            FileInputStream fis = new FileInputStream(UnderNetStandalone.tmpFileMgr.getCacheFolder() + "/me.identity");
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

    /**
     * Writes the current network identity data to cache.
     */
    private static void writeNetworkIdentityCache() {
        //Checking if the file exists.
        File f = new File(UnderNetStandalone.tmpFileMgr.getCacheFolder() + "/me.identity");
        if(f.exists()) {
            f.delete();
        }

        //Writing cache.
        try {
            FileOutputStream fos = new FileOutputStream(UnderNetStandalone.tmpFileMgr.getCacheFolder() + "/me.identity");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(networkIdentity);
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Sets the active network identity.
     * @param identity
     */
    public static void setNetworkIdentity(NetworkIdentity identity) {
        UnderNetStandalone.networkIdentity = identity;
        mainAppFrame.setTitle("UnderNet - " + UnderNetStandalone.networkIdentity.username);
        writeNetworkIdentityCache();
    }
}
