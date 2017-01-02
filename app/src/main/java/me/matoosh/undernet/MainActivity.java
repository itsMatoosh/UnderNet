package me.matoosh.undernet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
