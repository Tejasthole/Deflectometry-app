package paradva.nikunj.deflectometry;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Build.VERSION;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import paradva.nikunj.deflectometry.CamActivity.calls;

public class CameraFuncation implements Callback, PictureCallback {
    private Camera camera;
    private int cameraId;
    private Context context;
    calls mcalls;
    private SurfaceHolder surfaceHolder;
    private SurfaceView surfaceView;
    private Runnable tackPictureRunnable = new C02071();

    class C02071 implements Runnable {
        C02071() {
        }

        public void run() {
            try {
                if (CameraFuncation.this.camera != null) {
                    CameraFuncation.this.camera.takePicture(null, null, null, CameraFuncation.this);
                }
            } catch (Exception e) {
                CameraFuncation.this.clearCamera();
            }
        }
    }

    @SuppressLint({"NewApi"})
    public CameraFuncation(Context context, SurfaceView surfaceView, calls calls) {
        this.context = context;
        this.surfaceView = surfaceView;
        this.surfaceHolder = this.surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
        this.surfaceHolder.setType(3);
        this.surfaceHolder.setKeepScreenOn(true);
        this.mcalls = calls;
    }

    public void clearCamera() {
        if (this.camera != null) {
            this.camera.release();
            this.camera = null;
        }
    }

    public void tackPicture() {
        new Thread(this.tackPictureRunnable).start();
    }

    private Camera openFacingBackCamera() {
        Camera cam = null;
        try {
            CameraInfo cameraInfo = new CameraInfo();
            int camIdx = 0;
            int cameraCount = Camera.getNumberOfCameras();
            while (camIdx < cameraCount) {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == 1) {
                    try {
                        cam = Camera.open(camIdx);
                        this.cameraId = camIdx;
                        break;
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                } else {
                    camIdx++;
                }
            }
            if (cam == null) {
                return Camera.open();
            }
            return cam;
        } catch (Exception e2) {
            return null;
        }
    }

    private File getDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Deflectometry");
    }

    public void onPictureTaken(byte[] data, Camera camera) {
        File pictureFileDir = getDir();
        if (pictureFileDir.exists() || pictureFileDir.mkdirs()) {
            File pictureFile = new File(pictureFileDir.getPath() + File.separator + ("Picture_" + new SimpleDateFormat("yyyymmddhhmmss").format(new Date()) + ".jpg"));
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (Exception e) {
            }
            this.mcalls.getfile(pictureFile);
        }
    }

    @SuppressLint({"NewApi"})
    @TargetApi(9)
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (VERSION.SDK_INT >= 9) {
                for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                    CameraInfo info = new CameraInfo();
                    Camera.getCameraInfo(i, info);
                    if (info.facing == 1) {
                        this.camera = Camera.open(i);
                    }
                }
            }
            if (this.camera == null) {
                this.camera = Camera.open();
            }
            this.camera.setDisplayOrientation(90);
            this.camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            if (this.camera != null) {
                this.camera.release();
            }
            this.camera = null;
            e.printStackTrace();
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (this.camera != null) {
            Parameters parameters = this.camera.getParameters();
            parameters.setRotation(270);
            this.camera.setParameters(parameters);
            this.camera.startPreview();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.camera != null) {
            this.camera.stopPreview();
            this.camera.release();
        }
        this.camera = null;
    }

    public int getDegree() {
        CameraInfo info = new CameraInfo();
        Camera camera = this.camera;
        Camera.getCameraInfo(this.cameraId, info);
        return info.orientation;
    }
}
