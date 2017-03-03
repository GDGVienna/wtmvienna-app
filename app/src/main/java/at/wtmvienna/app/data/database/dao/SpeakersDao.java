package at.wtmvienna.app.data.database.dao;

import at.wtmvienna.app.data.app.AppMapper;
import at.wtmvienna.app.data.database.DbMapper;
import at.wtmvienna.app.data.database.model.Speaker;
import at.wtmvienna.app.utils.Preconditions;
import com.squareup.sqlbrite.BriteDatabase;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

@Singleton
public class SpeakersDao {

    private final BriteDatabase database;
    private final DbMapper dbMapper;
    private final AppMapper appMapper;

    @Inject
    public SpeakersDao(BriteDatabase database, DbMapper dbMapper, AppMapper appMapper) {
        this.database = database;
        this.dbMapper = dbMapper;
        this.appMapper = appMapper;
    }

    public Observable<Map<Integer, at.wtmvienna.app.data.app.model.Speaker>> getSpeakersMap() {
        return getSpeakers().map(appMapper::speakersToMap);
    }

    public Observable<List<at.wtmvienna.app.data.app.model.Speaker>> getSpeakers() {
        return database.createQuery(Speaker.TABLE, "SELECT * FROM " + Speaker.TABLE)
                .mapToList(Speaker.MAPPER)
                .first()
                .map(dbMapper::toAppSpeakers);
    }

    public void saveSpeakers(List<at.wtmvienna.app.data.app.model.Speaker> toSave) {
        Preconditions.checkNotOnMainThread();

        BriteDatabase.Transaction transaction = database.newTransaction();
        try {
            database.delete(Speaker.TABLE, null);
            for (at.wtmvienna.app.data.app.model.Speaker speaker : toSave) {
                database.insert(Speaker.TABLE, Speaker.createContentValues(dbMapper.fromAppSpeaker(speaker)));
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
    }
}
