package ie.dylancaulfield.realtimeinfo.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.adapters.LocationsListViewAdapter;
import ie.dylancaulfield.realtimeinfo.models.TLocation;
import ie.dylancaulfield.realtimeinfo.models.TLocationParent;

public class SearchActivity extends AppCompatActivity {

    private TLocationParent mTLocationParent;
    private ArrayList<TLocation> mTLocations;
    private Gson mGson = new Gson();
    private LocationsListViewAdapter mListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);


        InputStream inputStream = getResources().openRawResource(R.raw.locations);
        InputStreamReader reader = new InputStreamReader(inputStream);

        mTLocationParent = mGson.fromJson(reader, TLocationParent.class);
        mTLocations = (ArrayList<TLocation>) mTLocationParent.getLocations().clone();

        mListViewAdapter = new LocationsListViewAdapter(getApplicationContext(), R.layout.list_location, mTLocations);
        ListView listView = findViewById(R.id.listView_search);
        listView.setAdapter(mListViewAdapter);

        //TODO fix IllegalStateException

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TLocation tLocation = ( (ArrayList<TLocation>) mTLocations.clone()).get(position);
                String json = mGson.toJson(tLocation);

                Intent i = new Intent(getApplicationContext(), LocationActivity.class);
                i.putExtra("tlocation", json);
                startActivity(i);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);


        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        ArrayList<TLocation> locations = mTLocationParent.getLocations();

                        for (int i = 0; i < locations.size(); i++) {


                            final TLocation location = locations.get(i);

                            boolean shouldAdd = location.getName().toLowerCase().contains(newText) ||
                                    location.getStopid().toLowerCase().contains(newText);

                            if (shouldAdd) {


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
                }).start();

                mTLocations.clear();


                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home: {

                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

            }

        }

        return true;

    }
}
