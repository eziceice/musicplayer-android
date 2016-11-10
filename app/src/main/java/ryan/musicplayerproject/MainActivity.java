package ryan.musicplayerproject;

import android.Manifest;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
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
import ryan.musicplayerproject.Fragment.LoadSongFragment;
import ryan.musicplayerproject.Fragment.PlayFragment;
import ryan.musicplayerproject.Fragment.SongListFragment;

// All the Icon Download From Google Material Design

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,EasyPermissions.PermissionCallbacks {

    // Define Read Storage permission flag
    private static final int REQ_READ_STORAGE_PERMISSION = 200;

    // Fragment Manager
    private android.app.FragmentManager fragmentManager = getFragmentManager();

    // Fragment
    private PlayFragment playFragment;
    private LoadSongFragment loadSongFragment;
    private FavouriteSongFragment favouriteSongFragment;
    private SongListFragment songListFragment;

    // Fragment Show and Hide
    public static final int playF = 0;
    public static final int loadF = 1;
    public static final int favF = 2;
    public static final int songF = 3;

    // Value which can pass between Fragment
    private String songPath;

    public String getSongPath() {
        return songPath;
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

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

        getSupportActionBar().setTitle("Music Player");

        // Create All the Fragment and set Hide
        playFragment = new PlayFragment();
        loadSongFragment = new LoadSongFragment();
        favouriteSongFragment = new FavouriteSongFragment();
        songListFragment = new SongListFragment();
        android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(R.id.content_frame, playFragment, "play");
        ft.hide(playFragment);
        ft.add(R.id.content_frame, loadSongFragment, "load");
        ft.hide(loadSongFragment);
        ft.add(R.id.content_frame, favouriteSongFragment, "fav");
        ft.hide(favouriteSongFragment);
        ft.add(R.id.content_frame, songListFragment, "song");
        ft.show(songListFragment);
        ft.commit();
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
        if (id == R.id.nav_loadMusic) {
            // Handle the load music action
           showFragment(1);
        }
        if (id == R.id.nav_songList) {
            // Handle the Song List action
            showFragment(3);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // When Click the Navigation, show the corresponding Fragment
    public void showFragment(int index) {

        android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
        hideFragment(ft);

        switch (index) {
            case playF:
                    ft.show(playFragment);
                break;
            case loadF:
                    ft.show(loadSongFragment);
                break;
            case favF:
                    ft.show(favouriteSongFragment);
                break;
            case songF:
                    ft.show(songListFragment);
                break;
        }
        ft.commit();
    }

    // Hide Fragment
    public void hideFragment(android.app.FragmentTransaction ft){
        if (playFragment != null){
            ft.hide(playFragment);
        }
        if (loadSongFragment != null) {
            ft.hide(loadSongFragment);
        }
        if (songListFragment != null) {
            ft.hide(songListFragment);
        }
        if (favouriteSongFragment != null) {
            ft.hide(favouriteSongFragment);
        }
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
        Log.d("PlayFragment", "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("PlayFragment", "onPermissionsDenied:" + requestCode + ":" + perms.size());
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
        } else {
            // Ask for declared permissions
            EasyPermissions.requestPermissions(this,
                    "This app requires access to your storage to capture songs.",
                    REQ_READ_STORAGE_PERMISSION,
                    permissions);
        }
    }
}
