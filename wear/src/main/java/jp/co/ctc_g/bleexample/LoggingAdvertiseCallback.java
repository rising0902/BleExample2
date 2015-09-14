package jp.co.ctc_g.bleexample;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.util.Log;

public class LoggingAdvertiseCallback extends AdvertiseCallback {

    private static final String LOG_TAG = "AdvertiseCallback";

    @Override
    public void onStartFailure(int errorCode) {
        super.onStartFailure(errorCode);
        switch (errorCode) {
        case ADVERTISE_FAILED_ALREADY_STARTED:
            Log.e(LOG_TAG, "already started!");
            break;
        case ADVERTISE_FAILED_DATA_TOO_LARGE:
            Log.e(LOG_TAG, "data is too large!");
            break;
        case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
            Log.e(LOG_TAG, "feature unsupported!");
            break;
        case ADVERTISE_FAILED_INTERNAL_ERROR:
            Log.e(LOG_TAG, "internal error!");
            break;
        case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
            Log.e(LOG_TAG, "too many advertisers!");
            break;
        }
    }

    @Override
    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
        super.onStartSuccess(settingsInEffect);
        Log.i(LOG_TAG, "onStartSuccess: " + settingsInEffect.toString());
    }
}
