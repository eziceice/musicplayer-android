package ryan.musicplayerproject.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ryan.musicplayerproject.Model.Lyric;
import ryan.musicplayerproject.Model.LyricContent;
import ryan.musicplayerproject.Utility.Helper;
import ryan.musicplayerproject.MainActivity;
import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.MusicApplication;
import ryan.musicplayerproject.Notification.NotificationService;
import ryan.musicplayerproject.R;
import ryan.musicplayerproject.Utility.LyricProgress;
import ryan.musicplayerproject.Utility.LyricView;
import ryan.musicplayerproject.Utility.SongsManager;
/**
 * All the Lyric Function From Internet Learning
 * @link http://blog.csdn.net/iwanghang/article/details/51388896
 */
public class PlayFragment extends Fragment implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private View vPlay;
    private MusicApplication application;
    private NotificationService notification;

    // All variable for Fragment_Play Layout items
    private ImageButton btnPlay;
    private ImageButton btnFavourite;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeatShuffle;
    private ImageButton btnLyric;
    private SeekBar songProgressBar;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private TextView albumTextView;
    private TextView artistTextView;
    private ImageView albumImage;

    // Media Player Class to play music
    public MediaPlayer mediaPlayer;

    // MediaMetadataRetriever to get the info from the Music
    private MediaMetadataRetriever musicRetriver;

    // Handler to update time and progress bar
    private Handler mHandler = new Handler();

    // SongsManager and Helper
    private SongsManager songsManager;
    private Helper helper;
    // Current song index in the song List
    public int currentSongIndex = 0;
    // Current Playing Music
    private Music currentMusic;

    // Boolean variable to demonstrate shuffle and repeat state
    private boolean isShuffle = true;
    private boolean isRepeatOne = false;
    private boolean isRepeatAll = false;

    // SongList
    public ArrayList<HashMap<String, Music>> songList = new ArrayList<>();
    private int song_position;



    // Lyric Variable
    private int duration; // Duration for Find the Index of the Playing Song
    private int index = 0; // Index of the Playing Song Time
    private int currentTime;
    private LyricView lyricView; // Custom Define View to Show Lyric
    private LyricProgress lyricProgress; // Get the Lyric from the file and clean it
    private static String lyric_url = ""; // Path of the Lyric
    private ArrayList<LyricContent> lyricContents; // Lyrics in the LRC File - for each Row is an object
    private boolean showLyric = false; // Boolean Variable to decide the LyricView Show or Dismiss
    private ArrayList<Lyric> lrcList; // List contains all the Lrc path and Name


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        vPlay = inflater.inflate(R.layout.fragement_play, container, false);

        // Assign all the buttons and texts from Layout
        btnPlay = (ImageButton) vPlay.findViewById(R.id.btnPlay);
        btnNext = (ImageButton) vPlay.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) vPlay.findViewById(R.id.btnPrevious);
        btnRepeatShuffle = (ImageButton) vPlay.findViewById(R.id.btnRepeatShuffle);
        btnFavourite = (ImageButton) vPlay.findViewById(R.id.btnFavourite);
        btnLyric = (ImageButton)vPlay.findViewById(R.id.btnLyric);
        songProgressBar = (SeekBar) vPlay.findViewById(R.id.songProgressBar);
        songCurrentDurationLabel = (TextView) vPlay.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) vPlay.findViewById(R.id.songTotalDurationLabel);
        albumTextView = (TextView) vPlay.findViewById(R.id.album);
        artistTextView = (TextView) vPlay.findViewById(R.id.artist);
        albumImage = (ImageView) vPlay.findViewById(R.id.albumImage);

        // New SongManager and Helper Object
        songsManager = new SongsManager(getContext());
        helper = new Helper();

        // New a MediaPlayer Object
        mediaPlayer = new MediaPlayer();

        // Set onClickListeners for songProgressBar and MediaPlayer.OnCompletion
        songProgressBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // Get all the songs from - Same as in the SongListFragment
        songList = songsManager.getSongList();

        // Notification
        application = (MusicApplication) getContext().getApplicationContext();
        notification = application.songNotification;
        notification.updateNotification("","", null);

        // Lyric Variable
        lyricContents = new ArrayList<LyricContent>();
        lyricView =(LyricView)vPlay.findViewById(R.id.lrcShowView);
        lrcList = songsManager.getLrcList();

        // Click Play Button Listener
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPause();
            }
        });

        // Click Next Song Button Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
                // Search Matched Lyric and Init the Lyric for Next Song
                searchMatchLyric();
            }
        });

        // Click Previous Song Button Listener
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
                // Search Matched Lyric and Init the Lyric for Previous Song
                searchMatchLyric();
            }
        });

        // Click Repeat Song Button Listener
        btnRepeatShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRepeatAll) {
                    // if Repeat All is on, then turn it off and turn Repeat One on
                    isRepeatAll = false;
                    isRepeatOne = true;
                    Toast.makeText(getContext(), "One Song Repeat On", Toast.LENGTH_SHORT).show();
                    btnRepeatShuffle.setImageResource(R.drawable.ic_repeat_one_black_18dp);
                } else {
                    if (isRepeatOne){
                        // if Repeat One is on, then turn it off and turn Shuffle on
                        isRepeatOne = false;
                        isShuffle = true;
                        Toast.makeText(getContext(), "Song Shuffle On", Toast.LENGTH_SHORT).show();
                        btnRepeatShuffle.setImageResource(R.drawable.ic_shuffle_black_18dp);
                    } else {
                        // if Shuffle is on, then turn it off and turn Repeat All on
                        isShuffle = false;
                        isRepeatAll = true;
                        Toast.makeText(getContext(), "Song List Repeat On", Toast.LENGTH_SHORT).show();
                        btnRepeatShuffle.setImageResource(R.drawable.ic_repeat_black_18dp);
                    }

                }
            }
        });

        // Click Favourite Button Listener
        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to load this attribute from favourite song List - database needs
                // Get the Song Path and isLike
                String musicPath = currentMusic.getMusicPath();
                Boolean isLike = currentMusic.isLike();
                if(isLike) {
                    isLike = false;
                    int i = getSongIndex(musicPath);
                    // Change isLike in songList and currentMusic
                    songList.get(i).get("songTitle").setLike(isLike);
                    currentMusic.setLike(isLike);
                    // Pass the song to the fav Fragment List and remove it
                    FavouriteSongFragment favouriteSongFragment = (FavouriteSongFragment) getFragmentManager().findFragmentByTag("fav");
                    favouriteSongFragment.removeSong(currentMusic);
                    btnFavourite.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                } else {
                    isLike = true;
                    int i = getSongIndex(musicPath);
                    /*
                        if there is just one song in the list and user click the button again
                        add this song again and set it to Like equals true
                    */
                    if(songList.size() != 0) {
                        songList.get(i).get("songTitle").setLike(isLike);
                    } else {
                        HashMap<String, Music> currentSong = new HashMap<String, Music>();
                        currentSong.put("songTitle", currentMusic);
                        songList.add(currentSong);
                        songList.get(0).get("songTitle").setLike(isLike);
                    }
                    // set the isLike to current Music
                    currentMusic.setLike(isLike);
                    // Add the song to the fav Fragment List
                    FavouriteSongFragment favouriteSongFragment = (FavouriteSongFragment) getFragmentManager().findFragmentByTag("fav");
                    favouriteSongFragment.addSong(currentMusic);
                    btnFavourite.setImageResource(R.drawable.ic_favourite);
                }

            }
        });

        // Lyrics ClickListener Show and Dismiss
        btnLyric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Search Matched Lyric for current song and Init the Lyric
                searchMatchLyric();
                // if Lyric is shown, dismiss
                if(showLyric){
                    lyricView.setVisibility(View.INVISIBLE);
                    // Set Album Image Visible
                    albumImage.setVisibility(View.VISIBLE);
                    btnLyric.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    showLyric = false;
                }else{
                    // if Lyric is not shown, show it
                    lyricView.setVisibility(View.VISIBLE);
                    // Set Album Image Invisible
                    albumImage.setVisibility(View.INVISIBLE);
                    btnLyric.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    showLyric = true;
                }
            }
        });
        return vPlay;
    }

    // Play A Song Method
    public void playSong(int songIndex) {
        String path;
        try {
            // Rest the MediaPlayer at First
            mediaPlayer.reset();
            // If there is a song in the List
            if (songList.size() - 1 >= songIndex) {
                path = songList.get(songIndex).get("songTitle").getMusicPath();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                // Set currentMusic from the SongList
                currentMusic = songList.get(songIndex).get("songTitle");
                // Search Matched Lyric for currentSong and Init the Lyric
                searchMatchLyric();
                // Set BtnFavourite Button
                boolean isLike = currentMusic.isLike();
                if (isLike) {
                    btnFavourite.setImageResource(R.drawable.ic_favourite);
                } else {
                    btnFavourite.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                }
                // Set Current Index of the Song
                currentSongIndex = songIndex;
                // Set the button status to pause
                btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_36dp);
                // Set the button status to pause in Notification
                notification.changeBtn(false);
                // Set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);
                // Update Progress bar
                updateProgressBar();
                // Get and show artist and album information
                getMusicInfo(path);
                // Update Music Info in the Notification
                notification.updateNotification(getMusicArtist(path),getMusicAlbum(path),getMusicImage(path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get Music Info - artist info, album info and coverImage info
    public void getMusicInfo(String path) {
        musicRetriver = new MediaMetadataRetriever();
        musicRetriver.setDataSource(path);
        // Set Song Name Info
        int index = getSongIndex(path);
        String songName = songList.get(index).get("songTitle").getMusicTitle();
        int replace = songName.indexOf("-");
        songName = songName.substring(replace + 2).replace(".mp3","");
        // Set Action Bar Title for Current Song
        ((MainActivity)getActivity()).setActionBarTitle(songName);
        // Set Album Info
        try {
            albumTextView.setText(musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        } catch (Exception e) {
            // Set albumText to whitespace if some errors occur in the retrieving
            albumTextView.setText("");
        }
        // Set Artist Info
        try {
             artistTextView.setText(musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        } catch (Exception e) {
            // Set artistText to whitespace if some errors occur in the retrieving
            artistTextView.setText("");
        }
        // Set Album Image Info
        try {
            Bitmap bitmap = null;
            // Get the picture from Source
            byte[] bytes = musicRetriver.getEmbeddedPicture();
            // Change it to Image
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            albumImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            /*
               if there is no Image, set it to a default image.
            */
            albumImage.setImageResource(R.drawable.noimagefound);
        }
    }

    // Set a Runnable thread to update Duration of the song and the progress bar
    private Runnable updateDuration = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            // Rest of the Music Duration
            long restDuration = mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition();
            long currentDuration = mediaPlayer.getCurrentPosition();
            // Keep changing the progress bar
            int progressPercentage = helper.getProgressPercentage(currentDuration, totalDuration);
            songProgressBar.setProgress(progressPercentage);
            // Set the Total Duration to Text
            songTotalDurationLabel.setText("" + helper.milliSecondsToTime(restDuration));
            // Set the Current Duration to Text
            songCurrentDurationLabel.setText("" + helper.milliSecondsToTime(currentDuration));
            // Run the Runnable thread starts every 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    // Update the time in seekBar - Cover Progressbar, currentDuration and totalDuration
    public void updateProgressBar() {
        mHandler.postDelayed(updateDuration, 100);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }

    @Override
    // When the seekBar Handler is moving
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Remove the updateDuration Thread from mHandler queue
        mHandler.removeCallbacks(updateDuration);
    }

    @Override
    // When the seekBar Handler is stopped
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Remove the updateDuration Thread from mHandler queue
        mHandler.removeCallbacks(updateDuration);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = helper.progressToTime(seekBar.getProgress(), totalDuration);
        song_position = currentPosition;
        // Set to the certain seconds where the Handler is Stopped
        mediaPlayer.seekTo(currentPosition);
        // Restart the Thread Again
        updateProgressBar();
    }

    // When A Song is Completed - OnCompletion
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (songList.size() == 0) {
            // if there is no song exist in song list, do nothing
        } else {
            // check repeat is on or off
            if(isRepeatOne) {
                playSong(currentSongIndex);
                // check shuffle is on or off
            } else if(isShuffle) {
                Random random = new Random();
                playSong(random.nextInt(songList.size()));
            } else if(isRepeatAll) {
                // if shuffle and repeat is both off, play next song - Repeat All
                if (currentSongIndex < (songList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                } else {
                    // play the first song in the song list
                    playSong(0);
                }
            }
        }
    }

    // When the Player is Destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    // Get the Index of the Song when the songPath is given
    public int getSongIndex(String songPath){
        int index = 0;
        for(int i = 0; i < songList.size(); i++) {
            String path = songList.get(i).get("songTitle").getMusicPath();
            if (songPath.equals(path)) {
                index = i;
            }
        }
        return index;
    }

    // Set the SongList
    public void setSongList(ArrayList<HashMap<String, Music>> songList) {
        this.songList = songList;
    }

    // Play Previous Song
    public void playPreviousSong(){
        if (isRepeatOne) {
            // Play the currentSong Again
            playSong(currentSongIndex);
        }
        if (isRepeatAll) {
            // Play the Whole SongList
            if (currentSongIndex > 0) {
                playSong(currentSongIndex - 1);
            } else {
                // Play the last song of the list
                playSong(songList.size() - 1);
            }
        }
        if (isShuffle) {
            // Random Play in SongList
            Random random = new Random();
            playSong(random.nextInt((songList.size() - 1) + 1));
        }
    }

    // Get Current Music
    public Music getCurrentMusic() {
        return currentMusic;
    }

    // Set Current Music
    public void setCurrentMusic(Music currentMusic) {
        this.currentMusic = currentMusic;
    }

    // Play Next Song
    public void playNextSong() {
        if (isRepeatOne) {
            // Play the currentSong Again
            playSong(currentSongIndex);
        }
        if (isRepeatAll) {
            // Play the Whole SongList
            if (currentSongIndex < (songList.size() - 1)) {
                playSong(currentSongIndex + 1);

            } else {
                // Play the first song in the list
                playSong(0);
            }
        }
        if (isShuffle) {
            // Random Play in SongList
            Random random = new Random();
            playSong(random.nextInt((songList.size() - 1) + 1));
        }
    }

    // Get Music Artist Info - For Notification
    public String getMusicArtist(String path) {

        musicRetriver = new MediaMetadataRetriever();
        musicRetriver.setDataSource(path);
        String artist = "";
        // Set Artist Info
        try {
            artistTextView.setText(musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            artist = musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        } catch (Exception e) {
            // Set artistText to whitespace if some errors occur in the retrieving
            artistTextView.setText("");
            artist = "";
        }
        return artist;
    }

    // Get Music Album Info  - For Notification
    public String getMusicAlbum(String path) {
        musicRetriver = new MediaMetadataRetriever();
        musicRetriver.setDataSource(path);
        String album = "";
        // Set Album Info
        try {
            albumTextView.setText(musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            album = musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        } catch (Exception e) {
            // Set albumText to whitespace if some errors occur in the retrieving
            albumTextView.setText("");
            album = "";
        }
        return album;
    }

    // Get Music Image Info  - For Notification
    public Bitmap getMusicImage(String path) {
        musicRetriver = new MediaMetadataRetriever();
        musicRetriver.setDataSource(path);
        Bitmap bitmap = null;
        // Set Album Image Info
        try {
            // Get the picture from Source
            byte[] bytes = musicRetriver.getEmbeddedPicture();
            // Change it to Image
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            albumImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            /*
               if there is no Image, set it to a default image.
            */
            albumImage.setImageResource(R.drawable.noimagefound);
        }
        return bitmap;
    }

    // Play and Pause
    public void playPause() {
        if (currentMusic != null) {
            if (mediaPlayer.isPlaying()) {
                // if there is a song playing, pause it and save the song position
                mediaPlayer.pause();
                song_position = mediaPlayer.getCurrentPosition();
                // Change Button Status
                btnPlay.setImageResource(R.drawable.ic_play_circle_filled_black_36dp);
                notification.changeBtn(true);
            }
            // Play song from pause if there is a song which was playing before
            else {
                mediaPlayer.seekTo(song_position);
                mediaPlayer.start();
                // Change Button Status
                btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_36dp);
                notification.changeBtn(false);
            }
        } else {
            // if there is no song, Show the AlertDialog and Jump to the SongList Fragment
            AlertDialog.Builder ad = new AlertDialog.Builder(getActivity())
                    .setTitle("Sorry!")
                    .setMessage("No songs Available Now.")
                    .setCancelable(false)
                    .setPositiveButton("See Songs in SongList", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            FragmentManager manager = getFragmentManager();
                            FragmentTransaction ft = manager.beginTransaction();
                            SongListFragment songListFragment = (SongListFragment) getFragmentManager().findFragmentByTag("song");
                            PlayFragment currentFragment = (PlayFragment) getFragmentManager().findFragmentByTag("play");
                            ft.show(songListFragment);
                            ft.hide(currentFragment);
                            ft.commit();
                        }
                    });
            ad.show();
        }
    }

    // Get Current Music Name
    public String getSongName(String path) {
        if(path.equals("")) {
            return null;
        } else {
            int index = getSongIndex(path);
            String songName = songList.get(index).get("songTitle").getMusicTitle();
            int replace = songName.indexOf("-");
            songName = songName.substring(replace + 2).replace(".mp3", "");
            return songName;
        }
    }
    // Get current Music Path
    public String getCurrentMusicPath() {
        if(currentMusic == null) {
            return "";
        }
        return currentMusic.getMusicPath();
    }

    /*
     Lyric Functions
    */

    // Runnable for Lyric
    Runnable myRunnable = new Runnable(){
        @Override
        public void run() {
            int index = lyricIndex();
            lyricView.setIndex(index);
            lyricView.invalidate(); // By Calling OnDraw() Method in Lyric View
            myHandler.postDelayed(myRunnable, 100);
        }
    };
    // Handler for Lyric
    Handler myHandler = new Handler(){ // Using Handler to Synchronize the Lyric and the current Song
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    // Init the Object used in Lyric and read Lyric from the url
    public void initLyric(String url) {
        lyricProgress = new LyricProgress();
        lyricProgress.readLyric(url);
        lyricContents = lyricProgress.getLyricList();
        try{
            lyricView.setMyLyricList(lyricContents);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        myHandler.post(myRunnable);
    }

    // Find the index of the current song position
    public int lyricIndex() {
        int size = lyricContents.size();
        if(mediaPlayer.isPlaying()) {
            currentTime = mediaPlayer.getCurrentPosition();
            duration = mediaPlayer.getDuration();
        }

        if(currentTime < duration) {
            for (int i = 0; i < size; i++) {
                if (i < size - 1) {
                    if (currentTime < lyricContents.get(i).getLyricTime() && i == 0) {
                        index = i;
                        break;
                    }
                    if (currentTime > lyricContents.get(i).getLyricTime()
                            && currentTime < lyricContents.get(i + 1).getLyricTime()) {
                        index = i;
                        break;
                    }
                }
                if (i == size - 1
                        && currentTime > lyricContents.get(i).getLyricTime()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }
    // Find the Lrc which matches to the current song and Init it
    public void searchMatchLyric() {
        if(currentMusic == null) {
            lyric_url = "";
        } else {
            lyric_url = "";
            String songTitle = currentMusic.getMusicTitle().replace(".mp3", "");
            int replace = songTitle.indexOf("-");
            songTitle = songTitle.substring(replace + 1).toLowerCase();
            for (int i = 0; i < lrcList.size(); i++) {
                if (lrcList.get(i).getLyricName().contains(songTitle)){
                    lyric_url = lrcList.get(i).getLyricPath();
                }
            }
        }
        initLyric(lyric_url);
    }

    public ArrayList<HashMap<String, Music>> getSongList() {
        return songList;
    }

    public ArrayList<Lyric> getLrcList() {
        return lrcList;
    }

    public void setLrcList(ArrayList<Lyric> lrcList) {
        this.lrcList = lrcList;
    }
}

