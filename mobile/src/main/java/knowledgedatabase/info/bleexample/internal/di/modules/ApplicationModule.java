package knowledgedatabase.info.bleexample.internal.di.modules;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import knowledgedatabase.info.bleexample.BleExampleApplication;

@Module
public class ApplicationModule {

    private final BleExampleApplication application;

    public ApplicationModule(BleExampleApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideApplicationContext() {
        return application;
    }
}
