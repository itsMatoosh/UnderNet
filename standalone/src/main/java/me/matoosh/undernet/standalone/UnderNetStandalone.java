package me.matoosh.undernet.standalone;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.file.StandaloneFileManager;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.standalone.config.StandaloneConfig;
import me.matoosh.undernet.standalone.config.StandaloneConfigManager;
import me.matoosh.undernet.standalone.serialization.SerializationTools;
import me.matoosh.undernet.standalone.ui.AppFrame;
import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.context.environment.Environment;
import org.cfg4j.source.context.environment.ImmutableEnvironment;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.strategy.PeriodicalReloadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
     * The current config of the application.
     */
    public static StandaloneConfig standaloneConfig;

    /**
     * Local temp file mgr.
     */
    private static StandaloneFileManager tmpFileMgr;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(UnderNetStandalone.class);

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
                if(standaloneConfig.identity() == null || standaloneConfig.identity().isEmpty() || standaloneConfig.identity().equals("empty")) {
                    UnderNetStandalone.setNetworkIdentity(null, null);
                } else {
                    File currentIdentityFile = new File(standaloneConfig.identity());
                    try {
                        if (currentIdentityFile != null && currentIdentityFile.exists()) {
                            NetworkIdentity identity = (NetworkIdentity) SerializationTools.readObjectFromFile(currentIdentityFile);
                            UnderNetStandalone.setNetworkIdentity(identity, currentIdentityFile);
                        }
                    }
                    catch (NullPointerException e) {
                        logger.warn("Error reading the identity file!", e);
                        currentIdentityFile.delete();
                    }
                    finally {
                        if(Node.self.getIdentity() == null) {
                            UnderNetStandalone.setNetworkIdentity(null, null);
                        }
                    }
                }
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
                return Arrays.asList(Paths.get("network.yaml"), Paths.get("standalone.yaml"));
            }
        };

        //Use local files as configuration store
        ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);

        //Select path to use
        Environment environment = new ImmutableEnvironment(".");

        //Reload configuration every 5 seconds
        ReloadStrategy reloadStrategy = new PeriodicalReloadStrategy(5, TimeUnit.SECONDS);

        //Create provider
        ConfigurationProvider configProvider = new ConfigurationProviderBuilder()
                .withConfigurationSource(source)
                .withEnvironment(environment)
                .withReloadStrategy(reloadStrategy)
                .build();


        //Getting standalone config.
        standaloneConfig = configProvider.bind("standalone", StandaloneConfig.class);

        //Setting up UnderNet.
        UnderNet.setup(new StandaloneFileManager(), configProvider);
    }

    /**
     * Sets the active network identity.
     * @param identity
     */
    public static void setNetworkIdentity(NetworkIdentity identity, File identityFile) {
        if(identity == null || identityFile == null || !identity.isCorrect()) {
            identity = new NetworkIdentity();
            identityFile = new File(UnderNet.fileManager.getAppFolder() + "/random.id");
            SerializationTools.writeObjectToFile(identity, identityFile);
        }

        UnderNetStandalone.networkIdentity = identity;
        mainAppFrame.setTitle("UnderNet - " + UnderNetStandalone.networkIdentity.getNetworkId().getStringValue());

        //Save the changed identity.
        try {
            File standaloneConfig = new File(UnderNet.fileManager.getAppFolder() + "/standalone.yaml");

            //Deserialize
            YamlReader reader = new YamlReader(new FileReader(standaloneConfig));
            Object object = reader.read();
            Map map = (Map) object;
            Map standalone = (Map) map.get("standalone");
            standalone.put("identity", identityFile.getAbsolutePath());

            standaloneConfig.delete();
            standaloneConfig.createNewFile();

            //Serialize
            YamlWriter writer = new YamlWriter(new FileWriter(standaloneConfig));
            writer.write(map);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (YamlException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
