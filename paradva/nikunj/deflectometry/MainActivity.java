package paradva.nikunj.deflectometry;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {
    RadioGroup radioGroup;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) C0217R.layout.activity_main);
        this.radioGroup = (RadioGroup) findViewById(C0217R.id.radioGroup);
        if (VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0 && ContextCompat.checkSelfPermission(this, "android.permission.CAMERA") != 0) {
            requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"}, 123);
        }
    }

    public void onnext(View view) {
        int i = 0;
        switch (this.radioGroup.getCheckedRadioButtonId()) {
            case C0217R.id.s_1:
                i = 1;
                break;
            case C0217R.id.s_2:
                i = 2;
                break;
            case C0217R.id.s_3:
                i = 3;
                break;
            case C0217R.id.s_5:
                i = 5;
                break;
            case C0217R.id.s_10:
                i = 10;
                break;
        }
        Intent intent = new Intent(this, CamActivity.class);
        intent.putExtra("second", i);
        startActivity(intent);
        Log.e("i", i + ":");
    }
}
