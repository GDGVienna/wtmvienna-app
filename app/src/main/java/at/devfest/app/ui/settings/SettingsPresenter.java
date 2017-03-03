package at.devfest.app.ui.settings;

import android.content.Context;

import at.devfest.app.receiver.BootReceiver;
import at.devfest.app.receiver.reminder.SessionsReminder;
import at.devfest.app.ui.BasePresenter;
import at.devfest.app.utils.Analytics;
import at.devfest.app.utils.App;

public class SettingsPresenter extends BasePresenter<SettingsMvp.View> implements SettingsMvp.Presenter {

    private final Context context;
    private final SessionsReminder sessionsReminder;
    private final Analytics analytics;

    public SettingsPresenter(Context context, SettingsMvp.View view, SessionsReminder sessionsReminder, Analytics analytics) {
        super(view);
        this.context = context;
        this.sessionsReminder = sessionsReminder;
        this.analytics = analytics;
    }

    @Override
    public void onCreate() {
        view.setAppVersion(App.getVersion());
    }

    @Override
    public boolean onNotifySessionsChange(boolean checked) {
        view.setNotifySessionsCheckbox(checked);

        if (checked) {
            sessionsReminder.enableSessionReminder();
            BootReceiver.enable(context);
        } else {
            sessionsReminder.disableSessionReminder();
            BootReceiver.disable(context);
        }
        analytics.setNotificationFlag(checked);
        return true;
    }
}
