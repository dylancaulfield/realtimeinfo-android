package ie.dylancaulfield.realtimeinfo.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private Thread mFilterThread;

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

        String json = mGson.toJson(location);

        Intent i = new Intent(getApplicationContext(), LocationActivity.class);
        i.putExtra("tlocation", json);
        startActivity(i);

    }

    // Combine filters and update data set
    private void updateDataSet() {

        mTLocations.clear();
        mListViewAdapter.notifyDataSetChanged();

        if (mFilterThread != null){
            mFilterThread.interrupt();
        }

        mFilterThread = new Thread(new Runnable() {

            @Override
            public void run() {

                ArrayList<TLocation> locations = new ArrayList<>(mTLocationParent.getLocations());

                for (int i = 0; i < locations.size(); i++) {

                    final TLocation location = locations.get(i);

                    boolean nameIdFilter = location.getName().toLowerCase().contains(mSearchText.trim()) ||
                            location.getStopid().toLowerCase().contains(mSearchText.trim());

                    boolean operatorFilter = location.getOperator().getName().contains(mOperator);

                    if (nameIdFilter && operatorFilter) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTLocations.add(location);
                                mListViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }

            }
        });
        mFilterThread.start();


    }
}
