package me.matoosh.undernet;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import me.matoosh.undernet.camera.CamHost;
import me.matoosh.undernet.ui.view.ViewManager;
import me.matoosh.undernet.ui.view.ViewType;

/**
 * The main activity of the app.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Reference to the main activity instance.
     */
    public static MainActivity instance;

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
        //Initializing all the components.
        init();

        //Changing to the default view.
        ViewManager.setView(ViewType.CAMERA);
    }

    /**
     * Initializes all of the components.
     */
    protected void init() {
        //Initializing the ViewManager.
        ViewManager.init();
    }
}
