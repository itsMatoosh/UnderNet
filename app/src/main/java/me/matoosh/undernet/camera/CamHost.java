package me.matoosh.undernet.camera;

import android.graphics.SurfaceTexture;
import android.view.TextureView;

/**
 * Manages the camera preview.
 */
public class CamHost {
    /**
     * View for displaying the camera preview.
     */
    private TextureView previewView;
    /**
     * Surface texture listener.
     */
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    /**
     * Initializes the CamHost.
     */
    public void init() {

    }
    /**
     * Sets up the camera.
     */
    private void setupCamera(SurfaceTexture preview, int prevWidth, int prevHeight) {

    }
}
