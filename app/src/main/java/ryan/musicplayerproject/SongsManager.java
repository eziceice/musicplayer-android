package ryan.musicplayerproject;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.os.EnvironmentCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import ryan.musicplayerproject.Model.Music;

/**
 * Created by EleMeNt on 4/05/16.
 */
public class SongsManager{

    private Context mContext;
    private String songTitle;
    private String songIDString = "0";
    private int songIDInt = 0;

    // SDCard Path
    private String MUSIC_PATH = Environment.getExternalStorageDirectory().getPath();
    private ArrayList<HashMap<String, Music>> songList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> searchedSongList = new ArrayList<>();

    // Constructor - For Folder Interface
    /*
    public SongsManager(Context context, String folderPath) {
        mContext = context;
        String validate = "";
        if (folderPath != null && !validate.equals(folderPath)) {
        }
    }
    */

    // Constructor
    public SongsManager(Context context) {
        mContext = context;

    }

    // Load all music files from sdcard and put them into the ArrayList
    public ArrayList<HashMap<String, Music>> getSongList() {
        if (MUSIC_PATH != null) {
            File file = new File(MUSIC_PATH);

            if (songList != null) {
                songList.clear();
            }

            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for(File files: listFiles) {
                    if (file.isDirectory()) {
                        scanDirectroy(files);
                    } else {
                        searchMusicFiles(files);
                    }
                }
            }
        }
        return songList;
    }

    // Scan all folders from root music directory for music files
    public void scanDirectroy(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File files : listFiles) {
                    if (files.isDirectory()) {
                        scanDirectroy(files);
                    } else {
                        searchMusicFiles(files);
                    }
                }
            }
        }
    }

    public void searchMusicFiles(File file) {
        String musicFormat = file.getName().toLowerCase();
        if (isMusic(musicFormat)) {
            HashMap<String, Music> song = new HashMap<>();

            songTitle = file.getName();

            Music music = new Music(Long.parseLong(songIDString), file.getPath(), songTitle, false);
            song.put("songTitle", music);

            /*
            song.put("songTitle", songTitle);
            song.put("songPath", file.getPath());
            song.put("songID", songIDString);
            */

            songIDInt++;
            songIDString = String.valueOf(songIDInt);
            // Add a song to the SongList
            songList.add(song);
            //Toast.makeText(mContext,song.get("songTitle") + " has Been Added.",Toast.LENGTH_SHORT).show();
        }

    }

    // Decide the ic_file is music or not
    public boolean isMusic(String musicFormat) {
        if (musicFormat.endsWith(".mp3")) {
            return true;
        } else {
            return false;
        }
    }

    /*
    // Search a song from songList by a search String
    public ArrayList<HashMap<String, String>> searchSong(String search) {
        search = search.toLowerCase();

        // if the search string is empty, show all the added songs
        if (search.length() == 0) {

            // if there already exist a searched list, clear it.
            if (searchedSongList != null) {
                searchedSongList.clear();
            }
            searchedSongList = songList;
        }

        else {
            if (searchedSongList != null) {
                searchedSongList.clear();

                for(HashMap<String,String> songs: songList) {
                    String songTitle = songs.get("songTitle");
                    if (songTitle.toLowerCase().contains(search)) {
                        searchedSongList.add(songs);
                    }
                }
            }
        }
        return searchedSongList;
    }
    */
}
