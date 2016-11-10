package ryan.musicplayerproject.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ryan.musicplayerproject.FileSelectActivity;
import ryan.musicplayerproject.R;

/**
 * Created by EleMeNt on 6/05/16.
 */
public class LoadSongFragment extends Fragment {

    private View vLSong;
    private ImageButton btnInternet;
    private ImageButton btnSdcard;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vLSong = inflater.inflate(R.layout.fragment_loadsong, container, false);
        super.onCreate(savedInstanceState);

        btnSdcard = (ImageButton) vLSong.findViewById(R.id.loadFromSDcard);

        btnSdcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), FileSelectActivity.class);
                startActivity(intent);
            }
        });

        return vLSong;
    }
}
