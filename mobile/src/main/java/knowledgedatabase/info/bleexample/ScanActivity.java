package knowledgedatabase.info.bleexample;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseActivity {
    private BluetoothLeScanner mBluetoothLeScanner;
    private DeviceScanCallback mDeviceScanCallback;
    private DeviceAdapter mDeviceAdapter;
    private boolean mIsScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_scan);

        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mIsScanning) {
            menu.findItem(R.id.action_scan).setVisible(false);
            menu.findItem(R.id.action_stop).setVisible(true);
        } else {
            menu.findItem(R.id.action_scan).setVisible(true);
            menu.findItem(R.id.action_stop).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            // ignore
            return true;
        } else if (itemId == R.id.action_scan) {
            startScan();
            return true;
        } else if (itemId == R.id.action_stop) {
            stopScan();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void initialize() {
        super.initialize();

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mDeviceScanCallback = new DeviceScanCallback();

        ListView deviceListView = (ListView) findViewById(R.id.list);
        mDeviceAdapter = new DeviceAdapter(this, R.layout.listitem_device,
                new ArrayList<ScannedDevice>());
        deviceListView.setAdapter(mDeviceAdapter);
        deviceListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
                ScannedDevice item = mDeviceAdapter.getItem(position);
                if (item != null) {
                    Intent intent = new Intent(view.getContext(), DeviceActivity.class);
                    BluetoothDevice selectedDevice = item.getDevice();
                    intent.putExtra(DeviceActivity.EXTRA_BLUETOOTH_DEVICE, selectedDevice);
                    startActivity(intent);

                    stopScan();
                }
            }
        });

        stopScan();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScan() {
        if ((mBluetoothLeScanner != null) && (!mIsScanning)) {
            mBluetoothLeScanner.startScan(mDeviceScanCallback);
            mIsScanning = true;
            setProgressBarIndeterminateVisibility(true);
            invalidateOptionsMenu();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopScan() {
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mDeviceScanCallback);
        }
        mIsScanning = false;
        setProgressBarIndeterminateVisibility(false);
        invalidateOptionsMenu();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private class DeviceScanCallback extends ScanCallback {
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            final BluetoothDevice device = result.getDevice();
            final int rssi = result.getRssi();
            final byte[] scanRecord = result.getScanRecord().getBytes();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDeviceAdapter.update(device, rssi, scanRecord);
                }
            });
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result: results) {
                final BluetoothDevice device = result.getDevice();
                final int rssi = result.getRssi();
                final byte[] scanRecord = result.getScanRecord().getBytes();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceAdapter.update(device, rssi, scanRecord);
                    }
                });
            }
        }

        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            showText("Device scan fail!");
            mIsScanning = false;
            setProgressBarIndeterminateVisibility(false);
            invalidateOptionsMenu();
        }
    }
}
