package ryan.musicplayerproject.Fragment;

/**
 * Created by EleMeNt on 6/05/16.
 */

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

import ryan.musicplayerproject.Adapter.FavouriteSongAdapter;
import ryan.musicplayerproject.MainActivity;
import ryan.musicplayerproject.Database.DatabaseHelper;
import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.R;
import ryan.musicplayerproject.Utility.SongsManager;

public class FavouriteSongFragment extends Fragment {
    // Fragment View
    private View vFSong;
    // ArrayList for all Favourite Songs
    private ArrayList<HashMap<String, Music>> songList;
    // ArrayList for all searched Favourite songs
    private ArrayList<HashMap<String, Music>> searchedSongList;
    private Context context;
    // Layout Variable
    private EditText searchText;
    // Song Manager
    private SongsManager songsManager;
    // ListView and Adapter
    private ListView favouriteListView;
    private FavouriteSongAdapter adapter;
    // DatabaseHelper
    private DatabaseHelper dbHelper;
    // Fragment
    private FragmentManager manager = getFragmentManager();
    private PlayFragment playFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initiate all used variables
        vFSong = inflater.inflate(R.layout.fragment_favouritesong_list, container, false);
        dbHelper = new DatabaseHelper(getContext());
        context = getContext();
        songsManager = new SongsManager(getContext());
        // Connection with variables in Layout
        searchText = (EditText) vFSong.findViewById(R.id.search);
        favouriteListView = (ListView) vFSong.findViewById(R.id.favList);

        // Check if there exist favSongList in the database, if not create a new list.
        if (dbHelper.getAllFavMusic().size() != 0) {
            songList = checkSongExist(dbHelper.getAllFavMusic());
        } else {
            songList = new ArrayList<>();
        }
        // Set Adapter to the ListView
        adapter = new FavouriteSongAdapter(songList, context);
        favouriteListView.setAdapter(adapter);
        // Create the new searchedList
        searchedSongList = new ArrayList<>();
        // Toolbar for favView
        Toolbar toolbar = (Toolbar) vFSong.findViewById(R.id.toolbar);

        favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction ft = manager.beginTransaction();
                // Get Selected Song
                HashMap<String, Music> selectedSong = (HashMap<String, Music>) favouriteListView.getAdapter().getItem(position);
                // Get Current FavouriteSongFragment
                FavouriteSongFragment currentFragment = (FavouriteSongFragment) getFragmentManager().findFragmentByTag("fav");
                // Find the playFragment
                playFragment = (PlayFragment) getFragmentManager().findFragmentByTag("play");
                // Set the FavSongList to Play Fragment
                playFragment.setSongList(songList);
                if(selectedSong.get("songTitle") == playFragment.getCurrentMusic()) {
                    // if its current Playing Song, Do nothing
                } else {
                    playFragment.playSong(position);
                }
                // Set the ActionBar Name for PlayFragment
                ((MainActivity)getActivity()).setActionBarTitle(playFragment.getSongName(playFragment.getCurrentMusicPath()));
                // Show PlayFragment
                ft.show(playFragment);
                // Hide the Current SongListFragment
                ft.hide(currentFragment);


                /* Pass Value Through Activity by using Bundle
                ((MainActivity)getActivity()).setSongPath(selectedSong.get("songPath"));
                Bundle bundle = new Bundle();
                bundle.putString("songPath", selectedSong.get("songPath"));
                playFragment.setArguments(bundle);
                */

                ft.commit();
            }
        });

        // Remove music from the Fav List
        favouriteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int position
                    , long l) {
                // Build Dialog to delete item
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Remove Song?");
                builder.setMessage("Are you sure you wish to remove this Song?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Remove Fav music from Database
                                HashMap<String, Music> music = new HashMap<String, Music>();
                                Music m = songList.get(position).get("songTitle");
                                music.put("songTitle", m);
                                songList.remove(music);
                                dbHelper.removeFavMusic(m);
                                Toast.makeText(getContext(), m.getMusicTitle() + " has been removed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // close the dialog
                                dialog.cancel();
                            }
                        }
                );
                builder.create().show();
                return false;
            }
        });


        // search Text changeListener
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // When Text Changed in the SearchText, Show the New List matches the SearchText
                searchedSongList = searchSong(searchText.getText().toString());
                adapter = new FavouriteSongAdapter(searchedSongList, context);
                favouriteListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return vFSong;
    }

    // Add a Song to the FavList - Database
    public void addSong(Music music) {
        HashMap<String, Music> songFav = new HashMap<>();
        songFav.put("songTitle", music);
        if(checkExist(songFav)) {
            songList.add(songFav);
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Add To Favourite", Toast.LENGTH_SHORT).show();
            // add the song to Database
            dbHelper.addFavMusic(music);
        } else {
        }
    }
    // Remove a song from the FavList - Database
    public void removeSong(Music music) {
        HashMap<String, Music> songFavObj = new HashMap<>();
        songFavObj.put("songTitle", music);
        // Remove the song by Object
        songList.remove(songFavObj);
        // Remove the song from database
        dbHelper.removeFavMusic(music);
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Remove From Favourite", Toast.LENGTH_SHORT).show();
    }

    // Check the song exist in the List or not
    public boolean checkExist(HashMap<String, Music> checkSong) {
        boolean check = true;
        for (int i = 0; i < songList.size(); i++) {
            if (checkSong.get("songTitle").getMusicTitle().equals(songList.get(i).get("songTitle").getMusicTitle())) {
                check = false;
            }
        }
        return check;
    }

    /*
    // Get the Index of the song From SongList
    public int getIndex(HashMap<String, Music> songFavObj) {
        int index = -1;
        for (int i = 0; i < songList.size(); i++) {
            if (songFavObj.get("songTitle").getMusicTitle().equals(songList.get(i).get("songTitle").getMusicTitle())) {
                index = i;
            }
        }
        return index;
    }
    */

    // Search a song from songList by a search String
    public ArrayList<HashMap<String, Music>> searchSong(String search) {
        search = search.toLowerCase();
        // if the search string is empty, show all the added songs
        if (search.trim().length() == 0) {

            // if there already exist a searched list, clear it.
            if (searchedSongList != null) {
                searchedSongList.clear();
            }
            // Get all the songs from songList and put them into SearchedSongList
            for(HashMap<String, Music> songs: songList) {
                searchedSongList.add(songs);
            }
        }

        else {
            // if there already exist a searched list, clear it.
            if (searchedSongList != null) {
                searchedSongList.clear();

                // Add the songs Which Match the Search String
                for(HashMap<String,Music> songs: songList) {
                    String songTitle = songs.get("songTitle").getMusicTitle();
                    if (songTitle.toLowerCase().contains(search)) {
                        searchedSongList.add(songs);
                    }
                }
            }
        }
        return searchedSongList;
    }

    // If some songs are deleted, make sure they will not in the favSong List
    public ArrayList<HashMap<String,Music>> checkSongExist(ArrayList<HashMap<String, Music>> checkedSongList) {
        ArrayList<HashMap<String, Music>> sdSong =  songsManager.getSongList();
        ArrayList<HashMap<String, Music>> existSong = new ArrayList<>();
        for(int i = 0; i < checkedSongList.size(); i++) {
            String songPath = checkedSongList.get(i).get("songTitle").getMusicPath();
            for(int j = 0; j < sdSong.size(); j++) {
                if (songPath.equals(sdSong.get(j).get("songTitle").getMusicPath())) {
                    existSong.add(checkedSongList.get(i));
                }
            }
        }
        return existSong;
    }

    public ArrayList<HashMap<String, Music>> getSearchedSongList() {
        return searchedSongList;
    }

    public void setSearchedSongList(ArrayList<HashMap<String, Music>> searchedSongList) {
        this.searchedSongList = searchedSongList;
    }

    public ArrayList<HashMap<String, Music>> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<HashMap<String, Music>> songList) {
        this.songList = songList;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public FavouriteSongAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(FavouriteSongAdapter adapter) {
        this.adapter = adapter;
    }

    public ListView getFavouriteListView() {
        return favouriteListView;
    }

    public void setFavouriteListView(ListView favouriteListView) {
        this.favouriteListView = favouriteListView;
    }
}
