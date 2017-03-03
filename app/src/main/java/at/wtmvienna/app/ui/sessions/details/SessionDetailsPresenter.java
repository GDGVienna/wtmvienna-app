package at.wtmvienna.app.ui.sessions.details;

import android.os.Bundle;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import at.wtmvienna.app.R;
import at.wtmvienna.app.data.app.model.Session;
import at.wtmvienna.app.data.database.dao.SessionsDao;
import at.wtmvienna.app.receiver.reminder.SessionsReminder;
import at.wtmvienna.app.ui.BaseActivityPresenter;

import at.wtmvienna.app.utils.Analytics;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SessionDetailsPresenter extends BaseActivityPresenter<SessionDetailsMvp.View> implements SessionDetailsMvp.Presenter {

    private final Session session;
    private final SessionsDao sessionsDao;
    private final SessionsReminder sessionsReminder;
    private final Analytics analytics;

    public SessionDetailsPresenter(SessionDetailsMvp.View view, Session session, SessionsDao sessionsDao, SessionsReminder sessionsReminder, Analytics analytics) {
        super(view);
        this.session = session;
        this.sessionsDao = sessionsDao;
        this.sessionsReminder = sessionsReminder;
        this.analytics = analytics;
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        view.bindSessionDetails(session);
        sessionsDao.isSelected(session)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isSelected -> view.updateFabButton(isSelected, false),
                        throwable -> Timber.e(throwable, "Error getting selected session state"));
        // check if session has started already
        ZoneId confZone = ZoneId.of("Europe/Vienna");
        LocalDateTime now = LocalDateTime.now(confZone);
        view.enableFeedback(now.compareTo(session.getFromTime()) > 0);
    }

    @Override
    public void onFloatingActionButtonClicked() {
        sessionsDao.getSelectedSessions(session.getFromTime())
                .map(sessions -> {
                    boolean shouldInsert = true;
                    for (Session curSession : sessions) {
                        sessionsReminder.removeSessionReminder(curSession);
                        if (curSession.getId() == session.getId()) {
                            shouldInsert = false;
                        }
                    }

                    if (shouldInsert) {
                        sessionsReminder.addSessionReminder(session);
                    }
                    sessionsDao.toggleSelectedSessionState(session, shouldInsert);
                    return shouldInsert;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(shouldInsert -> {
                    if (shouldInsert) {
                        analytics.logSelectSession(session.getId());
                        view.showSnackbarMessage(R.string.session_details_added);
                    } else {
                        view.showSnackbarMessage(R.string.session_details_removed);
                    }
                    view.updateFabButton(shouldInsert, true);
                }, throwable -> Timber.e(throwable, "Error getting selected session state"));
    }
}
