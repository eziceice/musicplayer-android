package ryan.musicplayerproject.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import ryan.musicplayerproject.MainActivity;
import ryan.musicplayerproject.Database.DatabaseHelper;
import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.R;
import ryan.musicplayerproject.Adapter.SongListAdapter;
import ryan.musicplayerproject.Utility.SongsManager;

/**
 * Created by EleMeNt on 11/05/16.
 */
public class SongListFragment extends Fragment {
    private View vSongList;
    // Song Variable
    private ArrayList<HashMap<String, Music>> songList;
    private SongsManager songsManager;
    // List Variable
    private ListView songListView;
    private SongListAdapter songListAdapter;
    // Fragment Variable
    private FragmentManager manager;
    private PlayFragment playFragment;
    // Database helper and Favlist
    private DatabaseHelper dbHelper;
    private ArrayList<HashMap<String, Music>> favList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vSongList = inflater.inflate(R.layout.fragement_song_list, container, false);
        songsManager = new SongsManager(getContext());
        songList = songsManager.getSongList();
        setFav(); // Set Song Status in two SongList are same
        songListView = (ListView) vSongList.findViewById(R.id.songList);
        songListAdapter = new SongListAdapter(songList,getContext());
        songListView.setAdapter(songListAdapter);
        manager = getFragmentManager();
        // Set Action Bar Title
        ((MainActivity) getActivity()).setActionBarTitle("Song List");
        // Add click listener for list items
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = manager.beginTransaction();
                // Get Selected Song
                HashMap<String, Music> selectedSong = (HashMap<String, Music>) songListView.getAdapter().getItem(position);
                // Get Current SongListFragment
                SongListFragment currentSongListFragment = (SongListFragment) getFragmentManager().findFragmentByTag("song");
                // Set the songList to PlayFragment
                playFragment = (PlayFragment) getFragmentManager().findFragmentByTag("play");
                playFragment.setSongList(songList);
                if(selectedSong.get("songTitle") == playFragment.getCurrentMusic()) {
                    // if its current Playing Song, Do nothing
                } else {
                    playFragment.playSong(position);
                }
                ((MainActivity)getActivity()).setActionBarTitle(playFragment.getSongName(playFragment.getCurrentMusicPath()));
                // Show PlayFragment
                ft.show(playFragment);
                // Hide the Current SongListFragment
                ft.hide(currentSongListFragment);
                ft.commit();
            }
        });
        return vSongList;
    }
    // Set the Fav in the SongList - make the song status in two Song List are Same
    public void setFav() {
        // Database Helper for changing the isLike in the songList
        dbHelper = new DatabaseHelper(getContext());
        favList = dbHelper.getAllFavMusic();
        // Match the isLike in songList and favList
        for(int i = 0; i < songList.size(); i++) {
            for(int j = 0; j < favList.size(); j++) {
                String songTitle = songList.get(i).get("songTitle").getMusicTitle();
                if(songTitle.equals(favList.get(j).get("songTitle").getMusicTitle())) {
                    songList.get(i).get("songTitle").setLike(favList.get(j).get("songTitle").isLike());
                }
            }
        }
    }
}
