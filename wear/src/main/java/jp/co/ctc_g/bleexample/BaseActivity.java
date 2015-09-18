package jp.co.ctc_g.bleexample;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import static jp.co.ctc_g.bleexample.BleExampleApplication.TAG;

public abstract class BaseActivity extends Activity {

    protected void showText(Context ctx, final String text) {
        Toast.makeText(this, TAG + text, Toast.LENGTH_SHORT).show();
    }

    protected void showText(final String text) {
        showText(this, text);
    }
}
