package at.wtmvienna.app.utils;

/**
 * Created by helmuth on 19/08/16.
 *
 * This interface specifies functions to log analytics events and user properties.
 */

public interface Analytics {
    public void logViewSession(int id, String title);
    public void logViewSpeaker(int id, String name);
    public void logViewSessionSpeaker(int id, String name);
    public void logViewScreen(String screen);
    public void logSelectSession(int id);
    public void setNotificationFlag(boolean value);
}
