package paradva.nikunj.deflectometry;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Capture extends Service implements Callback {
    private String FLASH_MODE;
    private int QUALITY_MODE = 0;
    private Bitmap bmp;
    public Intent cameraIntent;
    Editor editor;
    FileOutputStream fo;
    Handler handler = new Handler();
    int height = 0;
    private boolean isFrontCamRequest = false;
    PictureCallback mCall = new C02167();
    private Camera mCamera;
    private Parameters parameters;
    LayoutParams params;
    private Size pictureSize;
    SharedPreferences pref;
    private SurfaceHolder sHolder;
    SurfaceView sv;
    int width = 0;
    private WindowManager windowManager;

    class C02081 implements Runnable {
        C02081() {
        }

        public void run() {
            Toast.makeText(Capture.this.getApplicationContext(), "API dosen't support front camera", 1).show();
        }
    }

    class C02092 implements Runnable {
        C02092() {
        }

        public void run() {
            Toast.makeText(Capture.this.getApplicationContext(), "Your Device dosen't have Front Camera !", 1).show();
        }
    }

    class C02103 implements Runnable {
        C02103() {
        }

        public void run() {
            Toast.makeText(Capture.this.getApplicationContext(), "API dosen't support front camera", 1).show();
        }
    }

    class C02114 implements Runnable {
        C02114() {
        }

        public void run() {
            Toast.makeText(Capture.this.getApplicationContext(), "Your Device dosen't have Front Camera !", 1).show();
        }
    }

    class C02125 implements Runnable {
        C02125() {
        }

        public void run() {
            Toast.makeText(Capture.this.getApplicationContext(), "Camera is unavailable !", 1).show();
        }
    }

    class C02136 implements Runnable {
        C02136() {
        }

        public void run() {
            Toast.makeText(Capture.this.getApplicationContext(), "Your Device dosen't have a Camera !", 1).show();
        }
    }

    class C02167 implements PictureCallback {

        class C02141 implements OnScanCompletedListener {
            C02141() {
            }

            public void onScanCompleted(String path, Uri uri) {
                Log.i("ExternalStorage", "Scanned " + path + ":");
                Log.i("ExternalStorage", "-> uri=" + uri);
            }
        }

        class C02152 implements Runnable {
            C02152() {
            }

            public void run() {
                Toast.makeText(Capture.this.getApplicationContext(), "Your Picture has been taken !", 0).show();
            }
        }

        C02167() {
        }

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d("ImageTakin", "Done");
            if (Capture.this.bmp != null) {
                Capture.this.bmp.recycle();
            }
            System.gc();
            Capture.this.bmp = Capture.decodeBitmap(data);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            if (Capture.this.bmp != null && Capture.this.QUALITY_MODE == 0) {
                Capture.this.bmp.compress(CompressFormat.JPEG, 70, bytes);
            } else if (!(Capture.this.bmp == null || Capture.this.QUALITY_MODE == 0)) {
                Capture.this.bmp.compress(CompressFormat.JPEG, Capture.this.QUALITY_MODE, bytes);
            }
            File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MYGALLERY");
            if (!imagesFolder.exists()) {
                imagesFolder.mkdirs();
            }
            File image = new File(imagesFolder, System.currentTimeMillis() + ".jpg");
            try {
                Capture.this.fo = new FileOutputStream(image);
            } catch (FileNotFoundException e) {
                Log.e("TAG", "FileNotFoundException", e);
            }
            try {
                Capture.this.fo.write(bytes.toByteArray());
            } catch (IOException e2) {
                Log.e("TAG", "fo.write::PictureTaken", e2);
            }
            try {
                Capture.this.fo.close();
                if (VERSION.SDK_INT < 19) {
                    Capture.this.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.parse("file://" + Environment.getExternalStorageDirectory())));
                } else {
                    MediaScannerConnection.scanFile(Capture.this.getApplicationContext(), new String[]{image.toString()}, null, new C02141());
                }
            } catch (IOException e22) {
                e22.printStackTrace();
            }
            if (Capture.this.mCamera != null) {
                Capture.this.mCamera.stopPreview();
                Capture.this.mCamera.release();
                Capture.this.mCamera = null;
            }
            Log.d("Camera", "Image Taken !");
            if (Capture.this.bmp != null) {
                Capture.this.bmp.recycle();
                Capture.this.bmp = null;
                System.gc();
            }
            Capture.this.mCamera = null;
            Capture.this.handler.post(new C02152());
            Capture.this.stopSelf();
        }
    }

    private class TakeImage extends AsyncTask<Intent, Void, Void> {
        private TakeImage() {
        }

        protected Void doInBackground(Intent... params) {
            Capture.this.takeImage(params[0]);
            return null;
        }

        protected void onPostExecute(Void result) {
        }
    }

    public void onCreate() {
        super.onCreate();
    }

    private Camera openFrontFacingCameraGingerbread() {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
        }
        Camera cam = null;
        CameraInfo cameraInfo = new CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == 1) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("Camera", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }
        return cam;
    }

    private void setBesttPictureResolution() {
        int i = 1;
        this.width = this.pref.getInt("Picture_Width", 0);
        this.height = this.pref.getInt("Picture_height", 0);
        int i2 = this.width == 0 ? 1 : 0;
        if (this.height != 0) {
            i = 0;
        }
        if ((i2 | i) != 0) {
            this.pictureSize = getBiggesttPictureSize(this.parameters);
            if (this.pictureSize != null) {
                this.parameters.setPictureSize(this.pictureSize.width, this.pictureSize.height);
            }
            this.width = this.pictureSize.width;
            this.height = this.pictureSize.height;
            this.editor.putInt("Picture_Width", this.width);
            this.editor.putInt("Picture_height", this.height);
            this.editor.commit();
            return;
        }
        this.parameters.setPictureSize(this.width, this.height);
    }

    private Size getBiggesttPictureSize(Parameters parameters) {
        Size result = null;
        for (Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else if (size.width * size.height > result.width * result.height) {
                result = size;
            }
        }
        return result;
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.camera")) {
            return true;
        }
        return false;
    }

    private boolean checkFrontCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature("android.hardware.camera.front")) {
            return true;
        }
        return false;
    }

    private synchronized void takeImage(Intent intent) {
        if (checkCameraHardware(getApplicationContext())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                this.FLASH_MODE = extras.getString("FLASH");
                this.isFrontCamRequest = extras.getBoolean("Front_Request");
                this.QUALITY_MODE = extras.getInt("Quality_Mode");
            }
            if (this.isFrontCamRequest) {
                this.FLASH_MODE = "off";
                Parameters parameters;
                if (VERSION.SDK_INT >= 9) {
                    this.mCamera = openFrontFacingCameraGingerbread();
                    if (this.mCamera != null) {
                        try {
                            this.mCamera.setPreviewDisplay(this.sv.getHolder());
                        } catch (IOException e) {
                            this.handler.post(new C02081());
                            stopSelf();
                        }
                        parameters = this.mCamera.getParameters();
                        this.pictureSize = getBiggesttPictureSize(parameters);
                        if (this.pictureSize != null) {
                            parameters.setPictureSize(this.pictureSize.width, this.pictureSize.height);
                        }
                        this.mCamera.setParameters(parameters);
                        this.mCamera.startPreview();
                        this.mCamera.takePicture(null, null, this.mCall);
                    } else {
                        this.mCamera = null;
                        this.handler.post(new C02092());
                        stopSelf();
                    }
                } else if (checkFrontCamera(getApplicationContext())) {
                    this.mCamera = openFrontFacingCameraGingerbread();
                    if (this.mCamera != null) {
                        try {
                            this.mCamera.setPreviewDisplay(this.sv.getHolder());
                        } catch (IOException e2) {
                            this.handler.post(new C02103());
                            stopSelf();
                        }
                        parameters = this.mCamera.getParameters();
                        this.pictureSize = getBiggesttPictureSize(parameters);
                        if (this.pictureSize != null) {
                            parameters.setPictureSize(this.pictureSize.width, this.pictureSize.height);
                        }
                        this.mCamera.setParameters(parameters);
                        this.mCamera.startPreview();
                        this.mCamera.takePicture(null, null, this.mCall);
                    } else {
                        this.mCamera = null;
                        this.handler.post(new C02114());
                        stopSelf();
                    }
                }
            } else {
                if (this.mCamera != null) {
                    this.mCamera.stopPreview();
                    this.mCamera.release();
                    this.mCamera = Camera.open();
                } else {
                    this.mCamera = getCameraInstance();
                }
                try {
                    if (this.mCamera != null) {
                        this.mCamera.setPreviewDisplay(this.sv.getHolder());
                        this.parameters = this.mCamera.getParameters();
                        if (this.FLASH_MODE == null || this.FLASH_MODE.isEmpty()) {
                            this.FLASH_MODE = "auto";
                        }
                        this.parameters.setFlashMode(this.FLASH_MODE);
                        setBesttPictureResolution();
                        Log.d("Qaulity", this.parameters.getJpegQuality() + "");
                        Log.d("Format", this.parameters.getPictureFormat() + "");
                        this.mCamera.setParameters(this.parameters);
                        this.mCamera.startPreview();
                        Log.d("ImageTakin", "OnTake()");
                        this.mCamera.takePicture(null, null, this.mCall);
                    } else {
                        this.handler.post(new C02125());
                    }
                } catch (IOException e3) {
                    Log.e("TAG", "CmaraHeadService()::takePicture", e3);
                }
            }
        } else {
            this.handler.post(new C02136());
            stopSelf();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        this.cameraIntent = intent;
        Log.d("ImageTakin", "StartCommand()");
        this.pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        this.editor = this.pref.edit();
        this.windowManager = (WindowManager) getSystemService("window");
        this.params = new LayoutParams(-2, -2, 2003, 8, -3);
        this.params.gravity = 51;
        this.params.width = 1;
        this.params.height = 1;
        this.params.x = 0;
        this.params.y = 0;
        this.sv = new SurfaceView(getApplicationContext());
        this.windowManager.addView(this.sv, this.params);
        this.sHolder = this.sv.getHolder();
        this.sHolder.addCallback(this);
        if (VERSION.SDK_INT < 11) {
            this.sHolder.setType(3);
        }
        return 1;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    public void onDestroy() {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
        if (this.sv != null) {
            this.windowManager.removeView(this.sv);
        }
        Intent intent = new Intent("custom-event-name");
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        super.onDestroy();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if (this.cameraIntent != null) {
            new TakeImage().execute(new Intent[]{this.cameraIntent});
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (this.mCamera != null) {
            this.mCamera.stopPreview();
            this.mCamera.release();
            this.mCamera = null;
        }
    }

    public static Bitmap decodeBitmap(byte[] data) {
        Options bfOptions = new Options();
        bfOptions.inDither = false;
        bfOptions.inPurgeable = true;
        bfOptions.inInputShareable = true;
        bfOptions.inTempStorage = new byte[32768];
        if (data != null) {
            return BitmapFactory.decodeByteArray(data, 0, data.length, bfOptions);
        }
        return null;
    }
}
