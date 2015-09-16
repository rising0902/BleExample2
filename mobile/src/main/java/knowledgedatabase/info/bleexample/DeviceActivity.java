package knowledgedatabase.info.bleexample;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.co.ctc_g.common.BleUuid;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceActivity extends BaseActivity {

    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    @Bind(R.id.read_manufacturer_name_button)
    Button readManufacturerNameButton;
    @Bind(R.id.read_serial_number_button)
    Button readSerialNumberButton;
    @Bind(R.id.write_alert_level_button)
    Button writeAlertLevelButton;
    private BluetoothDevice mDevice;
    private BluetoothGatt mConnGatt;
    private int mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device);
        ButterKnife.bind(this);

        mStatus = BluetoothProfile.STATE_DISCONNECTED;
    }

    private final BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mStatus = newState;
                mConnGatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                mStatus = newState;
                runOnUiThread(new Runnable() {
                    public void run() {
                        readManufacturerNameButton.setEnabled(false);
                        readSerialNumberButton.setEnabled(false);
                        writeAlertLevelButton.setEnabled(false);
                    }
                });
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION.equalsIgnoreCase(service.getUuid().toString())) {
                    readManufacturerNameButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_MANUFACTURER_NAME_STRING)));
                    readSerialNumberButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_SERIAL_NUMBEAR_STRING)));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            readManufacturerNameButton.setEnabled(true);
                            readSerialNumberButton.setEnabled(true);
                        }
                    });
                }
                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service.getUuid().toString())) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            writeAlertLevelButton.setEnabled(true);
                        }

                        ;
                    });
                    writeAlertLevelButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }
            }

            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                }

                ;
            });
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (BleUuid.CHAR_MANUFACTURER_NAME_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            readManufacturerNameButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                } else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
                        .equalsIgnoreCase(characteristic.getUuid().toString())) {
                    final String name = characteristic.getStringValue(0);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            readSerialNumberButton.setText(name);
                            setProgressBarIndeterminateVisibility(false);
                        }

                        ;
                    });
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            runOnUiThread(new Runnable() {
                public void run() {
                    setProgressBarIndeterminateVisibility(false);
                }

                ;
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        initialize();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnGatt != null) {
            if ((mStatus != BluetoothProfile.STATE_DISCONNECTING) && (mStatus != BluetoothProfile.STATE_DISCONNECTED)) {
                mConnGatt.disconnect();
            }
            mConnGatt.close();
            mConnGatt = null;
        }
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.read_manufacturer_name_button)
    public void onClickReadManufacturerNameButton(Button button) {
        if ((button.getTag() != null) && (button.getTag() instanceof BluetoothGattCharacteristic)) {
            BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) button.getTag();
            if (mConnGatt.readCharacteristic(ch)) {
                setProgressBarIndeterminateVisibility(true);
            }
        }
    }

    @OnClick(R.id.read_serial_number_button)
    public void onClickReadSerialNumberButton(Button button) {
        if ((button.getTag() != null) && (button.getTag() instanceof BluetoothGattCharacteristic)) {
            BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) button.getTag();
            if (mConnGatt.readCharacteristic(ch)) {
                setProgressBarIndeterminateVisibility(true);
            }
        }
    }

     @OnClick(R.id.write_alert_level_button)
     public void onClickWriteAlertLevelButton(Button button) {
         if ((button.getTag() != null) && (button.getTag() instanceof BluetoothGattCharacteristic)) {
             BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) button.getTag();
             ch.setValue(new byte[]{(byte) 0x03});
             if (mConnGatt.writeCharacteristic(ch)) {
                 setProgressBarIndeterminateVisibility(true);
             }
         }
     }

    protected void initialize() {
        super.initialize();

        if (mDevice == null) {
            mDevice = getBTDeviceExtra();
            if (mDevice == null) {
                finish();
                return;
            }
        }

        readManufacturerNameButton.setEnabled(false);
        readSerialNumberButton.setEnabled(false);
        writeAlertLevelButton.setEnabled(false);

        if ((mConnGatt == null) && (mStatus == BluetoothProfile.STATE_DISCONNECTED)) {
            mConnGatt = mDevice.connectGatt(this, false, mGattcallback);
            mStatus = BluetoothProfile.STATE_CONNECTING;
        } else {
            if (mConnGatt != null) {
                mConnGatt.connect();
                mConnGatt.discoverServices();
            } else {
                showText("state error");
                finish();
                return;
            }
        }
        setProgressBarIndeterminateVisibility(true);
    }

    private BluetoothDevice getBTDeviceExtra() {
        Intent intent = getIntent();
        if (intent == null) {
            return null;
        }

        Bundle extras = intent.getExtras();
        if (extras == null) {
            return null;
        }

        return extras.getParcelable(EXTRA_BLUETOOTH_DEVICE);
    }

}
