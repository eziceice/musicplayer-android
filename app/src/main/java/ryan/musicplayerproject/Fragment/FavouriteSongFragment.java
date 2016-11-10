package ryan.musicplayerproject.Fragment;

/**
 * Created by EleMeNt on 6/05/16.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ryan.musicplayerproject.FavouriteSongAdapter;
import ryan.musicplayerproject.Model.DatabaseHelper;
import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.R;
import ryan.musicplayerproject.SongListAdapter;
import ryan.musicplayerproject.SongsManager;

public class FavouriteSongFragment extends Fragment {
    private View vFSong;

    // ArrayList for all Favourite Songs
    private ArrayList<HashMap<String, Music>> songList;
    // ArrayList for all searched songs
    private ArrayList<HashMap<String, String>> searchedSongList;
    private Context context;

    // Layout Variable
    private EditText searchText;
    private ImageView searchIcon;
    // Song Manager
    private SongsManager songsManager;
    // ListView and Adapter
    private ListView favouriteListView;
    private FavouriteSongAdapter adapter;

    private DatabaseHelper dbHelper;
    private Music music;
    // Fragment
    private FragmentManager manager;
    private PlayFragment playFragment;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vFSong = inflater.inflate(R.layout.fragment_favouritesong_list, container, false);

        dbHelper = new DatabaseHelper(getContext());
        context = getContext();
        searchText = (EditText) vFSong.findViewById(R.id.search);
        searchIcon = (ImageView) vFSong.findViewById(R.id.search_icon);

        songList = new ArrayList<>();
        songsManager = new SongsManager(getContext());
        favouriteListView = (ListView) vFSong.findViewById(R.id.favList);
        adapter = new FavouriteSongAdapter(songList, context);
        favouriteListView.setAdapter(adapter);

        manager = getFragmentManager();

        favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = manager.beginTransaction();
                // Get Selected Song
                HashMap<String, Music> selectedSong = (HashMap<String, Music>) favouriteListView.getAdapter().getItem(position);
                // Get Current FavouriteSongFragment
                FavouriteSongFragment currentFragment = (FavouriteSongFragment) getFragmentManager().findFragmentByTag("fav");
                //
                playFragment = (PlayFragment) getFragmentManager().findFragmentByTag("play");
                int index = playFragment.getSongIndex(selectedSong.get("songTitle").getMusicPath());
                playFragment.playSong(index);
                playFragment.currentSongIndex = index;

                // Show PlayFragment
                ft.show(playFragment);
                // Hide the Current SongListFragment
                ft.hide(currentFragment);


                // Pass Value Through Activity
                //((MainActivity)getActivity()).setSongPath(selectedSong.get("songPath"));
                // Pass Value Through Bundle
                //Bundle bundle = new Bundle();
                //bundle.putString("songPath", selectedSong.get("songPath"));
                //playFragment.setArguments(bundle);

                ft.commit();
            }
        });

        return vFSong;
    }

    // Add a Song to the List
    public void addSong(Music music) {
        HashMap<String, Music> songFavList = new HashMap<>();
        songFavList.put("songTitle", music);
        if(checkExist(songFavList)) {
            songList.add(songFavList);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Add To Favourite", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext()," This Song is already Exist in the Favourite List. ",Toast.LENGTH_SHORT).show();
        }
    }
    // Remove a song to the List
    public void removeSong(Music music) {
        int index = 0;
        HashMap<String, Music> songFavList = new HashMap<>();
        songFavList.put("songTitle", music);
        for (int i = 0; i < songList.size(); i++) {
            if (songFavList.get("songTitle").getMusicTitle() == songList.get(i).get("songTitle").getMusicTitle()) {
                index = i;
            }
        }
        songList.remove(index);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Remove From Favourite", Toast.LENGTH_SHORT).show();
    }

    public boolean checkExist(HashMap<String, Music> checkSong) {
        boolean check = true;
        for (int i = 0; i < songList.size(); i++) {
            if (checkSong.get("songTitle").getMusicTitle() == songList.get(i).get("songTitle").getMusicTitle()) {
                check = false;
            }
        }
        return check;
    }

}
