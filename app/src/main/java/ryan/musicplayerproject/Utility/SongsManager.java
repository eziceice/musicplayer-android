package ryan.musicplayerproject.Utility;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ryan.musicplayerproject.Model.Lyric;
import ryan.musicplayerproject.Model.LyricContent;
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
    private String MUSIC_PATH;
    private String LRC_PATH;
    private ArrayList<HashMap<String, Music>> songList = new ArrayList<>();
    private ArrayList<Lyric> lrcList = new ArrayList<>();

    // Constructor
    public SongsManager(Context context) {
        mContext = context;
        MUSIC_PATH = Environment.getExternalStorageDirectory().getPath();
        LRC_PATH = Environment.getExternalStorageDirectory().getPath();
    }

    // Test Constructor
    public SongsManager(Context context,String musicPath, String lrcPath) {
        mContext = context;
        MUSIC_PATH = musicPath;
        LRC_PATH = lrcPath;
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
                        scanMusicDirectory(files);
                    } else {
                        searchMusicFiles(files);
                    }
                }
            }
        }
        return songList;
    }

    // Scan all folders from root music directory for music files
    public void scanMusicDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File files : listFiles) {
                    if (files.isDirectory()) {
                        scanMusicDirectory(files);
                    } else {
                        searchMusicFiles(files);
                    }
                }
            }
        }
    }

    // Search Music Files and add to the songList
    public void searchMusicFiles(File file) {
        String musicFormat = file.getName().toLowerCase();
        if (isMusic(musicFormat)) {
            HashMap<String, Music> song = new HashMap<>();
            songTitle = file.getName();
            Music music = new Music(Long.parseLong(songIDString), file.getPath(), songTitle, false);
            song.put("songTitle", music);
            songIDInt++;
            songIDString = String.valueOf(songIDInt);
            // Add a song to the SongList
            songList.add(song);
        }

    }

    // Decide the file is music or not
    public boolean isMusic(String musicFormat) {
        if (musicFormat.endsWith(".mp3")) {
            return true;
        } else {
            return false;
        }
    }

    // Decide the file is lrc or not
    public boolean isLrc(String lrcFormat) {
        if (lrcFormat.endsWith(".lrc")) {
            return true;
        } else {
            return false;
        }
    }

    // Load all lyric from sdcard and put them into the ArrayList
    public ArrayList<Lyric> getLrcList() {
        if (LRC_PATH != null) {
            File file = new File(LRC_PATH);
            File[] listFiles = file.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for(File files: listFiles) {
                    if (file.isDirectory()) {
                        scanLyrDirectory(files);
                    } else {
                        searchLrcFiles(files);
                    }
                }
            }
        }
        return lrcList;
    }

    // Search the File is Lrc or not
    public void searchLrcFiles(File file) {
        String lrcFormat = file.getName().toLowerCase();
        if (isLrc(lrcFormat)) {
            Lyric lyric = new Lyric(file.getName().toLowerCase(),file.getPath());
            lrcList.add(lyric);
        }
    }

    // Scan all folders from root lrc directory for lrc files
    public void scanLyrDirectory(File directory) {
        if (directory != null) {
            File[] listFiles = directory.listFiles();
            if (listFiles != null && listFiles.length > 0) {
                for (File files : listFiles) {
                    if (files.isDirectory()) {
                        scanLyrDirectory(files);
                    } else {
                        searchLrcFiles(files);
                    }
                }
            }
        }
    }

    public String getLRC_PATH() {
        return LRC_PATH;
    }

    public void setLRC_PATH(String LRC_PATH) {
        this.LRC_PATH = LRC_PATH;
    }

    public String getMUSIC_PATH() {
        return MUSIC_PATH;
    }

    public void setMUSIC_PATH(String MUSIC_PATH) {
        this.MUSIC_PATH = MUSIC_PATH;
    }

    public void setSongList(ArrayList<HashMap<String, Music>> songList) {
        this.songList = songList;
    }

    public void setLrcList(ArrayList<Lyric> lrcList) {
        this.lrcList = lrcList;
    }
}
