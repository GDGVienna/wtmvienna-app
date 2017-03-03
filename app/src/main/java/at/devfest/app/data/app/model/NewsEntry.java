package at.devfest.app.data.app.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Value;

/**
 * Created by helmuth on 27/08/16.
 *
 * This class models the entry in Firebase for news announcements
 */

@Value
public class NewsEntry implements Parcelable {
    String title;
    String text;
    String url;

    public NewsEntry() {
        title = null;
        text = null;
        url = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.text);
        dest.writeString(this.url);
    }

    protected NewsEntry(Parcel in) {
        title = in.readString();
        text = in.readString();
        url = in.readString();
    }

    public static final Parcelable.Creator<NewsEntry> CREATOR = new Parcelable.Creator<NewsEntry>() {
        @Override
        public NewsEntry createFromParcel(Parcel source) {
            return new NewsEntry(source);
        }

        @Override
        public NewsEntry[] newArray(int size) {
            return new NewsEntry[size];
        }
    };
}
