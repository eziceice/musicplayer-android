package ryan.musicplayerproject.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by EleMeNt on 10/05/16.
 */

// Need To Implement the Database
public class Music implements Parcelable{
    private String musicPath;
    private long _id;
    private String musicTitle;
    private boolean isLike;

    // Database constants
    public static final String TABLE_NAME = "musicPath";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_FAV = "favourite";

    // Table create Statement
    public static final String CREATE_STATEMENT =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_PATH + " TEXT NOT NULL, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_FAV + " INTEGER NOT NULL" +
                    ")";

    // Constructor
    public Music(long id, String musicPath, String musicTitle, boolean isLike) {
        this._id = id;
        this.musicPath = musicPath;
        this.musicTitle = musicTitle;
        this.isLike = isLike;
    }

    /*
    All Getter And Setter
    */
    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public void setMusicTitle(String musicTitle) {
        this.musicTitle = musicTitle;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    protected Music(Parcel in) {
        musicPath = in.readString();
        _id = in.readLong();
        musicTitle = in.readString();
        isLike = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(musicPath);
        dest.writeLong(_id);
        dest.writeString(musicTitle);
        dest.writeByte((byte) (isLike ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Music> CREATOR = new Parcelable.Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
}