package ryan.musicplayerproject;

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

/**
 * Created by EleMeNt on 4/05/16.
 */
public class SongListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String,Music>> songList;
    public SongListAdapter(ArrayList<HashMap<String,Music>> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }
    public static class ViewHolder {
        TextView songTitle;
        TextView songDuration;
        ImageView songImage;
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
        Helper helper = new Helper();

        // Check if view has been created for the row.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Reference List item layout here
            view = inflater.inflate(R.layout.songlist_item, null);

            // Setup ViewHolder and attach to view
            vh = new ViewHolder();
            vh.songTitle = (TextView) view.findViewById(R.id.songTitle);
            vh.songDuration = (TextView) view.findViewById(R.id.songDuration);
            vh.songImage = (ImageView) view.findViewById(R.id.imageCover);
            view.setTag(vh);
        } else {
            // View has already been created, fetch Viewholder
            vh = (ViewHolder) view.getTag();
        }
        // Assign values to the TextViews using the Reminder object
        String songTitle = songList.get(i).get("songTitle").getMusicTitle().replace(".mp3", "");
        int replace = songTitle.indexOf("-");
        songTitle = songTitle.substring(replace + 2);
        musicRetriver.setDataSource(songList.get(i).get("songTitle").getMusicPath());
        try {
            String songDuration = helper.milliSecondsToTime(Long.parseLong(musicRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
            vh.songDuration.setText("Duration " + songDuration);
        } catch (Exception e) {
            vh.songDuration.setText("");
        }
        try {
            Bitmap bitmap = null;
            // Get the picture from Source
            byte[] bytes = musicRetriver.getEmbeddedPicture();
            // Change it to Image
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            vh.songImage.setImageBitmap(bitmap);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        vh.songTitle.setText(songTitle);
        return view;
    }
}

