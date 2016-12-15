package ryan.musicplayerproject.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ryan.musicplayerproject.Fragment.PlayFragment;
import ryan.musicplayerproject.MusicApplication;

/**
 * Created by EleMeNt on 18/05/16.
 */
public class NotificationBroadcast extends BroadcastReceiver {
    // Application for Get the current PlayFragment
    private MusicApplication application;
    private PlayFragment playFragment;
    private NotificationService songNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        application = (MusicApplication) context.getApplicationContext();
        // Get the same PlayFragment in Now Playing
        playFragment = application.playFragment;
        // Get the same SongNotification
        songNotification = application.songNotification;
        // if btnPlay is clicked in notification, stop or play the music
        if (intent.getAction().equals(NotificationService.NOTIFY_PLAY)) {
            playFragment.playPause();
            // if btnNext is clicked in notification, play next song
        } else if (intent.getAction().equals(NotificationService.NOTIFY_NEXT)) {
            playFragment.playNextSong();
            // if btnPrevious is clicked in notification, play previous song
        } else if (intent.getAction().equals(NotificationService.NOTIFY_DELETE)) {
            songNotification.cancelNotification();
        }
    }
}