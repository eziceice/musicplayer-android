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
import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.R;
import ryan.musicplayerproject.SongListAdapter;
import ryan.musicplayerproject.SongsManager;

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        vSongList = inflater.inflate(R.layout.fragement_song_list, container, false);
        songsManager = new SongsManager(getContext());
        songList = songsManager.getSongList();
        songListView = (ListView) vSongList.findViewById(R.id.songList);
        songListAdapter = new SongListAdapter(songList,getContext());
        songListView.setAdapter(songListAdapter);
        manager = getFragmentManager();

        // Add click listener for list items
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = manager.beginTransaction();
                // Get Selected Song
                HashMap<String, Music> selectedSong = (HashMap<String, Music>) songListView.getAdapter().getItem(position);
                // Get Current SongListFragment
                SongListFragment currentSongListFragment = (SongListFragment) getFragmentManager().findFragmentByTag("song");
                //
                playFragment = (PlayFragment) getFragmentManager().findFragmentByTag("play");
                int index = playFragment.getSongIndex(selectedSong.get("songTitle").getMusicPath());
                playFragment.playSong(index);
                playFragment.currentSongIndex = index;

                // Show PlayFragment
                ft.show(playFragment);
                // Hide the Current SongListFragment
                ft.hide(currentSongListFragment);


                // Pass Value Through Activity
                //((MainActivity)getActivity()).setSongPath(selectedSong.get("songPath"));
                // Pass Value Through Bundle
                //Bundle bundle = new Bundle();
                //bundle.putString("songPath", selectedSong.get("songPath"));
                //playFragment.setArguments(bundle);

                ft.commit();
            }
        });
        return vSongList;
    }
}
