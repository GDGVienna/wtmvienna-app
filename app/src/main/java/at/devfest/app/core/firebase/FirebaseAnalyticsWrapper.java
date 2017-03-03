package at.devfest.app.core.firebase;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import at.devfest.app.utils.Analytics;

/**
 * Created by helmuth on 19/08/16.
 */

public class FirebaseAnalyticsWrapper implements Analytics {
    private FirebaseAnalytics firebaseAnalytics;

    public FirebaseAnalyticsWrapper(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }


    void logViewItem(int id, String name, String category) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(id));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    @Override
    public void logViewSession(int id, String title) {
        logViewItem(id, title, "session");
    }

    @Override
    public void logViewSpeaker(int id, String name) {
        logViewItem(id, name, "speaker");
    }

    @Override
    public void logViewSessionSpeaker(int id, String name) {
        logViewItem(id, name, "session_speaker");
    }

    @Override
    public void logViewScreen(String screen) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, screen);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM_LIST, bundle);
    }

    @Override
    public void logSelectSession(int id) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "presentation");
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(id));
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Override
    public void setNotificationFlag(boolean val) {
        firebaseAnalytics.setUserProperty("notify_sessions", Boolean.toString(val));
    }
}
