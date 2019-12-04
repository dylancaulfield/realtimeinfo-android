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

import java.util.List;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.models.LiveData;
import ie.dylancaulfield.realtimeinfo.models.TLocation;

public class LiveDataListViewAdapter extends ArrayAdapter<LiveData> {

    private LayoutInflater mInflater;

    public LiveDataListViewAdapter(Context context, int textViewResourceId, List<LiveData> list) {
        super(context, textViewResourceId, list);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_livedata, parent, false);
        }

        try {
            getItem(position);
        } catch (IndexOutOfBoundsException e) {
            return view;
        }

        LiveData liveData = getItem(position);

        TextView destination = view.findViewById(R.id.list_livedata_destination);
        destination.setText(liveData.getDestination());

        TextView time = view.findViewById(R.id.list_livedata_time);
        time.setText(liveData.getDuetime());

        TextView route = view.findViewById(R.id.list_livedata_route);
        route.setText(liveData.getRoute());


        return view;

    }

}
