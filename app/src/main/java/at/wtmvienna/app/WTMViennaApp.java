package at.wtmvienna.app;

import android.app.Application;
import android.content.Context;
import android.os.Looper;

import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import at.wtmvienna.app.core.dagger.AppComponent;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class WTMViennaApp extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = BuildConfig.TWITTER_KEY;
    private static final String TWITTER_SECRET = BuildConfig.TWITTER_SECRET;


    private AppComponent component;

    public static at.wtmvienna.app.WTMViennaApp get(Context context) {
        return (at.wtmvienna.app.WTMViennaApp) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // silent "crashes" if not the main thread
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                try {
                    FirebaseCrash.report(e);
                }
                catch (Throwable tx) {}
                if (t == Looper.getMainLooper().getThread()) {
                    System.exit(2);
                }
            }
        });
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        AndroidThreeTen.init(this);
        initGraph();
        initLogger();
    }

    public AppComponent component() {
        return component;
    }

    private void initGraph() {
        component = AppComponent.Initializer.init(this);
    }

    private void initLogger() {
        Timber.plant(new Timber.DebugTree());
    }
}
