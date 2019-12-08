package ie.dylancaulfield.realtimeinfo.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.adapters.LocationsListViewAdapter;
import ie.dylancaulfield.realtimeinfo.models.TLocation;
import ie.dylancaulfield.realtimeinfo.models.TLocationParent;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener {

    private TLocationParent mTLocationParent;
    private ArrayList<TLocation> mTLocations;
    private Gson mGson = new Gson();
    private LocationsListViewAdapter mListViewAdapter;
    private String mSearchText = "";
    private String mOperator = "";
    private BottomSheetBehavior mBehaviour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        RelativeLayout bottomSheet = findViewById(R.id.bottomDialog_search);
        mBehaviour = BottomSheetBehavior.from(bottomSheet);
        mBehaviour.setHideable(true);
        mBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);


        InputStream inputStream = getResources().openRawResource(R.raw.locations);
        InputStreamReader reader = new InputStreamReader(inputStream);

        mTLocationParent = mGson.fromJson(reader, TLocationParent.class);
        mTLocations = new ArrayList<>(mTLocationParent.getLocations());

        mListViewAdapter = new LocationsListViewAdapter(getApplicationContext(), R.layout.list_location, mTLocations);
        ListView listView = findViewById(R.id.listView_search);
        listView.setAdapter(mListViewAdapter);
        listView.setOnItemClickListener(this);

        SharedPreferences preferences = getApplicationContext().getSharedPreferences("filter", MODE_PRIVATE);
        // TODO: save last operator
        //Editor edit = preferences.edit();

        mOperator = preferences.getString("operator", "");


        updateDataSet();

    }

    // Create the toolbar options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    // Toolbar menu item clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
                break;

            }

            case R.id.action_search: {

                mBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                break;

            }

            case R.id.action_filter: {

                if (mBehaviour.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                    mBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                } else {
                    mBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                break;


            }

        }

        return true;

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    // Search bar text changes
    @Override
    public boolean onQueryTextChange(String newText) {

        mSearchText = newText;
        updateDataSet();

        return false;

    }

    // Listview item clicked
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView textView = view.findViewById(R.id.list_location_stopid);
        String stopid = textView.getText().toString();

        TLocation location = null;

        for (TLocation t : mTLocationParent.getLocations()) {

            if (t.getStopid().equals(stopid)) {
                location = t;
            }

        }

        if (location == null) {
            return;
        }

        Intent i = new Intent(getApplicationContext(), LocationActivity.class);
        i.putExtra("stopid", location.getStopid());
        startActivity(i);

    }

    // Combine filters and update data set
    private void updateDataSet() {

        FilterTask task = new FilterTask();
        task.execute(mSearchText);


    }

    private class FilterTask extends AsyncTask<String, Void, ArrayList<TLocation>> {

        @Override
        protected ArrayList<TLocation> doInBackground(String... strings) {

            ArrayList<TLocation> filterLocations = new ArrayList<>();

            for (TLocation location : mTLocationParent.getLocations()) {

                if (isCancelled()) {
                    return null;
                }

                boolean shouldAdd = false;

                for (String searchText : strings) {

                    String lowerCase = searchText.toLowerCase();

                    if (location.getName().toLowerCase().contains(lowerCase)) {
                        shouldAdd = true;
                    }

                    if (location.getStopid().toLowerCase().contains(lowerCase)) {
                        shouldAdd = true;
                    }

                }

                if (shouldAdd) {
                    filterLocations.add(location);
                }


            }


            return filterLocations;
        }

        @Override
        protected void onPostExecute(ArrayList<TLocation> locations) {

            mTLocations.clear();
            mTLocations.addAll(locations);
            mListViewAdapter.notifyDataSetChanged();

        }

    }

}
