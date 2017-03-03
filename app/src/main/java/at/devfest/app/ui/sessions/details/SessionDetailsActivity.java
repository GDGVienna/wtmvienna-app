package at.devfest.app.ui.sessions.details;

import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import at.devfest.app.DevFestApp;
import at.devfest.app.R;
import at.devfest.app.data.app.model.Session;
import at.devfest.app.data.app.model.Speaker;
import at.devfest.app.data.database.dao.SessionsDao;
import at.devfest.app.receiver.reminder.SessionsReminder;
import at.devfest.app.ui.BaseActivity;
import at.devfest.app.ui.speakers.details.SpeakerDetailsDialogFragment;
import at.devfest.app.utils.Analytics;
import at.devfest.app.utils.Animations;
import at.devfest.app.utils.App;
import at.devfest.app.utils.Strings;
import at.devfest.app.utils.Views;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import se.emilsjolander.intentbuilder.Extra;
import se.emilsjolander.intentbuilder.IntentBuilder;

@IntentBuilder
public class SessionDetailsActivity extends BaseActivity<SessionDetailsPresenter> implements SessionDetailsMvp.View {

    @Extra Session session;

    @Inject Picasso picasso;
    @Inject SessionsDao sessionsDao;
    @Inject SessionsReminder sessionsReminder;
    @Inject Analytics analytics;

    @BindView(R.id.session_details_layout) View layout;
    @BindView(R.id.session_details_toolbar) Toolbar toolbar;
    @BindView(R.id.session_details_toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.session_details_photo) ImageView photo;
    @BindView(R.id.session_details_header) ViewGroup header;
    @BindView(R.id.session_details_title) TextView title;
    @BindView(R.id.session_details_info) TextView talkInfo;
    @BindView(R.id.session_details_description_header) View descriptionHeader;
    @BindView(R.id.session_details_description) TextView description;
    @BindView(R.id.session_details_speakers_title) TextView speakersTitle;
    @BindView(R.id.session_details_speakers_container) ViewGroup speakersContainer;
    @BindView(R.id.session_details_fab) FloatingActionButton fab;
    @BindView(R.id.session_details_feedback) Button feedback;

    @Override
    protected SessionDetailsPresenter newPresenter() {
        return new SessionDetailsPresenter(this, session, sessionsDao, sessionsReminder, analytics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DevFestApp.get(this).component().inject(this);
        SessionDetailsActivityIntentBuilder.inject(getIntent(), this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void bindSessionDetails(Session session) {
        title.setText(session.getTitle());
        bindTalkInfo(session);
        if (TextUtils.isEmpty(session.getDescription())) {
            descriptionHeader.setVisibility(View.GONE);
            description.setVisibility(View.GONE);
        } else {
            descriptionHeader.setVisibility(View.VISIBLE);
            description.setVisibility(View.VISIBLE);
            description.setText(session.getDescription());
        }

        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = header.getHeight();
                if (height != 0) {
                    Views.removeOnGlobalLayoutListener(header.getViewTreeObserver(), this);
                    int toolbarHeight = height + Views.getActionBarSize(SessionDetailsActivity.this);
                    toolbar.getLayoutParams().height = toolbarHeight;
                    toolbar.requestLayout();
                    toolbarLayout.getLayoutParams().height = Math.round(2.25f * (toolbarHeight));
                    toolbarLayout.requestLayout();
                    bindHeaderPhoto(session, header.getWidth());
                }
            }
        });

        List<Speaker> speakers = session.getSpeakers();
        if (speakers != null && !speakers.isEmpty()) {
            speakersTitle.setText(getResources().getQuantityString(R.plurals.session_details_speakers, speakers.size()));
            for (Speaker speaker : speakers) {
                SessionDetailsSpeaker view = new SessionDetailsSpeaker(this, speaker, picasso);
                view.setOnClickListener(v -> openSpeakerDetails(speaker));
                speakersContainer.addView(view);
            }
        }

        fab.setOnClickListener(v -> presenter.onFloatingActionButtonClicked());
        feedback.setOnClickListener(v -> openSessionFeedbackForm());
    }

    @Override
    public void updateFabButton(boolean isSelected, boolean animate) {
        if (animate) {
            Animations.scale(fab, 0.8f, 1f, 600);
        }

        if (isSelected) {
            // As of Android Support Library 23.3.0, support vector drawables can only be loaded
            // via app:srcCompat or setImageResource().
            // fab.setImageDrawable(Views.getDrawable(this, R.drawable.session_details_like_selected));
            fab.setImageResource(R.drawable.session_details_like_selected);
        } else {
            // fab.setImageDrawable(Views.getDrawable(this, R.drawable.session_details_like_default));
            fab.setImageResource(R.drawable.session_details_like_default);
        }
    }

    @Override
    public void showSnackbarMessage(@StringRes int resId) {
        Snackbar.make(layout, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void enableFeedback(boolean show) {
        feedback.setEnabled(show);
        feedback.setAlpha(show ? 1.f : .5f);
    }

    private void bindTalkInfo(Session session) {
        analytics.logViewSession(session.getId(), session.getTitle());
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
        String day = session.getFromTime().format(DateTimeFormatter.ofPattern(getString(R.string.session_details_talk_info_date_pattern)));
        String fromTime = session.getFromTime().format(timeFormatter);
        String toTime = session.getToTime().format(timeFormatter);
        String room = session.getRoom();
        talkInfo.setText(Strings.capitalizeFirstLetter(getString(R.string.session_details_talk_info, day, fromTime, toTime, room)));
    }

    private void bindHeaderPhoto(Session session, int headerWidth) {
        String photoUrl = App.getPhotoUrl(session);
        if (!TextUtils.isEmpty(photoUrl)) {
            picasso.load(photoUrl).resize(headerWidth, 0).into(photo);
        }
    }

    private void openSpeakerDetails(Speaker speaker) {
        analytics.logViewSessionSpeaker(speaker.getId(), speaker.getName());
        SpeakerDetailsDialogFragment.show(speaker, getSupportFragmentManager());
    }

    private void openSessionFeedbackForm() {
        SessionFeedbackDialogFragment.show(session, getSupportFragmentManager());
    }
}
