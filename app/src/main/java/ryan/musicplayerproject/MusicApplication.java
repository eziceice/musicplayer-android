package ryan.musicplayerproject;

import android.app.Application;


import ryan.musicplayerproject.Fragment.PlayFragment;
import ryan.musicplayerproject.Notification.NotificationService;

/**
 * Created by EleMeNt on 18/05/16.
 */
public class MusicApplication extends Application{
    // Music Application for passing values from different Class
    public PlayFragment playFragment; // PlayFragment
    public NotificationService songNotification; // Notification

}
