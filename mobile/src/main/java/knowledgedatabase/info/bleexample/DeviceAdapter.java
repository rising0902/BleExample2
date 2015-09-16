package knowledgedatabase.info.bleexample;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DeviceAdapter extends ArrayAdapter<ScannedDevice> {
    private final static String TAG = "DeviceAdapter";

    private static final String PREFIX_RSSI = "RSSI:";
    private List<ScannedDevice> mList;
    private LayoutInflater mInflater;
    private int mResId;

    public DeviceAdapter(Context context, int resId, List<ScannedDevice> objects) {
        super(context, resId, objects);
        mResId = resId;
        mList = objects;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i(TAG, "#DeviceAdapter");
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ScannedDevice device = (ScannedDevice) getItem(position);

        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(mResId, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(device.getDisplayName());
        holder.address.setText(device.getDevice().getAddress());
        holder.rssi.setText(PREFIX_RSSI + Integer.toString(device.getRssi()));

        Log.i(TAG, "#getView");
        return view;
    }

    public void update(BluetoothDevice newDevice, int rssi, byte[] scanRecord) {
        Log.i(TAG, "#update");
        if ((newDevice == null) || (newDevice.getAddress() == null)) {
            return;
        }

        boolean contains = false;
        for (ScannedDevice device : mList) {
            if (newDevice.getAddress().equals(device.getDevice().getAddress())) {
                Log.i(TAG, "contain");
                contains = true;
                device.setRssi(rssi); // update
                break;
            }
        }
        if (!contains) {
            Log.i(TAG, "not contain");
            mList.add(new ScannedDevice(newDevice, rssi));
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        @Bind(R.id.device_name) TextView name;
        @Bind(R.id.device_address) TextView address;
        @Bind(R.id.device_rssi) TextView rssi;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
