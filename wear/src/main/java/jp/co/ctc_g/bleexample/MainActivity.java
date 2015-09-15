package jp.co.ctc_g.bleexample;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import jp.co.ctc_g.common.BleUtil;
import knowledgedatabase.info.bleexample.R;

public class MainActivity extends Activity {

    private final static String TAG = "MainActivity";

    private AdvertiseController advertiseController;
    private Button advertiseStartButton;
    private Button advertiseStopButton;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        if (!BleUtil.isBLESupported(getApplicationContext())) {
            finish();
        }

        advertiseController = new AdvertiseController(getApplicationContext());

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                advertiseStartButton = (Button) findViewById(R.id.advertiseStartButton);
                advertiseStartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        advertiseController.startAdvertising();
                        statusTextView.setText("Now Advertising!");
                    }
                });
                advertiseStopButton = (Button) findViewById(R.id.advertiseStopButton);
                advertiseStopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        advertiseController.stopAdvertising();
                        statusTextView.setText("");
                    }
                });
                statusTextView = (TextView) findViewById(R.id.statusTextView);
            }
        });
    }

    public void onResume() {
        super.onResume();
        // EventBus.getDefault().register(this);
        // advertiseController.startAdvertising();
    }

    public void onPause() {
        super.onPause();
        // EventBus.getDefault().unregister(this);
        // advertiseController.stopAdvertising();
    }

}
