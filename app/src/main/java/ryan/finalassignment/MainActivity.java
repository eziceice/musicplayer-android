package ryan.finalassignment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener{

    // All variable for activity_main Layout items
    private ImageButton btnPlay;
    private ImageButton btnFavourite;
    private ImageButton btnNext;
    private ImageButton btnPrevious;
    private ImageButton btnPlaylist;
    private ImageButton btnRepeat;
    private ImageButton btnShuffle;
    private ImageButton btnFolder_icon;
    private SeekBar songProgressBar;
    private TextView songTitleLabel;
    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;
    private TextView albumTextView;
    private TextView artistTextView;

    // Media Player Class to play music
    private MediaPlayer mediaPlayer;

    // MediaMetadataRetriever to get the info from the Music
    private MediaMetadataRetriever musicRetriver;

    // Handler to update time and progress bar
    private Handler mHandler = new Handler();

    private SongsManager songsManager;
    private Helper helper;
    // Current song index in the song List
    private int currentSongIndex = 0;

    private String songTitle = "";
    private String songPath = "";
    private String songID = "";

    // Boolean variable to demonstrate shuffle and repeat state
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    private ArrayList<HashMap<String, String>> songList = new ArrayList<>();
    private Context context;
    private int song_position;
    private String filePath = "";
    private String musicFilePath = "";

    // Constructor
    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Assign all the buttons and texts from Layout
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnNext = (ImageButton) findViewById(R.id.btnNext);
        btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
        btnRepeat = (ImageButton) findViewById(R.id.btnRepeat);
        btnShuffle = (ImageButton) findViewById(R.id.btnShuffle);
        btnFolder_icon = (ImageButton) findViewById(R.id.folder_icon);
        btnFavourite = (ImageButton) findViewById(R.id.btnFavourite);
        songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
        songTitleLabel = (TextView) findViewById(R.id.songTitle);
        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
        albumTextView = (TextView) findViewById(R.id.album);
        artistTextView = (TextView) findViewById(R.id.artist);

        // New SongManager and Helper Object
        // songsManager = new SongsManager(context, musicFilePath);
        songsManager = new SongsManager();
        helper = new Helper();
        // New a Mediaplayer Object
        mediaPlayer = new MediaPlayer();

        // onClickListeners for songProgressBar and MediaPlayer
        songProgressBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);


        // Test song
        songList = songsManager.getPlayList();
        // Get all the songs from
        //songList = songsManager.getSongList();

        // Click Play Button Listener
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    // if there is a song playing, pause it and save the song position
                    mediaPlayer.pause();
                    song_position = mediaPlayer.getCurrentPosition();
                    btnPlay.setImageResource(R.drawable.ic_av_play_circle_fill);
                }
                // Play song from pause if there is a song which was playing before
                else if (songList != null && songList.size() > 0) {
                    mediaPlayer.seekTo(song_position);
                    mediaPlayer.start();
                    btnPlay.setImageResource(R.drawable.ic_av_play_circle_fill);
                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Sorry!")
                            .setMessage("No songs Available Now.")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setIcon(R.drawable.ic_hardware_headset)
                            .show();
                }
            }
        });

        // Click Next Song Button Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    // Make sure that next song is exist
                    if(currentSongIndex < (songList.size() - 1)) {
                        playSong(currentSongIndex + 1, songTitle, songPath);
                        currentSongIndex = currentSongIndex + 1;
                    } else {
                        // Play the first song in the list
                        playSong(0, songTitle, songPath);
                        currentSongIndex = 0;
                    }
                }
            }
        });

        // Click Previous Song Button Listener
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()) {
                    if (currentSongIndex > 0) {
                        playSong(currentSongIndex - 1, songTitle, songPath);
                        currentSongIndex = currentSongIndex - 1;
                    } else {
                        // Play the last song of the list
                        playSong(songList.size() - 1, songTitle, songPath);
                        currentSongIndex = songList.size() - 1;
                    }
                }
            }
        });

        // Click Repeat Song Button Listener
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRepeat) {
                    // if Repeat is on, then turn it off
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Song Repeat OFF", Toast.LENGTH_SHORT).show();
                    btnRepeat.setImageResource(R.drawable.ic_av_repeat);
                } else {
                    // Set repeat on
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Song Repeat ON", Toast.LENGTH_SHORT).show();
                    // Set shuffle off
                    isShuffle = false;
                    btnRepeat.setImageResource(R.drawable.ic_av_repeat);
                    btnShuffle.setImageResource(R.drawable.ic_av_shuffle);
                }

            }
        });

        // Click Shuffle Song Button Listener
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShuffle) {
                    // if Shuffle is on, then turn it off
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Song Shuffle OFF", Toast.LENGTH_SHORT).show();
                    btnShuffle.setImageResource(R.drawable.ic_av_shuffle);
                } else {
                    // Set shuffle on
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Song Shuffle ON", Toast.LENGTH_SHORT).show();
                    // Set repeat off
                    isRepeat = false;
                    btnShuffle.setImageResource(R.drawable.ic_av_shuffle);
                    btnRepeat.setImageResource(R.drawable.ic_av_repeat);
                }
            }
        });

        btnFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // Play song
    public void playSong(int songIndex, String songTitle, String songPath) {
        String path;

        try {
            mediaPlayer.reset();

            if (songList.size() - 1 >= songIndex) {
                path = songList.get(songIndex).get("songPath");
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                // Set the currentPlaySong title
                songTitleLabel.setText(songList.get(songIndex).get("songTitle"));

                // Set the button status to pause
                btnPlay.setImageResource(R.drawable.ic_av_pause_circle_fill);

                // Set Progress bar values
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);

                // Update Progress bar
                updateProgressBar();

                // Get and show artist and album infomation
                getMusicInfo(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get Music Info - artist info and album info
    private void getMusicInfo(String path) {
        musicRetriver = new MediaMetadataRetriever();
        musicRetriver.setDataSource(path);
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

            // Run the Runable every 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };


    // Update the time in seekBar - Cover Progressbar, currentDuration and totalDuration
    public void updateProgressBar() {
        mHandler.postDelayed(updateDuration, 100);
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
    }

    // When the seekBar Handler is moving
    public void onStartTrackingTouch(SeekBar seekBar) {
        // Remove the updateDuration Thread from mHandler queue
        mHandler.removeCallbacks(updateDuration);
    }

    // When the seekBar Handler is stopped
    public void onStopTrackingTouch(SeekBar seekBar) {
        // Remove the updateDuration Thread from mHandler queue
        mHandler.removeCallbacks(updateDuration);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = helper.progressToTime(seekBar.getProgress(), totalDuration);

        // Set to the certain seconds where the Handler is Stopped
        mediaPlayer.seekTo(currentPosition);
        // Restart the Thread Again
        updateProgressBar();
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        if (songList.size() == 0) {
            // if there is no song exist in song list, do nothing
        } else {
            // check repeat is on or off
            if(isRepeat) {
                playSong(currentSongIndex, songTitle, songPath);
                // check shuffle is on or off
            } else if(isShuffle) {
                Random random = new Random();
                currentSongIndex = random.nextInt((songList.size() - 1) + 1);
                playSong(currentSongIndex,songTitle,songPath);
            } else {
                // if shuffle and repeat is both off, play next song
                if (currentSongIndex < (songList.size() - 1)) {
                    playSong(currentSongIndex + 1, songTitle, songPath);
                    currentSongIndex = currentSongIndex + 1;
                } else {
                    // play the 1st song in the song list
                    playSong(0, songTitle, songPath);
                    currentSongIndex = 0;
                }
            }
        }
    }

    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
    }

    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        finish();
    }
}
