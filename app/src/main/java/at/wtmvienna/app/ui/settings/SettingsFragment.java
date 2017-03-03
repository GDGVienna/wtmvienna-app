package at.wtmvienna.app.ui.settings;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import at.wtmvienna.app.R;
import at.wtmvienna.app.receiver.reminder.SessionsReminder;
import at.wtmvienna.app.utils.Analytics;
import at.wtmvienna.app.utils.Intents;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragmentCompat implements SettingsMvp.View {

    @Inject SessionsReminder sessionsReminder;
    @Inject Analytics analytics;

    private SettingsPresenter presenter;
    private CheckBoxPreference notifySessions;
    private Preference appVersion;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        initPreferences();
        initPresenter();
        notifySessions.setOnPreferenceChangeListener((preference, newValue) ->
                presenter.onNotifySessionsChange((Boolean) newValue));
    }

    @Override
    public void setNotifySessionsCheckbox(boolean checked) {
        notifySessions.setChecked(checked);
    }

    @Override
    public void setAppVersion(CharSequence version) {
        appVersion.setSummary(version);
    }

    private void initPreferences() {
        addPreferencesFromResource(R.xml.settings);
        notifySessions = findPreference(R.string.settings_notify_key);
        appVersion = findPreference(R.string.settings_version_key);
        initPreferenceLink(R.string.settings_conf_key);
        initPreferenceLink(R.string.settings_sources_key);
        initPreferenceLink(R.string.settings_developer_key);
        initPreferenceLink(R.string.settings_hacker_key);
    }

    private void initPresenter() {
        at.wtmvienna.app.WTMViennaApp.get(getContext()).component().inject(this);
        presenter = new SettingsPresenter(getContext(), this, sessionsReminder, analytics);
        presenter.onCreate();
    }

    private <T extends Preference> T findPreference(@StringRes int resId) {
        return (T) findPreference(getString(resId));
    }

    private void initPreferenceLink(@StringRes int resId) {
        findPreference(resId).setOnPreferenceClickListener(preference -> {
            Intents.startExternalUrl(getActivity(), preference.getSummary().toString());
            return true;
        });
    }
}
