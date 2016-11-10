package ryan.finalassignment;

import android.content.Context;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EleMeNt on 4/05/16.
 */
public class SongsManager {

    private Context mContext;
    private String songTitle;
    private String songIDString = "0";
    private int songIDInt = 0;

    // SDCard Path
    private String MUSIC_PATH = "/data";
    private ArrayList<HashMap<String, String>> songList = new ArrayList<>();
    private ArrayList<HashMap<String, String>> searchedSongList = new ArrayList<>();

    // Constructor
    /*
    public SongsManager(Context context, String folderPath) {
        mContext = context;
        String validate = "";
        if (folderPath != null && !validate.equals(folderPath)) {
        }
    }
    */

    // Test Constructor
    public SongsManager() {

    }

    // Load all music files from sdcard and put them into the ArrayList
    public ArrayList<HashMap<String, String>> getSongList() {
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
    private void scanDirectroy(File directory) {
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

    private void searchMusicFiles(File file) {
        String musicFormat = file.getName().toLowerCase();
        if (isMusic(musicFormat)) {
            HashMap<String, String> song = new HashMap<>();

            songTitle = file.getName();

            song.put("songTitle", songTitle);
            song.put("songPath", file.getPath());
            song.put("songID", songIDString);

            songIDInt++;
            songIDString = String.valueOf(songIDInt);
            // Add a song to the SongList
            songList.add(song);
        }

    }

    // Judge the file is music or not
    private boolean isMusic(String musicFormat) {
        if (musicFormat.endsWith(".mp3")) {
            return true;
        } else {
            return false;
        }
    }

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
        return  searchedSongList;
    }


    // Test Method
    public ArrayList<HashMap<String, String>> getPlayList(){
        System.out.println("Given path:" + MUSIC_PATH);
        File home = new File(MUSIC_PATH);
        System.out.println("File length:" + home.length());
       if(home.isDirectory()){
           System.out.println("Is dir");
       }else{
           System.out.println(" Is file!");
       }
        System.out.println("Directory name:" + home.getPath());
        System.out.println("files:" + home.listFiles().length);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<String, String>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Test
                System.out.print("Song title: " + song.get("songTitle"));
                System.out.print("Song Path: " + song.get("songPath"));

                // Adding each song to SongList
                songList.add(song);
            }
        }
        // return songs list array
        return songList;
    }
    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
