package ryan.musicplayerproject.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLClientInfoException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by EleMeNt on 10/05/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "FavouriteMusicDB";
    public static final int DATABASE_VERSION = 1;

    // Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Music.CREATE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Music.TABLE_NAME);
        onCreate(db);
    }

    // Insert A Music To Favourite Music List
    public void addFavMusic(Music music) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Music.COLUMN_PATH, music.getMusicPath());
        values.put(Music.COLUMN_NAME, music.getMusicTitle());
        db.insert(Music.TABLE_NAME, null, values);
        db.close();
    }

    // Delete A Music From Favourite Music List
    public void removeFavMusic(Music music) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Music.TABLE_NAME,
                Music.COLUMN_ID + " = ?", new String[]{String.valueOf(music.get_id())});
    }

    public HashMap<String, Music> getAllFavMusic() {
        HashMap<String, Music> musics = new LinkedHashMap<String, Music>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Music.TABLE_NAME, null);
        // Add music to hash map for each row result
        // User cursor to get the values in the database
        if (cursor.moveToFirst()) {
            do {
                Music music = new Music(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2), true);
                musics.put(music.getMusicTitle(), music);
            } while (cursor.moveToNext());
        }
        // Close cursor
        cursor.close();
        return musics;
    }
}
