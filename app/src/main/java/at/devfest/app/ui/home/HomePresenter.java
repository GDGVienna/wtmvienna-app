package at.devfest.app.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;

import java.util.List;

import at.devfest.app.R;
import at.devfest.app.data.app.DataProvider;
import at.devfest.app.data.app.model.NewsEntry;
import at.devfest.app.data.app.model.Schedule;
import at.devfest.app.data.app.model.ScheduleDay;
import at.devfest.app.data.app.model.ScheduleSlot;
import at.devfest.app.ui.BaseFragmentPresenter;
import at.devfest.app.utils.Analytics;
import icepick.State;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by helmuth on 25/08/16.
 */

public class HomePresenter extends BaseFragmentPresenter<HomeMvp.View> implements HomeMvp.Presenter {
    private DatabaseReference dbRef;
    private Analytics analytics;
    private DataProvider dataProvider;
    private DatabaseReference newsRef = null;
    private ValueEventListener firebaseListener = null;
    private Subscription scheduleSubscription = null;
    private boolean isDisplayed = false;
    private boolean firebaseLoaded = false;
    private boolean sessionsLoaded = false;

    @State Schedule schedule;
    @State NewsEntry newsEntry;

    public HomePresenter(HomeMvp.View view, DatabaseReference dbRef, Analytics analytics, DataProvider dataProvider) {
        super(view);
        this.dbRef = dbRef;
        this.analytics = analytics;
        this.dataProvider = dataProvider;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionsLoaded = (schedule != null);
        firebaseLoaded = (newsEntry != null || schedule != null);
        setVisibility();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (schedule != null) {
            // load currently running sessions
            loadUpcoming();
        }
        if (newsEntry != null) {
            // display current Firebase newsEntry
            loadNewsEntry();
        }
        setVisibility();
        // watch for Firebase data
        firebaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                newsEntry = dataSnapshot.getValue(NewsEntry.class);
                loadNewsEntry();
                setVisibility();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                view.hideAnnouncement();
                firebaseLoaded = true;
                setVisibility();
            }
        };
        if (newsRef == null) {
            newsRef = dbRef.child("news");
        }
        newsRef.addValueEventListener(firebaseListener);
        // check what session is coming next
        scheduleSubscription = dataProvider.getSchedule()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(scheduleDays -> schedule = scheduleDays,
                        throwable -> {sessionsLoaded = true; setVisibility(); Timber.e(throwable, "Error getting schedule");},
                        () -> {
                            if (schedule == null) {
                                // view.displayLoadingError();
                            } else {
                                loadUpcoming();
                            }
                            sessionsLoaded = true;
                            setVisibility();
                        });
    }

    private void loadNewsEntry() {
        if (newsEntry != null && newsEntry.getTitle() != null && newsEntry.getText() != null) {
            view.updateAnnouncement(newsEntry.getTitle(), newsEntry.getText());
        }
        else {
            view.hideAnnouncement();
        }
        firebaseLoaded = true;
    }

    private void loadUpcoming() {
        ZoneId confZone = ZoneId.of("Europe/Vienna");
        LocalDateTime now = LocalDateTime.now(confZone);
        LocalDate today = LocalDate.now(confZone);
        boolean isBefore = false;
        boolean isLastDay = false;
        ScheduleDay todayScheduleDay = null;
        ScheduleDay day;
        List<ScheduleSlot> slots;
        ScheduleSlot currentSlot = null;
        ScheduleSlot nextSlot = null;
        int cnt = schedule.size();
        int i;
        for (i=0; i<cnt; i++) {
            day = schedule.get(i);
            if (today.compareTo(day.getDay()) < 0) {
                isBefore = true;
            }
            if (today.isEqual(day.getDay())) {
                todayScheduleDay = day;
                isLastDay = (i == cnt -1);
                break;
            }
        }
        if (todayScheduleDay == null) {
            if (isBefore) {
                // days before the conference
                view.setComingNext(R.string.home_conference_before_title, R.string.home_conference_before);
            }
            else {
                // days after the conference
                view.setComingNext(R.string.home_conference_after_title, R.string.home_conference_after);
            }
        }
        else {
            slots = todayScheduleDay.getSlots();
            cnt = slots.size();
            for (i = 0; i<cnt; i++) {
                ScheduleSlot slot = slots.get(i);
                if (now.compareTo(slot.getTime()) < 0) {
                    nextSlot = slot;
                    break;
                }
                currentSlot = slot;
            }
            if (nextSlot == null) {
                // at or after the last slot
                if (isLastDay) {
                    // last day, nothing coming
                    view.setComingNext(R.string.home_conference_last_day_title, R.string.home_conference_last_day);
                }
                else {
                    // tomorrow is another session
                    view.setComingNext(R.string.home_conference_next_day_title, R.string.home_conference_next_day);
                }
            }
            else if (currentSlot == null) {
                // no currentSlot: conf day did not start yet
                view.setComingNext(R.string.home_session_next_title, nextSlot.getSessions());
            }
            else {
                LocalDateTime nextStart = nextSlot.getTime();
                if (now.plusMinutes(10).compareTo(nextStart) > 0) {
                    // starting soon
                    view.setComingNext(R.string.home_session_next_title, nextSlot.getSessions());
                }
                else {
                    // starting in more than 15 minutes
                    view.setComingNext(R.string.home_session_current_title, currentSlot.getSessions());
                }
            }
        }
    }

    private void setVisibility() {
        if (!isDisplayed && firebaseLoaded && sessionsLoaded) {
            isDisplayed = true;
        }
        view.setIsLoading(!isDisplayed);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (firebaseListener != null) {
            if (newsRef == null) {
                newsRef = dbRef.child("news");
            }
            newsRef.removeEventListener(firebaseListener);
            firebaseListener = null;
        }
        if (scheduleSubscription != null) {
            scheduleSubscription.unsubscribe();
            scheduleSubscription = null;
        }
    }

}
