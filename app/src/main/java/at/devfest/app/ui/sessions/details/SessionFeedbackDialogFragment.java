package at.devfest.app.ui.sessions.details;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import javax.inject.Inject;

import at.devfest.app.DevFestApp;
import at.devfest.app.R;
import at.devfest.app.data.app.model.Session;
import at.devfest.app.data.app.model.SessionFeedback;
import at.devfest.app.utils.Analytics;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by helmuth on 21/08/16.
 */

public class SessionFeedbackDialogFragment extends AppCompatDialogFragment {

    @Inject Analytics analytics;
    @Inject FirebaseInstanceId instanceId;
    @Inject DatabaseReference dbRef;

    @BindView(R.id.session_feedback_title) TextView title;
    @BindView(R.id.session_feedback_talk) RatingBar ratingTalk;
    @BindView(R.id.session_feedback_speaker) RatingBar ratingSpeaker;
    @BindView(R.id.session_feedback_content) RatingBar ratingContent;

    private Unbinder unbinder;

    private static final String EXTRA_SESSION = "session";

    private Session session;

    public static void show(@NonNull Session session, @NonNull FragmentManager fm) {
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_SESSION, session);
        SessionFeedbackDialogFragment fragment = new SessionFeedbackDialogFragment();
        fragment.setArguments(args);
        fragment.show(fm, SessionFeedbackDialogFragment.class.getSimpleName());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DevFestApp.get(getContext()).component().inject(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(getContext()).inflate(R.layout.session_feedback, null);
        unbinder = ButterKnife.bind(this, view);
        bindSession(getArguments().getParcelable(EXTRA_SESSION));
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .setPositiveButton(R.string.session_feedback_submit, (dialog, which) -> saveFeedback())
                .setNegativeButton(R.string.session_feedback_cancel, (dialog, which) -> dismiss())
                .create();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();
    }

    public void saveFeedback() {
        // get application identifier
        String id = instanceId.getId();
        // get floats for feedback
        float sessionRating = ratingTalk.getRating();
        float speakerRating = ratingSpeaker.getRating();
        float contentLevel = ratingContent.getRating();
        // create model object
        SessionFeedback feedback = new SessionFeedback(session, sessionRating, speakerRating, contentLevel);
        // write into the reference object
        dbRef.child("feedback")
                .child(id)
                .child(String.valueOf(session.getId()))
                .setValue(feedback, (err, ref) -> {
                    if (err != null) {
                        Timber.e("Firebase error: %s", err.getMessage());
                    }
                });
    }

    public void bindSession(Session session) {
        this.session = session;
        // analytics.logViewSpeaker(speaker.getId(), speaker.getName());
        title.setText(session.getTitle());
        // load data from firebase
        String id = instanceId.getId();
        dbRef.child("feedback")
                .child(id)
                .child(String.valueOf(session.getId()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SessionFeedback feedback = dataSnapshot.getValue(SessionFeedback.class);
                if (feedback != null) {
                    // write the values into the form
                    ratingTalk.setRating(feedback.getSessionRating());
                    ratingSpeaker.setRating(feedback.getSpeakerRating());
                    ratingContent.setRating(feedback.getContentLevel());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}
