package jp.co.ctc_g.bleexample;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;

import java.util.UUID;

import static jp.co.ctc_g.bleexample.Constants.*;

public class AdvertiseController {

    private Context context;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private BluetoothGattServer gattServer;
    private AdvertiseCallback advertiseCallback;

    public AdvertiseController(Context context) {
        this.context = context;
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        advertiseCallback = new LoggingAdvertiseCallback();
    }

    public void startAdvertising() {
        gattServer = bluetoothManager.openGattServer(context, new LoggingBluetoothGattServerCallback(gattServer));
        setupUuid();
        bluetoothLeAdvertiser.startAdvertising(createSettings(), createAdvertiseData(), advertiseCallback);
    }

    public void stopAdvertising() {

        if (gattServer != null) {
            gattServer.clearServices();
            gattServer.close();
            gattServer = null;
        }

        if (bluetoothLeAdvertiser != null) {
            bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
        }
    }

    private void setupUuid() {

        BluetoothGattService service = new BluetoothGattService(
                UUID.fromString(SERVICE_UUID),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(
                UUID.fromString(CHARACTERISTIC_UUID),
                BluetoothGattCharacteristic.PROPERTY_READ |
                        BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_READ |
                        BluetoothGattCharacteristic.PERMISSION_WRITE);
        characteristic.setValue("0x01".getBytes());
        BluetoothGattDescriptor desc = new BluetoothGattDescriptor(UUID.fromString(DESCRIPTOR_UUID), BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        desc.setValue("0xaa".getBytes());
        //characteristic.addDescriptor(desc);

        service.addCharacteristic(characteristic);

        gattServer.addService(service);
    }

    private AdvertiseSettings createSettings() {
        AdvertiseSettings.Builder settingBuilder = new AdvertiseSettings.Builder();
        settingBuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW);
        settingBuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        settingBuilder.setConnectable(true);
        return settingBuilder.build();
    }

    private AdvertiseData createAdvertiseData() {
        AdvertiseData.Builder dataBuilder = new AdvertiseData.Builder();
        dataBuilder.setIncludeTxPowerLevel(true);
        ParcelUuid uuid = new ParcelUuid(UUID.fromString(SERVICE_UUID));
        dataBuilder.addServiceUuid(uuid);
        //dataBuilder.addServiceData(uuid, "IAI".getBytes(Charset.forName("UTF-8")));
        AdvertiseData advertiseData = dataBuilder.build();
        return advertiseData;
    }

    private byte[] convert(String str) {
        byte[] result = null;
        try {
            char[] buf = new String(str.getBytes("UTF-8"), "UTF-8").toCharArray();
            result = asByteArray(Integer.toHexString(buf[0]) + Integer.toHexString(buf[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static byte[] asByteArray(String hex) {
        // 文字列長の1/2の長さのバイト配列を生成。
        byte[] bytes = new byte[hex.length() / 2];

        // バイト配列の要素数分、処理を繰り返す。
        for (int index = 0; index < bytes.length; index++) {
            // 16進数文字列をバイトに変換して配列に格納。
            bytes[index] =
                    (byte) Integer.parseInt(
                            hex.substring(index * 2, (index + 1) * 2),
                            16);
        }

        // バイト配列を返す。
        return bytes;
    }

}
