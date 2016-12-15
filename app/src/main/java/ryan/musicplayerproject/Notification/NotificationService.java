package ryan.musicplayerproject.Notification;

/**
 * Created by EleMeNt on 18/05/16.
 */
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import ryan.musicplayerproject.MusicApplication;
import ryan.musicplayerproject.R;

public class NotificationService extends Service{

    private MusicApplication application;
    private Notification notification;
    private Context context;
    // Notification Service
    // public static final String NOTIFY_PREVIOUS = "com.notification.previous";
    public static final String NOTIFY_DELETE = "com.notification.delete";
    public static final String NOTIFY_PLAY = "com.notification.play";
    public static final String NOTIFY_NEXT = "com.notification.next";
    private static final int NOTIFICATION_ID = 1;

    public void customSimpleNotification(Context context) {
        // Create new RemoteViews for Notification
        RemoteViews simpleView = new RemoteViews(context.getPackageName(), R.layout.notification);
        this.context = context;
        application = (MusicApplication) context.getApplicationContext();
        // Set Notification Icon and Title
        notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("").build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.contentView = simpleView;
        // Set Listener
        setListeners(simpleView, context);
        // Notify the created Notification in Notification Bar
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, notification);
        // Get Notification to the Application
        application.songNotification = this;
    }

    // Set clickListener for Notification layout button
    private static void setListeners(RemoteViews view, Context context) {
        //Intent previous = new Intent(NOTIFY_PREVIOUS);
        Intent delete = new Intent(NOTIFY_DELETE);
        Intent next = new Intent(NOTIFY_NEXT);
        Intent play = new Intent(NOTIFY_PLAY);

        /*
        // Previous Song button
        PendingIntent pPrevious = PendingIntent.getBroadcast(context, 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);
        */
        // Delete Notification button
        PendingIntent pDelete = PendingIntent.getBroadcast(context, 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnDelete, pDelete);

        // Next Song Button
        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);
        // Play or Pause button
        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);
    }

    // Update the SongName and AlbumName in Notification when changing song
    public void updateNotification(String artist, String album, Bitmap bitmap){
        notification.contentView.setTextViewText(R.id.textSongName, artist);
        notification.contentView.setTextViewText(R.id.textAlbumName, album);
        if (bitmap == null) {
            notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.noimagefound);
        } else {
            notification.contentView.setImageViewBitmap(R.id.imageViewAlbumArt, bitmap);
        }
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, notification);
    }

    // Update the button in Notification when changing song
    public void changeBtn(Boolean isPlaying) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (isPlaying) {
            // if the song is Playing, change the btn to Pause btn
            notification.contentView.setImageViewResource(R.id.btnPlay, R.drawable.ic_play_circle_outline_black_24dp);
            // Notify the change
            nm.notify(NOTIFICATION_ID, notification);
        } else {
            // if the song is Paused, change the btn to play btn
            notification.contentView.setImageViewResource(R.id.btnPlay, R.drawable.ic_pause_circle_outline_black_24dp);
            // Notify the change
            nm.notify(NOTIFICATION_ID, notification);
        }
    }

    // Cancel the Notification
    public void cancelNotification() {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancelAll();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
