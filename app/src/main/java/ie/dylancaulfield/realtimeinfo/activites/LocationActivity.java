package ie.dylancaulfield.realtimeinfo.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.seismic.ShakeDetector;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.adapters.LiveDataListViewAdapter;
import ie.dylancaulfield.realtimeinfo.models.LiveData;
import ie.dylancaulfield.realtimeinfo.models.LiveDataParent;
import ie.dylancaulfield.realtimeinfo.models.TLocation;
import ie.dylancaulfield.realtimeinfo.models.TLocationParent;
import ie.dylancaulfield.realtimeinfo.utility.LiveDataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LocationActivity extends AppCompatActivity implements ShakeDetector.Listener {

    private TLocation mTLocation;
    private ArrayList<LiveData> mLiveData = new ArrayList<>();
    private Gson mGson = new Gson();
    private Call<LiveDataParent> mCall;
    private ContentLoadingProgressBar mProgressBar;
    private LiveDataListViewAdapter mAdapter;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        getTLocationFromIntent();

        mProgressBar = findViewById(R.id.progressBar_livedata);

        Toolbar toolbar = findViewById(R.id.toolbar_location);
        toolbar.setTitle(mTLocation.getName());
        setSupportActionBar(toolbar);

        mCoordinatorLayout = findViewById(R.id.parent_coordinator_location);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);


        String endpoint = getResources().getString(R.string.api_endpoint);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LiveDataService dataService = retrofit.create(LiveDataService.class);
        mCall = dataService.getData(mTLocation.getStopid());

        ListView listView = findViewById(R.id.listView_livedata);
        mAdapter = new LiveDataListViewAdapter(getApplicationContext(), R.layout.list_livedata, mLiveData);
        listView.setAdapter(mAdapter);

        fetchData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.livedata_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();
                break;

            }

            case R.id.action_add_fav: {

                addAsFavourite();

                break;
            }

            case R.id.action_show_map: {

                String formatted = String.format(Locale.ENGLISH, "geo:0,0?q=%f,%f", mTLocation.getLatitude(), mTLocation.getLongitude());
                Uri uri = Uri.parse(formatted);

                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                i.setPackage("com.google.android.apps.maps");
                startActivity(i);

                break;
            }

            case R.id.action_refresh: {

                fetchData();

                break;
            }


        }


        return true;
    }

    private void fetchData() {

        mLiveData.clear();
        mAdapter.notifyDataSetChanged();

        mProgressBar.setVisibility(View.VISIBLE);
        Call<LiveDataParent> call = mCall.clone();
        call.enqueue(new Callback<LiveDataParent>() {

            @Override
            public void onResponse(Call<LiveDataParent> call, final Response<LiveDataParent> response) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (response.body() == null) {
                            return;
                        }

                        mLiveData.addAll(response.body().getData());
                        mAdapter.notifyDataSetChanged();


                        if (response.body().getData().size() == 0) {

                            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Nothing due in the next 90 mins", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
                            snackbar.show();

                        }

                    }
                });

                mProgressBar.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onFailure(Call<LiveDataParent> call, Throwable t) {

                mProgressBar.setVisibility(View.INVISIBLE);

                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Error retrieving data", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
                snackbar.show();

            }
        });


    }

    @Override
    public void hearShake() {

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(200);
        }

        fetchData();

    }

    private void addAsFavourite() {

        SharedPreferences preferences = getSharedPreferences("favourites", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String favsJson = preferences.getString("favourites.json", "[]");

        ArrayList<TLocation> favs = mGson.fromJson(favsJson, new TypeToken<ArrayList<TLocation>>() {
        }.getType());

        for (TLocation f : favs) {

            if (f.getStopid().equals(mTLocation.getStopid())) {

                Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "This is already a favourite", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
                snackbar.show();
                return;

            }

        }

        favs.add(mTLocation);

        String newJson = mGson.toJson(favs);
        editor.putString("favourites.json", newJson);
        editor.apply();

        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Added to favourites", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("Ok", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.colorPrimary));
        snackbar.show();


    }

    private void getTLocationFromIntent() {

        String stopid = getIntent().getStringExtra("stopid");
        InputStream inputStream = getResources().openRawResource(R.raw.locations);
        InputStreamReader reader = new InputStreamReader(inputStream);

        TLocationParent parent = mGson.fromJson(reader, TLocationParent.class);

        for (TLocation location : parent.getLocations()) {

            if (location.getStopid().equals(stopid)) {
                mTLocation = location;

                return;
            }

        }


    }

}
