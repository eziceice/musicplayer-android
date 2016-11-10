package ryan.finalassignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by EleMeNt on 4/05/16.
 */
public class PlayerListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HashMap<String,String>> songList;

    public PlayerListAdapter(ArrayList<HashMap<String,String>> songList, Context context) {
        this.songList = songList;
        this.context = context;
    }
    public static class ViewHolder {
        TextView songTitle;
    }

    @Override // Method extends from BaseAdapter which must be implemented
    public int getCount() {
        return songList.size();
    }
    @Override // Method extends from BaseAdapter which must be implemented
    public HashMap<String,String> getItem(int i) {
        return songList.get(i);
    }
    @Override // Method extends from BaseAdapter which must be implemented
    public long getItemId(int i) {
        return i;
    }
    @Override // Method extends from BaseAdapter which must be implemented
    public View getView (int i, View view, ViewGroup viewGroup) {
        ViewHolder vh;

        // Check if view has been created for the row.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Reference List item layout here
            view = inflater.inflate(R.layout.player_list_item, null);

            // Setup ViewHolder and attach to view
            vh = new ViewHolder();
            vh.songTitle = (TextView) view.findViewById(R.id.songTitle);
            view.setTag(vh);
        } else {
            // View has already been created, fetch Viewholder
            vh = (ViewHolder) view.getTag();
        }
        // Assign values to the TextViews using the Reminder object
        vh.songTitle.setText(songList.get(i).get("songTitle"));
        return view;
    }
}

