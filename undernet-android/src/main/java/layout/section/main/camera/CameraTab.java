package layout.section.main.camera;


import android.animation.Animator;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import layout.section.main.Tab;
import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.camera.Camera;
import me.matoosh.undernet.camera.LegacyCamera;

/**
 * The camera tab of the app.
 * A simple {@link Fragment} subclass.
 */
public class CameraTab extends Tab{

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(CameraTab.class);

    //Section Transition
    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;
    /**
     * Gesture detector of this section.
     */
    private GestureDetector gestureDetector;
    /**
     * The preview surface view.
     */
    private SurfaceView mSurfaceView;

    /**
     * The callback of the camera preview surface.
     */
    public SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            //Checking the camera permissions.
            if(Camera.checkCameraPermissions()) {
                startPreview();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        }
    };

    public CameraTab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_camera, container, false);

        //Registering click listeners.
        Button shutterBtn = (Button) layout.findViewById(R.id.shutterButton);
        shutterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakePicture(v);
            }
        });

        //Setting up the communities pager transition.
        gestureDetector = new GestureDetector(layout.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //Handling the communities section transition.
                handleCommunitiesRevealAnim(e1, e2, distanceX, distanceY);

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

        });
        //Adding the touch listener to the gesture detector.
        MainActivity.instance.mainSection.pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);

                return false;
            }
        });


        //Caching the camera preview.
        mSurfaceView = (SurfaceView)layout.findViewById(R.id.cameraPreview);
        mSurfaceView.getHolder().addCallback(surfaceHolderCallback);

        return layout;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Camera.CAMERA_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission was granted.
                    startPreview();

                } else {

                    //Permission denied. Attempting to get permission once again.
                    Camera.checkCameraPermissions();
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Starts the camera preview.
     */
    public void startPreview() {
        //Opening the camera.
        if(Camera.currentCamera != null) {
            Camera.currentCamera.release();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Camera.currentCamera = new LegacyCamera(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            Camera.currentCamera = new LegacyCamera(android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK);
        }

        Camera.currentCamera.open();
        Camera.currentCamera.openPreview(mSurfaceView.getHolder());
    }

    @Override
    public void OnVisible() {
        //Preventing the screen from turning off.
        MainActivity.instance.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ActionBar actionBar = MainActivity.instance.getActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void OnInvisible() {
        //Turning off the keep screen on.
        MainActivity.instance.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Called when the Fragment is paused.
     */
    @Override
    public void onPause() {
        //Releasing all the cameras.
        if(Camera.currentCamera != null) {
            Camera.currentCamera.closePreview();
            Camera.currentCamera = null;
        }

        Camera.releaseAll();
        super.onPause();
    }

    /**
     * Called when the Fragment is resumed.
     */
    @Override
    public void onResume() {


        super.onResume();
    }

    /**
     * Called when the take picture button is pressed.
     */
    public void onTakePicture(View view) {
        logger.info("Taking a picture...");
    }

    /**
     * Handles the reveal transition to the communities section.
     */
    private void handleCommunitiesRevealAnim(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Making sure no accidental swipes happen.
        if(distanceY < -20f && Math.abs(distanceX) < 10f) {
            MainActivity.instance.communitiesSection.show(e1.getX(), e1.getY());
        }
    }
}
