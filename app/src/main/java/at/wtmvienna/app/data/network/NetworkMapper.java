package at.wtmvienna.app.data.network;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import at.wtmvienna.app.data.app.AppMapper;
import at.wtmvienna.app.data.app.model.Room;
import at.wtmvienna.app.data.network.model.Session;
import at.wtmvienna.app.data.network.model.Speaker;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import java8.util.stream.Collectors;

import static java8.util.stream.StreamSupport.stream;

public class NetworkMapper {

    private final AppMapper appMapper;

    @Inject
    public NetworkMapper(AppMapper appMapper) {
        this.appMapper = appMapper;
    }

    public List<at.wtmvienna.app.data.app.model.Speaker> toAppSpeakers(@Nullable List<Speaker> from) {
        if (from == null) {
            return null;
        }

        return stream(from).map(speaker -> new at.wtmvienna.app.data.app.model.Speaker(
                speaker.getId(), speaker.getName(), speaker.getTitle(),
                speaker.getBio(), speaker.getWebsite(), speaker.getTwitter(),
                speaker.getGithub(), speaker.getGplus(), speaker.getXing(),
                speaker.getLinkedin(), speaker.getPhoto())
        ).collect(Collectors.toList());
    }

    public List<at.wtmvienna.app.data.app.model.Session> toAppSessions(@Nullable List<Session> from, @NonNull Map<Integer, at.wtmvienna.app.data.app.model.Speaker> speakersMap) {
        if (from == null) {
            return null;
        }

        return stream(from).map(session -> new at.wtmvienna.app.data.app.model.Session(session.getId(),
                Room.getFromId(session.getRoomId()).label,
                appMapper.toSpeakersList(session.getSpeakersId(), speakersMap),
                session.getTitle(), session.getDescription(),
                session.getStartAt(), session.getStartAt().plusMinutes(session.getDuration()),
                session.getPhoto())
        ).collect(Collectors.toList());
    }
}
