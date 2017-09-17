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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.file.StandaloneFileManager;
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

    public static void main (String[] args) {
        //Setting up the environment.
        setup();

        //Starting the ui.
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainAppFrame = new AppFrame();
                mainAppFrame.setVisible(true);
            }
        });
    }

    /**
     * Sets up the standalone environment.
     */
    private static void setup() {
        //Checking whether the config files exist.
        StandaloneConfigManager.checkConfigs();

        //Getting the file configuration source.
        //Specify which files to load. Configuration from both files will be merged.
        ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
            @Override
            public Iterable<Path> getConfigFiles() {
                return Arrays.asList(Paths.get("./network.yaml"), Paths.get("./standalone.yaml"));
            }
        };

        // Use local files as configuration store
        ConfigurationSource source = new FilesConfigurationSource(configFilesProvider);

        // (optional) Select path to use
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
}
