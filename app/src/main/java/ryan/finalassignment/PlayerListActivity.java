package ryan.finalassignment;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerListActivity extends AppCompatActivity {

    private EditText searchText;
    private PlayerListAdapter playerListAdapter;
    // ArrayList for all songs
    private ArrayList<HashMap<String, String>> songList;
    // ArrayList for all searched songs
    private ArrayList<HashMap<String, String>> searchedSongList;
    private Context context;
    private SongsManager songsManager;
    private String musicPath;
    private ImageView searchIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        context = getApplicationContext();

        searchText = (EditText) findViewById(R.id.search);
        searchIcon = (ImageView) findViewById(R.id.search_icon);



    }
}
