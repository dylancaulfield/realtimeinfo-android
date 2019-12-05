package ie.dylancaulfield.realtimeinfo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.models.LiveData;
import ie.dylancaulfield.realtimeinfo.models.TLocation;

public class LiveDataListViewAdapter extends ArrayAdapter<LiveData> {

    private LayoutInflater mInflater;
    private ArrayList<LiveData> mData;

    public LiveDataListViewAdapter(Context context, int textViewResourceId, ArrayList<LiveData> list) {
        super(context, textViewResourceId, list);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = list;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_livedata, parent, false);
        }

        LiveData liveData = mData.get(position);

        TextView destination = convertView.findViewById(R.id.list_livedata_destination);
        destination.setText(liveData.getDestination());

        TextView time = convertView.findViewById(R.id.list_livedata_time);
        time.setText(liveData.getDuetime());

        TextView route = convertView.findViewById(R.id.list_livedata_route);
        route.setText(liveData.getRoute());


        return convertView;

    }

}
