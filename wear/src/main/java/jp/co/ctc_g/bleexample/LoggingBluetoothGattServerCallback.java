package jp.co.ctc_g.bleexample;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

public class LoggingBluetoothGattServerCallback extends BluetoothGattServerCallback {

    private static final String LOG_TAG = "LoggingBluetoothGattServerCallback";

    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        switch (status) {
        case BluetoothGatt.GATT_SUCCESS:
            Log.i(LOG_TAG, "onServiceAdded " + service.getUuid().toString());
            break;

        default:
            Log.i(LOG_TAG, "onServiceAdded status is not GATT_SUCCESS");
            break;
        }
    }

    @Override
    public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status, int newState) {
        Log.d(LOG_TAG, "onConnectionStateChange [" + status + "->" + newState + "]");
    }

}
