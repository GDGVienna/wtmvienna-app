package at.devfest.app.core.dagger;

import javax.inject.Singleton;

import at.devfest.app.DevFestApp;
import at.devfest.app.core.dagger.module.ApiModule;
import at.devfest.app.core.dagger.module.AppModule;
import at.devfest.app.core.dagger.module.DataModule;
import at.devfest.app.core.dagger.module.DatabaseModule;
import at.devfest.app.core.dagger.module.UtilsModule;
import at.devfest.app.receiver.BootReceiver;
import at.devfest.app.receiver.reminder.ReminderReceiver;
import at.devfest.app.ui.drawer.DrawerActivity;
import at.devfest.app.ui.home.HomeFragment;
import at.devfest.app.ui.schedule.day.ScheduleDayFragment;
import at.devfest.app.ui.schedule.pager.SchedulePagerFragment;
import at.devfest.app.ui.sessions.details.SessionDetailsActivity;
import at.devfest.app.ui.sessions.details.SessionFeedbackDialogFragment;
import at.devfest.app.ui.sessions.list.SessionsListActivity;
import at.devfest.app.ui.settings.SettingsFragment;
import at.devfest.app.ui.speakers.details.SpeakerDetailsDialogFragment;
import at.devfest.app.ui.speakers.list.SpeakersListFragment;
import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ApiModule.class, DataModule.class, DatabaseModule.class, UtilsModule.class})
public interface AppComponent {

    void inject(HomeFragment fragment);

    void inject(DrawerActivity activity);

    void inject(SchedulePagerFragment fragment);

    void inject(ScheduleDayFragment fragment);

    void inject(SessionsListActivity activity);

    void inject(SessionFeedbackDialogFragment fragment);

    void inject(SpeakersListFragment fragments);

    void inject(SessionDetailsActivity activity);

    void inject(SpeakerDetailsDialogFragment fragment);

    void inject(SettingsFragment fragment);

    void inject(BootReceiver receiver);

    void inject(ReminderReceiver receiver);

    final class Initializer {

        private Initializer() {
            throw new UnsupportedOperationException();
        }

        public static AppComponent init(DevFestApp app) {
            return DaggerAppComponent.builder()
                    .appModule(new AppModule(app))
                    .apiModule(new ApiModule())
                    .dataModule(new DataModule())
                    .databaseModule(new DatabaseModule())
                    .utilsModule(new UtilsModule())
                    .build();
        }
    }
}
