package knowledgedatabase.info.bleexample;

import android.app.Application;
import android.content.res.Configuration;

import knowledgedatabase.info.bleexample.internal.di.components.ApplicationComponent;
import knowledgedatabase.info.bleexample.internal.di.components.DaggerApplicationComponent;
import knowledgedatabase.info.bleexample.internal.di.modules.ApplicationModule;

public class BleExampleApplication extends Application {

    public static final String TAG = "BleExample-Mobile";

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        injectApplicationComponent();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    private void injectApplicationComponent() {
        this.applicationComponent =
                DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        this.applicationComponent.inject(this);
    }
}
