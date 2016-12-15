package ryan.musicplayerproject;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import ryan.musicplayerproject.Fragment.FavouriteSongFragment;
import ryan.musicplayerproject.Fragment.PlayFragment;
import ryan.musicplayerproject.Fragment.SongListFragment;
import ryan.musicplayerproject.Notification.NotificationService;

/**
 * All the Icon Download From Google Material Design
 * @link https://design.google.com/icons/
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,EasyPermissions.PermissionCallbacks {

    // Application to pass information
    private MusicApplication application;

    // Define Read Storage permission flag
    private static final int REQ_READ_STORAGE_PERMISSION = 200;

    // Fragment Manager
    private android.app.FragmentManager fragmentManager = getFragmentManager();

    // Fragment in Main Activity
    private PlayFragment playFragment = new PlayFragment();
    private FavouriteSongFragment favouriteSongFragment = new FavouriteSongFragment();
    private SongListFragment songListFragment = new SongListFragment();

    // Fragment Show and Hide
    public static final int playF = 0;
    public static final int favF = 2;
    public static final int songF = 3;

    /* Method which can pass values between Fragment through Activity
    private String songPath;
    public String getSongPath() { return songPath;}
    public void setSongPath(String songPath) { this.songPath = songPath;}
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set Title Bar Name
        getSupportActionBar().setTitle("Music Player");

        // Notification Service
        NotificationService notificationService = new NotificationService();
        notificationService.customSimpleNotification(getApplicationContext());

        // Get Permissions from the User
        storageAccess();

        // Application for passing PlayFragment and Notification
        application = (MusicApplication) getApplication();
        application.playFragment = this.playFragment;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favourite) {
            // Handle the favourite song list action
            showFragment(2);
        }
        if (id == R.id.nav_nowPlaying) {
            // Handle the Now Playing song action
            showFragment(0);
        }

        if (id == R.id.nav_songList) {
            // Handle the Song List action
            showFragment(3);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // When Click the Navigation Item, show the corresponding Fragment
    public void showFragment(int index) {
        // Begin Fragment
        android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        // Hide all Fragment
        hideFragment(ft);
        switch (index) {
            case playF:
                // Get current Music Name
                String title = playFragment.getSongName(playFragment.getCurrentMusicPath());
                // Set action bar title for different layout - Now Playing
                if (title == null) {
                    setActionBarTitle("Now Playing");
                } else {
                    setActionBarTitle(playFragment.getSongName(playFragment.getCurrentMusicPath()));
                }
                // Show PlayFragment
                ft.show(playFragment);
                break;
            case favF:
                // Set action bar title for different layout - Favourite Song
                setActionBarTitle("Favourite Song");
                // Show FavFragment
                ft.show(favouriteSongFragment);
                break;
            case songF:
                // Set action bar title for different layout - Song List
                setActionBarTitle("Song List");
                // Show SongListFragment
                ft.show(songListFragment);
                break;
        }
        ft.commit();
    }

    // Hide All Fragment
    public void hideFragment(android.app.FragmentTransaction ft){
        if (playFragment != null){
            ft.hide(playFragment);
        }
        if (songListFragment != null) {
            ft.hide(songListFragment);
        }
        if (favouriteSongFragment != null) {
            ft.hide(favouriteSongFragment);
        }
    }

    // Set Title For Action Bar
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }



    /**
     * Using the EasyPermissions library to simplify runtime permissions on Android 6.0+
     * @link https://github.com/googlesamples/easypermissions/
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("LyricFragment", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("LyricFragment", "onPermissionsDenied:" + requestCode + ":" + perms.size());
    }

    // Declare method which outlines required permissions to read file from SdCard
    @AfterPermissionGranted(REQ_READ_STORAGE_PERMISSION)
    public void storageAccess() {
        // Declare permissions that we require
        String[] permissions = {
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (EasyPermissions.hasPermissions(this, permissions)) {
            // We have permission...
            // Create an intent to capture picture and return the result back to the application
            // Create All the Fragment and set Hide
            android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
            // Add tag Play to playFragment
            ft.add(R.id.content_frame, playFragment, "play");
            ft.hide(playFragment);
            // Add tag fav to FavFragment
            ft.add(R.id.content_frame, favouriteSongFragment, "fav");
            ft.hide(favouriteSongFragment);
            // Add tag Song to songListFragment
            ft.add(R.id.content_frame, songListFragment, "song");
            ft.show(songListFragment);
            ft.commit();

        } else {
            // Ask for declared permissions
            EasyPermissions.requestPermissions(this,
                    "This app requires access to your storage to capture songs.",
                    REQ_READ_STORAGE_PERMISSION,
                    permissions);
        }
    }

    public PlayFragment getPlayFragment() {
        return playFragment;
    }

    public void setPlayFragment(PlayFragment playFragment) {
        this.playFragment = playFragment;
    }

    public FavouriteSongFragment getFavouriteSongFragment() {
        return favouriteSongFragment;
    }

    public void setFavouriteSongFragment(FavouriteSongFragment favouriteSongFragment) {
        this.favouriteSongFragment = favouriteSongFragment;
    }

    public SongListFragment getSongListFragment() {
        return songListFragment;
    }

    public void setSongListFragment(SongListFragment songListFragment) {
        this.songListFragment = songListFragment;
    }
}
