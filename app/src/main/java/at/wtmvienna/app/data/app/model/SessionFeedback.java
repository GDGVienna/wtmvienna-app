package at.wtmvienna.app.data.app.model;

import lombok.Value;

/**
 * Created by helmuth on 23/08/16.
 *
 * This model class represents a feedback given for a session.
 */

@Value
public class SessionFeedback {
    int sessionId;
    String sessionTitle;
    float sessionRating;
    float speakerRating;
    float contentLevel;

    public SessionFeedback() {
        sessionId = 0;
        sessionTitle = null;
        sessionRating = 0f;
        speakerRating = 0f;
        contentLevel = 0f;
    }

    public SessionFeedback(Session session, float sessionRating, float speakerRating, float contentLevel) {
        this.sessionId = session.getId();
        this.sessionTitle = session.getTitle();
        this.sessionRating = sessionRating;
        this.speakerRating = speakerRating;
        this.contentLevel = contentLevel;
    }
}
