package at.wtmvienna.app.data.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import at.wtmvienna.app.core.moshi.LocalDateTimeAdapter;
import at.wtmvienna.app.data.app.AppMapper;
import at.wtmvienna.app.data.app.model.Room;
import at.wtmvienna.app.data.database.model.Session;
import at.wtmvienna.app.data.database.model.Speaker;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.temporal.ChronoUnit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import java8.util.stream.Collectors;
import timber.log.Timber;

import static java8.util.stream.StreamSupport.stream;

public class DbMapper {

    private final AppMapper appMapper;
    private final LocalDateTimeAdapter localDateTimeAdapter;
    private final JsonAdapter<List<Integer>> intListAdapter;

    @Inject
    public DbMapper(Moshi moshi, AppMapper appMapper, LocalDateTimeAdapter localDateTimeAdapter) {
        this.appMapper = appMapper;
        this.localDateTimeAdapter = localDateTimeAdapter;
        this.intListAdapter = moshi.adapter(Types.newParameterizedType(List.class, Integer.class));
    }

    public List<at.wtmvienna.app.data.app.model.Session> toAppSessions(@NonNull List<Session> from, @NonNull Map<Integer, at.wtmvienna.app.data.app.model.Speaker> speakersMap) {
        return stream(from).map(session -> {
            LocalDateTime fromTime = localDateTimeAdapter.fromText(session.startAt);
            return new at.wtmvienna.app.data.app.model.Session(session.id, Room.getFromId(session.roomId).label,
                    appMapper.toSpeakersList(deserialize(session.speakersIds), speakersMap),
                    session.title, session.description, fromTime, fromTime.plusMinutes(session.duration),
                    session.photo);
        }).collect(Collectors.toList());
    }

    public Session fromAppSession(@Nullable at.wtmvienna.app.data.app.model.Session from) {
        if (from == null) {
            return null;
        }

        return new Session(from.getId(), localDateTimeAdapter.toText(from.getFromTime()),
                (int) ChronoUnit.MINUTES.between(from.getFromTime(), from.getToTime()),
                Room.getFromLabel(from.getRoom()).id,
                serialize(appMapper.toSpeakersIds(from.getSpeakers())), from.getTitle(), from.getDescription(),
                from.getPhoto());
    }

    public Speaker fromAppSpeaker(@Nullable at.wtmvienna.app.data.app.model.Speaker from) {
        if (from == null) {
            return null;
        }

        return new Speaker(from.getId(), from.getName(), from.getTitle(), from.getBio(),
                from.getWebsite(), from.getTwitter(), from.getGithub(), from.getGplus(),
                from.getXing(), from.getLinkedin(), from.getPhoto());
    }

    public List<at.wtmvienna.app.data.app.model.Speaker> toAppSpeakers(@Nullable List<Speaker> from) {
        if (from == null) {
            return null;
        }

        return stream(from).map(speaker -> new at.wtmvienna.app.data.app.model.Speaker(speaker.id,
                speaker.name, speaker.title, speaker.bio, speaker.website, speaker.twitter,
                speaker.github, speaker.gplus, speaker.xing, speaker.linkedin, speaker.photo)
        ).collect(Collectors.toList());
    }

    private String serialize(@Nullable List<Integer> toSerialize) {
        String result = null;
        if (toSerialize != null) {
            result = intListAdapter.toJson(toSerialize);
        }
        return result;
    }

    private List<Integer> deserialize(@Nullable String toDeserialize) {
        List<Integer> result = null;
        if (toDeserialize != null) {
            try {
                result = intListAdapter.fromJson(toDeserialize);
            } catch (IOException e) {
                Timber.e(e, "Error getting speakersIds for String: %s", toDeserialize);
            }
        }
        return result;
    }
}
