package knowledgedatabase.info.bleexample.internal.di.components;

import javax.inject.Singleton;

import dagger.Component;
import knowledgedatabase.info.bleexample.BleExampleApplication;
import knowledgedatabase.info.bleexample.internal.di.modules.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(BleExampleApplication application);
}
