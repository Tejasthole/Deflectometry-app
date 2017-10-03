package paradva.nikunj.deflectometry;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import java.io.File;

public class CamActivity extends AppCompatActivity {
    CameraFuncation cameraFuncation;
    ImageView imageView;

    class C02062 implements Runnable {
        C02062() {
        }

        public void run() {
            if (CamActivity.this.cameraFuncation != null) {
                CamActivity.this.cameraFuncation.tackPicture();
            }
        }
    }

    public interface calls {
        void getfile(File file);
    }

    class C02751 implements calls {
        C02751() {
        }

        public void getfile(File file) {
            Log.e("file", file.getPath() + ":");
            Picasso.with(CamActivity.this.getApplicationContext()).load(file).into(CamActivity.this.imageView);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0217R.layout.activity_cam);
        Bundle bundle = getIntent().getExtras();
        int second = bundle.getInt("second");
        this.imageView = (ImageView) findViewById(C0217R.id.iv_main);
        this.imageView.setImageResource(C0217R.drawable.thole);
        this.cameraFuncation = new CameraFuncation(getApplicationContext(), (SurfaceView) findViewById(C0217R.id.picSurfaceView), new C02751());
        Log.e("sec", bundle.get("second") + ":");
        new Handler().postDelayed(new C02062(), (long) (second * 1000));
    }

    public void images(File uri) {
    }
}
