package at.wtmvienna.app.data.app.model;

import android.support.annotation.NonNull;

import at.wtmvienna.app.R;

public enum Room {

    NONE(0, "", R.color.other_background),
    ROOM_1(1, "Presentation", R.color.presentation_background),
    ROOM_2(2, "Workshop", R.color.workshop_background),
    ROOM_3(3, "Kids' Workshop", R.color.kids_background);

    public final int id;
    public final String label;
    public final int color;

    Room(int id, String label, int color) {
        this.id = id;
        this.label = label;
        this.color = color;
    }

    public static Room getFromId(int id) {
        for (Room room : Room.values()) {
            if (room.id == id) {
                return room;
            }
        }
        return NONE;
    }

    public static Room getFromLabel(@NonNull String label) {
        for (Room room : Room.values()) {
            if (label.equals(room.label)) {
                return room;
            }
        }
        return NONE;
    }
}
