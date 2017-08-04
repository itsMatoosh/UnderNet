package me.matoosh.undernet.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;

/**
 * Represents a device camera.
 * Created by Mateusz Rębacz on 04.08.2017.
 */

public abstract class Camera {
    /**
     * Registered cameras.
     */
    public static ArrayList<Camera> cameras = new ArrayList<>();
    /**
     * The currently used camera.
     */
    public static Camera currentCamera;
    /**
     * The camera permission request id.
     */
    public static final int CAMERA_PERMISSION_REQUEST = 69;

    /**
     * The id of the camera.
     */
    public int id;
    /**
     * Whether the camera is open.
     */
    public boolean isOpen = false;

    /**
     * Opens the camera.
     */
    public abstract void open();
    /**
     * Releases the camera for other apps.
     */
    public abstract void release();
    /**
     * Opens the camera preview.
     */
    public abstract void openPreview(SurfaceHolder surfaceTexture);
    /**
     * Closes the camera preview.
     */
    public abstract void closePreview();

    /**
     * Takes a picture with the camera.
     */
    public abstract void takePicture();

    /**
     * Starts recording a video with the camera.
     */
    public abstract void startRecording();

    /**
     * Stops the ongoing recording with the camera.
     */
    public abstract void stopRecording();

    /**
     * Changes the camera parameters.
     */
    public abstract void changeParameters();

    /**
     * Called when the screen orientation changes.
     */
    public abstract void onScreenRotationChanged();

    /**
     * Gets the current screen rotation in degrees.
     * @return
     */
    public int getDegreeRotation() {
        //Getting the rotation of the main activity.
        int rotation = MainActivity.instance.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        return degrees;
    }

    /**
     * Releases all cameras.
     */
    public static void releaseAll() {
        for (Camera camera:
             cameras) {
            if(camera.isOpen) {
                camera.release();
            }
        }
    }

    /**
     * Checks whether the app has camera permissions.
     * @return
     */
    public static boolean checkCameraPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.instance,
                Manifest.permission.CAMERA);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            android.support.v13.app.ActivityCompat.requestPermissions(MainActivity.instance,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }
}
