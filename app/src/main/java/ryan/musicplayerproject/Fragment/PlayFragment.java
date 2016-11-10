package ryan.musicplayerproject.Fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
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

import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import ryan.musicplayerproject.Helper;
import ryan.musicplayerproject.Model.DatabaseHelper;
import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.R;
import ryan.musicplayerproject.SongsManager;

public class PlayFragment extends Fragment implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    private View vPlay;
    //private DatabaseHelper dbHelper;


    // All variable for Fragment_Play Layout items
    private ImageButton btnPlay;
    private ImageButton btnFavourite;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnRepeatShuffle;
    private SeekBar songProgressBar;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private TextView albumTextView;
    private TextView artistTextView;
    private ImageView albumImage;
    private TextView musicTitle;

    // Media Player Class to play music
    private MediaPlayer mediaPlayer;

    // MediaMetadataRetriever to get the info from the Music
    private MediaMetadataRetriever musicRetriver;

    // Handler to update time and progress bar
    private Handler mHandler = new Handler();

    private SongsManager songsManager;
    private Helper helper;
    // Current song index in the song List
    public int currentSongIndex = 0;

    private String songTitle = "";
    private String songPath = "";
    private String songID = "";

    // Boolean variable to demonstrate shuffle and repeat state
    private boolean isShuffle = false;
    private boolean isRepeatOne = false;
    private boolean isRepeatAll = true;

    // Boolean variable to demonstrate like this song or not
    //private boolean isLike = false;

    private ArrayList<HashMap<String, Music>> songList = new ArrayList<>();
    private ArrayList<HashMap<String, Music>> favSongList = new ArrayList<>();
    private Context context;
    private int song_position;
    private String filePath = "";
    private String musicFilePath = "";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vPlay = inflater.inflate(R.layout.fragement_play, container, false);

        // Assign all the buttons and texts from Layout
        btnPlay = (ImageButton) vPlay.findViewById(R.id.btnPlay);
        btnNext = (ImageButton) vPlay.findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) vPlay.findViewById(R.id.btnPrevious);
        btnRepeatShuffle = (ImageButton) vPlay.findViewById(R.id.btnRepeatShuffle);
        btnFavourite = (ImageButton) vPlay.findViewById(R.id.btnFavourite);
        songProgressBar = (SeekBar) vPlay.findViewById(R.id.songProgressBar);
        songCurrentDurationLabel = (TextView) vPlay.findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) vPlay.findViewById(R.id.songTotalDurationLabel);
        albumTextView = (TextView) vPlay.findViewById(R.id.album);
        artistTextView = (TextView) vPlay.findViewById(R.id.artist);
        albumImage = (ImageView) vPlay.findViewById(R.id.albumImage);
        musicTitle = (TextView) vPlay.findViewById(R.id.title);

        // New SongManager and Helper Object
        // songsManager = new SongsManager(context, musicFilePath);
        songsManager = new SongsManager(getContext());
        helper = new Helper();
        // New a MediaPlayer Object
        mediaPlayer = new MediaPlayer();

        // onClickListeners for songProgressBar and MediaPlayer
        songProgressBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);

        // Get all the songs from
        songList = songsManager.getSongList();
        //dbHelper = new DatabaseHelper(getContext());


        // Click Play Button Listener
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // if there is a song playing, pause it and save the song position
                    mediaPlayer.pause();
                    song_position = mediaPlayer.getCurrentPosition();
                    btnPlay.setImageResource(R.drawable.ic_play_circle_filled_black_36dp);
                }
                // Play song from pause if there is a song which was playing before
                else if (songList != null && songList.size() > 0) {
                    mediaPlayer.seekTo(song_position);
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_36dp);
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Sorry!")
                            .setMessage("No songs Available Now.")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
            }
        });

        // Click Next Song Button Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeatOne) {
                    // Play the currentSong Again
                    playSong(currentSongIndex);
                }
                if (isRepeatAll) {
                    // Play the Whole SongList
                    if(currentSongIndex < (songList.size() - 1)) {
                        playSong(currentSongIndex + 1);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // Play the first song in the list
                        playSong(0);
                        currentSongIndex = 0;
                    }
                }
                if (isShuffle) {
                    // Random Play in SongList
                    Random random = new Random();
                    currentSongIndex = random.nextInt((songList.size() - 1) + 1);
                    playSong(currentSongIndex);
                }

                /*
                // Make sure that next song is exist
                if(currentSongIndex < (songList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // Play the first song in the list
                    playSong(0);
                    currentSongIndex = 0;
                }
                */
            }
        });

        // Click Previous Song Button Listener
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRepeatOne) {
                    // Play the currentSong Again
                    playSong(currentSongIndex);
                }
                if (isRepeatAll) {
                    // Play the Whole SongList
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1);
                        currentSongIndex = currentSongIndex - 1;
                    } else {
                        // Play the last song of the list
                        playSong(songList.size() - 1);
                        currentSongIndex = songList.size() - 1;
                    }
                }
                if (isShuffle) {
                    // Random Play in SongList
                    Random random = new Random();
                    currentSongIndex = random.nextInt((songList.size() - 1) + 1);
                    playSong(currentSongIndex);
                }
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

                /*
                else {
                    // Set repeat on  and turn shuffle off
                    isRepeat = true;
                    isShuffle = false;
                    Toast.makeText(getContext(), "Song Repeat ON", Toast.LENGTH_SHORT).show();
                    // Set shuffle off
                    btnRepeatShuffle.setImageResource(R.drawable.ic_repeat_one_black_18dp);
                }
                */
            }
        });

        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Need to load this attribute from favourite song List - database needs

                HashMap<String, Music> currentSong = songList.get(currentSongIndex);
                Long musicId = currentSong.get("songTitle").get_id();
                String musicTitle = currentSong.get("songTitle").getMusicTitle();
                String musicPath = currentSong.get("songTitle").getMusicPath();
                Boolean isLike = currentSong.get("songTitle").isLike();

                // Some Logic issues with the BtnFavourite when changing the color.

                if(isLike) {
                    isLike = false;
                    Music music = new Music(musicId, musicPath, musicTitle, isLike);
                    HashMap<String, Music> newSong = new HashMap<String, Music>();
                    newSong.put("songTitle", music);
                    songList.remove(currentSong);
                    songList.add(newSong);
                    currentSongIndex = songList.size() - 1;

                    FavouriteSongFragment favouriteSongFragment = (FavouriteSongFragment) getFragmentManager().findFragmentByTag("fav");
                    favouriteSongFragment.removeSong(music);
                    //dbHelper.removeFavMusic(music);


                    //Toast.makeText(getContext(), "Remove From Favourite", Toast.LENGTH_SHORT).show();
                    btnFavourite.setImageResource(R.drawable.ic_favorite_border_black_18dp);
                } else {
                    isLike = true;

                    Music music = new Music(musicId, musicPath, musicTitle, isLike);
                    HashMap<String, Music> newSong = new HashMap<String, Music>();
                    newSong.put("songTitle", music);
                    songList.remove(currentSong);
                    songList.add(newSong);
                    currentSongIndex = songList.size() - 1;

                    FavouriteSongFragment favouriteSongFragment = (FavouriteSongFragment) getFragmentManager().findFragmentByTag("fav");
                    favouriteSongFragment.addSong(music);
                    //dbHelper.addFavMusic(music);


                    //Toast.makeText(getContext(), "Add To Favourite", Toast.LENGTH_SHORT).show();
                    btnFavourite.setImageResource(R.drawable.ic_favourite);
                }

            }
        });

        return vPlay;
    }

    // Play song
    public void playSong(int songIndex) {
        String path;

        try {
            mediaPlayer.reset();

            if (songList.size() - 1 >= songIndex) {
                path = songList.get(songIndex).get("songTitle").getMusicPath();
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();

                // Set the button status to pause
                btnPlay.setImageResource(R.drawable.ic_pause_circle_filled_black_36dp);

                // Set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);

                // Update Progress bar
                updateProgressBar();

                // Get and show artist and album information
                getMusicInfo(path);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get Music Info - artist info, album info and coverImage info
    private void getMusicInfo(String path) {
        musicRetriver = new MediaMetadataRetriever();
        musicRetriver.setDataSource(path);

        /*
        // Set Song Name Info
        int index = getSongIndex(path);
        String songName = songList.get(index).get("songTitle");
        int replace = songName.indexOf("-");
        songName = songName.substring(replace + 2);
        musicTitle.setText("Name: " + songName);
        */

        // Set Album Info
        try {
            albumTextView.setText("Album: " + musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        } catch (Exception e) {
            // Set albumText to whitespace if some errors occur in the retrieving
            albumTextView.setText("");
        }
        // Set Artist Info
        try {
             artistTextView.setText("Artist: " + musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
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
            Should Set Default Image From Res!
            */
            e.printStackTrace();
        }
    }
    // Set a Runnable thread to update Duration of the song and the progress bar
    private Runnable updateDuration = new Runnable() {
        @Override
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Keep changing the progress bar
            int progressPercentage = helper.getProgressPercentage(currentDuration, totalDuration);
            songProgressBar.setProgress(progressPercentage);
            // Set the Total Duration to Text
            songTotalDurationLabel.setText("" + helper.milliSecondsToTime(totalDuration));
            // Set the Current Duration to Text
            songCurrentDurationLabel.setText("" + helper.milliSecondsToTime(currentDuration));

            // Run the Runnable thread every 100 milliseconds
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
                currentSongIndex = random.nextInt((songList.size() - 1) + 1);
                playSong(currentSongIndex);
            } else {
                // if shuffle and repeat is both off, play next song - Repeat All
                if (currentSongIndex < (songList.size() - 1)) {
                    playSong(currentSongIndex + 1);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play the first song in the song list
                    playSong(0);
                    currentSongIndex = 0;
                }
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    */

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
}

