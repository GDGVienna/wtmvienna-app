package at.wtmvienna.app.data.app;

import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.threeten.bp.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

import at.wtmvienna.app.BuildConfig;
import at.wtmvienna.app.data.app.model.Session;

import static com.google.common.truth.Truth.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
public class SelectedSessionsMemoryTest {

    private final SelectedSessionsMemory memory = new SelectedSessionsMemory();

    @Test
    public void should_set_selected_sessions() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Map<LocalDateTime, Integer> map = new HashMap<>();
        map.put(now, 1);

        // When
        assertThat(memory.get(now)).isNull();
        memory.setSelectedSessions(map);

        // Then
        assertThat(memory.get(now)).isEqualTo(1);
    }

    @Test
    public void should_remove_previous_session_when_adding_a_new_one_for_the_same_slot_time() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Map<LocalDateTime, Integer> map = new HashMap<>();
        map.put(now, 1);
        memory.setSelectedSessions(map);
        Session toAdd = new Session(3, null, null, null, null, now, now.plusMinutes(30), null);

        // When
        assertThat(memory.get(now)).isEqualTo(1);
        memory.toggleSessionState(toAdd, true);

        // Then
        assertThat(memory.get(now)).isEqualTo(3);
    }
}
