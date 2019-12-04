package ie.dylancaulfield.realtimeinfo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;

import java.nio.file.Path;
import java.util.List;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.models.TLocation;

public class LocationsListViewAdapter extends ArrayAdapter<TLocation> {

    private LayoutInflater mInflater;

    public LocationsListViewAdapter(Context context, int textViewResourceId, List<TLocation> list) {
        super(context, textViewResourceId, list);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.list_location, parent, false);
        }


        try {
            getItem(position);
        } catch (IndexOutOfBoundsException e){
            return view;
        }

        TLocation tLocation = getItem(position);
        if (tLocation == null) {
            return view;
        }

        TextView name = view.findViewById(R.id.list_location_name);
        name.setText(tLocation.getName());

        TextView routes = view.findViewById(R.id.list_location_routes);
        routes.setText(tLocation.getOperator().getRoutes());

        TextView operator = view.findViewById(R.id.list_location_operator);
        operator.setText(tLocation.getOperator().getName());
        operator.setTextColor(getContext().getResources().getColor(R.color.colorAccent, null));

        TextView stopid = view.findViewById(R.id.list_location_stopid);
        // If bus
        if (tLocation.getOperator().getOperatortype() == 1){
            stopid.setText(tLocation.getStopid());
        } else {
            stopid.setText("");
        }



        return view;

    }

}
