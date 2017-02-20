package me.matoosh.undernet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.matoosh.undernet.camera.CamHost;
import me.matoosh.undernet.ui.view.ViewManager;

/**
 * The main activity of the app.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Reference to the main activity instance.
     */
    public static MainActivity instance;
    /**
     * The ViewManager.
     */
    public static ViewManager viewManager;
    public static Logger logger = LoggerFactory.getLogger("undernet.android");

    //PERMISSIONS
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Super create.
        super.onCreate(savedInstanceState);
        //Setting the instance.
        instance = this;
        //Setting the view to the activity.
        setContentView(R.layout.activity_main);

        //Initializing the Core library.
        UnderNet.setup();

        //Initializing all the components.
        init();
    }

    /**
     * Initializes all of the components.
     */
    protected void init() {
        //Initializing the ViewManager.
        viewManager = new ViewManager();
        viewManager.init();
        //Initializing the CamHost.
        CamHost.init();
    }
}
