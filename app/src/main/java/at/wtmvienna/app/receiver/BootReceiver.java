package at.wtmvienna.app.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import at.wtmvienna.app.receiver.reminder.SessionsReminder;

import javax.inject.Inject;

public class BootReceiver extends BroadcastReceiver {

    @Inject SessionsReminder sessionsReminder;

    public BootReceiver() {
    }

    public static void enable(Context context) {
        setActivationState(context, PackageManager.COMPONENT_ENABLED_STATE_ENABLED);
    }

    public static void disable(Context context) {
        setActivationState(context, PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
    }

    private static void setActivationState(Context context, int state) {
        ComponentName componentName = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(componentName, state, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        at.wtmvienna.app.WTMViennaApp.get(context).component().inject(this);
        sessionsReminder.enableSessionReminder();
    }
}
