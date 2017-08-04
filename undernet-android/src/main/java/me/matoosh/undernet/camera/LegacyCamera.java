package me.matoosh.undernet.camera;

import android.view.SurfaceHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Camera supporting devices below Lollipop.
 * Created by Mateusz Rębacz on 04.08.2017.
 */

public class LegacyCamera extends Camera {
    /**
     * The camera device.
     */
    public android.hardware.Camera mCamera;
    /**
     * The camera surface preview.
     */
    public SurfaceHolder mSurfaceHolder;
    /**
     * The logger of this class.
     */
    public Logger logger = LoggerFactory.getLogger(LegacyCamera.class);

    /**
     * Opens the camera.
     */
    public LegacyCamera(int cameraId) {
        this.id = cameraId;
        Camera.cameras.add(this);
    }

    /**
     * Opens the camera.
     */
    @Override
    public void open() {
        logger.info("Opening camera " + this.id);
        mCamera = android.hardware.Camera.open(this.id);
        isOpen = true;
    }

    /**
     * Releases the camera for other apps.
     */
    @Override
    public void release() {
        logger.info("Releasing camera " + this.id);
        mCamera.release();
        isOpen = false;
    }

    /**
     * Opens the camera preview.
     *
     * @param surfaceHolder
     */
    @Override
    public void openPreview(SurfaceHolder surfaceHolder) {
        logger.info("Opening preview of camera " + this.id);
        mSurfaceHolder = surfaceHolder;
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        Camera.currentCamera = this;
    }

    /**
     * Closes the camera preview.
     */
    @Override
    public void closePreview() {

    }

    /**
     * Takes a picture with the camera.
     */
    @Override
    public void takePicture() {

    }

    /**
     * Starts recording a video with the camera.
     */
    @Override
    public void startRecording() {

    }

    /**
     * Stops the ongoing recording with the camera.
     */
    @Override
    public void stopRecording() {

    }

    /**
     * Changes the camera parameters.
     */
    @Override
    public void changeParameters() {

    }

    /**
     * Called when the screen orientation changes.
     */
    @Override
    public void onScreenRotationChanged() {
        //Changing the mCamera preview orientation.
        mCamera.setDisplayOrientation(getDegreeRotation());
    }
}
