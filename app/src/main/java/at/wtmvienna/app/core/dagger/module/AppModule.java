package at.wtmvienna.app.core.dagger.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public final class AppModule {

    private final at.wtmvienna.app.WTMViennaApp app;

    public AppModule(at.wtmvienna.app.WTMViennaApp app) {
        this.app = app;
    }

    @Provides @Singleton Application provideApplication() {
        return app;
    }
}
