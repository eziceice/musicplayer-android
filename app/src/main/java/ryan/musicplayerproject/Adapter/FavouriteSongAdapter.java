package ryan.musicplayerproject.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import ryan.musicplayerproject.Model.Music;
import ryan.musicplayerproject.R;

/**
 * Created by EleMeNt on 12/05/16.
 */
public class FavouriteSongAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String,Music>> songList;


    public FavouriteSongAdapter(ArrayList<HashMap<String,Music>> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }
    public static class ViewHolder {
        TextView songTitle;
        ImageView songImage;
        TextView songSource;
    }

    @Override // Method extends from BaseAdapter which must be implemented
    public int getCount() {
        return songList.size();
    }
    @Override // Method extends from BaseAdapter which must be implemented
    public HashMap<String, Music> getItem(int i) {
        return songList.get(i);
    }
    @Override // Method extends from BaseAdapter which must be implemented
    public long getItemId(int i) {
        return i;
    }
    @Override // Method extends from BaseAdapter which must be implemented
    public View getView (int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;
        MediaMetadataRetriever musicRetriver = new MediaMetadataRetriever();
        // Check if view has been created for the row.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Reference List item layout here
            view = inflater.inflate(R.layout.favouritesong_list_item, null);

            // Setup ViewHolder and attach to view
            vh = new ViewHolder();
            vh.songTitle = (TextView) view.findViewById(R.id.songTitle);
            vh.songImage = (ImageView) view.findViewById(R.id.songImage);
            vh.songSource = (TextView) view.findViewById(R.id.songSource);
            view.setTag(vh);
        } else {
            // View has already been created, fetch ViewHolder
            vh = (ViewHolder) view.getTag();
        }
        // Assign values to the TextViews using the Music object
        String songTitle = songList.get(i).get("songTitle").getMusicTitle().replace(".mp3", "");
        int replace = songTitle.indexOf("-");
        songTitle = songTitle.substring(replace + 1);

        // Set Song Title Text
        vh.songTitle.setText(songTitle);

        // Set Song is Local Music or Internet Music
        if (songList.get(i).get("songTitle").getMusicPath().contains("/")) {
            vh.songSource.setText("Local Music");
        }
        musicRetriver.setDataSource(songList.get(i).get("songTitle").getMusicPath());

        // Set Song Album Cover Image
        try {
            Bitmap bitmap = null;
            // Get the picture from Source
            byte[] bytes = musicRetriver.getEmbeddedPicture();
            // Change it to Image
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            vh.songImage.setImageBitmap(bitmap);
        } catch (Exception e2) {
            // If No CoverImage, Set it to the default image
            vh.songImage.setImageResource(R.drawable.noimagefound);
        }
        return view;
    }
}

