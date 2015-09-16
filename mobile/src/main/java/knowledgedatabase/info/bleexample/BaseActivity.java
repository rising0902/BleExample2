package knowledgedatabase.info.bleexample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import jp.co.ctc_g.common.BleUtil;

public abstract class BaseActivity extends Activity {

    private final static String TAG = "BaseActivity";

    protected BluetoothAdapter mBluetoothAdapter;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void initialize() {

        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BluetoothManager manager = BleUtil.getManager(this);
        if (manager != null) {
            mBluetoothAdapter = manager.getAdapter();
        }
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    protected void showText(Context ctx, final String text) {
        Toast.makeText(this, TAG + text, Toast.LENGTH_SHORT).show();
    }

    protected void showText(final String text) {
        showText(this, text);
    }
}
