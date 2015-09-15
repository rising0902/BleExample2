package jp.co.ctc_g.bleexample;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import java.util.UUID;

public class LoggingBluetoothGattServerCallback extends BluetoothGattServerCallback {

    private static final String TAG = "LoggingBluetoothGattServerCallback";
    private BluetoothGattServer mGattServer;

    public LoggingBluetoothGattServerCallback(BluetoothGattServer mGattServer) {
        this.mGattServer = mGattServer;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onServiceAdded(int status, BluetoothGattService service) {
        switch (status) {
        case BluetoothGatt.GATT_SUCCESS:
            Log.i(TAG, "onServiceAdded " + service.getUuid().toString());
            break;

        default:
            Log.i(TAG, "onServiceAdded status is not GATT_SUCCESS");
            break;
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status, int newState) {
        Log.d(TAG, "onConnectionStateChange [" + status + "->" + newState + "]");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device,
                                            int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Log.d(TAG, "onCharacteristicReadRequest requestId=" + requestId + " offset=" + offset);
        if (characteristic.getUuid().equals(UUID.fromString(Constants.CHARACTERISTIC_UUID))) {
            Log.d(TAG, "CHAR_MANUFACTURER_NAME_STRING");
            characteristic.setValue("Name:Hoge");
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset, characteristic.getValue());
        }
    }

}
