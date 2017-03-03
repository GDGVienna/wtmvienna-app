package at.wtmvienna.app.core.dagger;

import javax.inject.Singleton;

import at.wtmvienna.app.core.dagger.module.ApiModule;
import at.wtmvienna.app.core.dagger.module.AppModule;
import at.wtmvienna.app.core.dagger.module.DataModule;
import at.wtmvienna.app.core.dagger.module.DatabaseModule;
import at.wtmvienna.app.core.dagger.module.UtilsModule;
import at.wtmvienna.app.receiver.BootReceiver;
import at.wtmvienna.app.receiver.reminder.ReminderReceiver;
import at.wtmvienna.app.ui.drawer.DrawerActivity;
import at.wtmvienna.app.ui.home.HomeFragment;
import at.wtmvienna.app.ui.schedule.day.ScheduleDayFragment;
import at.wtmvienna.app.ui.schedule.pager.SchedulePagerFragment;
import at.wtmvienna.app.ui.sessions.details.SessionDetailsActivity;
import at.wtmvienna.app.ui.sessions.details.SessionFeedbackDialogFragment;
import at.wtmvienna.app.ui.sessions.list.SessionsListActivity;
import at.wtmvienna.app.ui.settings.SettingsFragment;
import at.wtmvienna.app.ui.speakers.details.SpeakerDetailsDialogFragment;
import at.wtmvienna.app.ui.speakers.list.SpeakersListFragment;
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

        public static AppComponent init(at.wtmvienna.app.WTMViennaApp app) {
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
