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

import java.util.UUID;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DeviceActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_BLUETOOTH_DEVICE = "BT_DEVICE";
    private BluetoothDevice mDevice;
    private BluetoothGatt mConnGatt;
    private int mStatus;

    private Button mReadManufacturerNameButton;
    private Button mReadSerialNumberButton;
    private Button mWriteAlertLevelButton;

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
                        mReadManufacturerNameButton.setEnabled(false);
                        mReadSerialNumberButton.setEnabled(false);
                        mWriteAlertLevelButton.setEnabled(false);
                    };
                });
            }
        };

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            for (BluetoothGattService service : gatt.getServices()) {
                if ((service == null) || (service.getUuid() == null)) {
                    continue;
                }
                if (BleUuid.SERVICE_DEVICE_INFORMATION.equalsIgnoreCase(service .getUuid().toString())) {
                    mReadManufacturerNameButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_MANUFACTURER_NAME_STRING)));
                    mReadSerialNumberButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_SERIAL_NUMBEAR_STRING)));
                    runOnUiThread(new Runnable() {
                        public void run() {
                            mReadManufacturerNameButton.setEnabled(true);
                            mReadSerialNumberButton.setEnabled(true);
                        };
                    });
                }
                if (BleUuid.SERVICE_IMMEDIATE_ALERT.equalsIgnoreCase(service.getUuid().toString())) {
                    runOnUiThread(new Runnable() {
                         public void run() {
                             mWriteAlertLevelButton.setEnabled(true);
                         };
                    });
                    mWriteAlertLevelButton.setTag(service.getCharacteristic(UUID.fromString(BleUuid.CHAR_ALERT_LEVEL)));
                }
            }

            runOnUiThread(new Runnable() {
				public void run() {
					setProgressBarIndeterminateVisibility(false);
				};
			});
        };

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			if (status == BluetoothGatt.GATT_SUCCESS) {
				if (BleUuid.CHAR_MANUFACTURER_NAME_STRING
						.equalsIgnoreCase(characteristic.getUuid().toString())) {
					final String name = characteristic.getStringValue(0);

					runOnUiThread(new Runnable() {
						public void run() {
							mReadManufacturerNameButton.setText(name);
							setProgressBarIndeterminateVisibility(false);
						};
					});
				} else if (BleUuid.CHAR_SERIAL_NUMBEAR_STRING
						.equalsIgnoreCase(characteristic.getUuid().toString())) {
					final String name = characteristic.getStringValue(0);

					runOnUiThread(new Runnable() {
						public void run() {
							mReadSerialNumberButton.setText(name);
							setProgressBarIndeterminateVisibility(false);
						};
					});
				}

			}
		}

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            runOnUiThread(new Runnable() {
				public void run() {
					setProgressBarIndeterminateVisibility(false);
				};
			});
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device);

        mStatus = BluetoothProfile.STATE_DISCONNECTED;
        mReadManufacturerNameButton = (Button) findViewById(R.id.read_manufacturer_name_button);
        mReadManufacturerNameButton.setOnClickListener(this);
        mReadSerialNumberButton = (Button) findViewById(R.id.read_serial_number_button);
        mReadSerialNumberButton.setOnClickListener(this);
        mWriteAlertLevelButton = (Button) findViewById(R.id.write_alert_level_button);
        mWriteAlertLevelButton.setOnClickListener(this);
    }

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
    }

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.read_manufacturer_name_button) {
			if ((v.getTag() != null)
					&& (v.getTag() instanceof BluetoothGattCharacteristic)) {
				BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
						.getTag();
				if (mConnGatt.readCharacteristic(ch)) {
					setProgressBarIndeterminateVisibility(true);
				}
			}
		} else if (v.getId() == R.id.read_serial_number_button) {
			if ((v.getTag() != null)
					&& (v.getTag() instanceof BluetoothGattCharacteristic)) {
				BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
						.getTag();
				if (mConnGatt.readCharacteristic(ch)) {
					setProgressBarIndeterminateVisibility(true);
				}
			}

		} else if (v.getId() == R.id.write_alert_level_button) {
			if ((v.getTag() != null)
					&& (v.getTag() instanceof BluetoothGattCharacteristic)) {
				BluetoothGattCharacteristic ch = (BluetoothGattCharacteristic) v
						.getTag();
				ch.setValue(new byte[] { (byte) 0x03 });
				if (mConnGatt.writeCharacteristic(ch)) {
					setProgressBarIndeterminateVisibility(true);
				}
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

        mReadManufacturerNameButton.setEnabled(false);
        mReadSerialNumberButton.setEnabled(false);
        mWriteAlertLevelButton.setEnabled(false);

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
