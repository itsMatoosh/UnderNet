package me.matoosh.undernet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import layout.section.SectionType;
import layout.section.commmunities.CommunitiesSection;
import layout.section.main.MainSection;
import me.matoosh.undernet.camera.CamHost;
import me.matoosh.undernet.file.AndroidFileManager;

/**
 * The main activity of the app.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Reference to the main activity instance.
     */
    public static MainActivity instance;
    /**
     * Main section of the app.
     */
    public MainSection mainSection;
    /**
     * Communities section of the app.
     */
    public CommunitiesSection communitiesSection;
    /**
     * Currently open section.
     */
    public SectionType currentSection = SectionType.MAIN;
    /**
     * Whether the app is currently transitioning between sections.
     */
    public boolean isTransitioning = false;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(MainActivity.class);

    //PERMISSIONS
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TEST", "Launching the app.");

        //Super create.
        super.onCreate(savedInstanceState);
        //Setting the instance.
        instance = this;
        //Setting the view to the activity.
        setContentView(R.layout.activity_main);

        //Initializing all the components.
        init();
    }

    /**
     * Initializes all of the components.
     */
    protected void init() {
        //Initializing the Core library.
        UnderNet.setup(new AndroidFileManager());

        //Initializing the CamHost.
        CamHost.init();
    }
}
