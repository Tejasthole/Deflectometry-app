package paradva.nikunj.deflectometry;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.IOException;

public class CameraPreview extends SurfaceView implements Callback {
    private Camera mCamera;
    private SurfaceHolder mHolder = getHolder();

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.mCamera = camera;
        this.mHolder.addCallback(this);
        this.mHolder.setType(3);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            this.mCamera.setPreviewDisplay(holder);
            this.mCamera.startPreview();
        } catch (IOException e) {
            Log.d("", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mCamera != null) {
            this.mCamera.release();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (this.mHolder.getSurface() != null) {
            try {
                this.mCamera.stopPreview();
            } catch (Exception e) {
            }
            try {
                this.mCamera.setPreviewDisplay(this.mHolder);
                this.mCamera.startPreview();
            } catch (Exception e2) {
                Log.d("", "Error starting camera preview: " + e2.getMessage());
            }
        }
    }
}
