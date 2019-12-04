package ie.dylancaulfield.realtimeinfo.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.ContentLoadingProgressBar;


import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.adapters.LiveDataListViewAdapter;
import ie.dylancaulfield.realtimeinfo.models.LiveData;
import ie.dylancaulfield.realtimeinfo.models.LiveDataParent;
import ie.dylancaulfield.realtimeinfo.models.TLocation;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

        String json = getIntent().getStringExtra("tlocation");
        mTLocation = mGson.fromJson(json, TLocation.class);

        mProgressBar = findViewById(R.id.progressBar_livedata);

        Toolbar toolbar = findViewById(R.id.toolbar_location);
        toolbar.setTitle(mTLocation.getName());
        setSupportActionBar(toolbar);

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

            case R.id.action_info: {


                break;
            }

            case R.id.action_refresh: {

                fetchData();

                break;
            }


        }


        return true;
    }

    private synchronized void fetchData() {

        mLiveData.clear();
        mAdapter.notifyDataSetChanged();

        mProgressBar.setVisibility(View.VISIBLE);
        Call<LiveDataParent> call = mCall.clone();
        call.enqueue(new Callback<LiveDataParent>() {

            @Override
            public void onResponse(Call<LiveDataParent> call, final Response<LiveDataParent> response) {


                for (LiveData l : response.body().getData()) {
                    mLiveData.add(l);
                    mAdapter.notifyDataSetChanged();
                }

                mProgressBar.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onFailure(Call<LiveDataParent> call, Throwable t) {

                mProgressBar.setVisibility(View.INVISIBLE);
                CoordinatorLayout coordinatorLayout = findViewById(R.id.parent_coordinator_location);

                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error retrieving data", Snackbar.LENGTH_INDEFINITE);
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
        if (vibrator != null){
            vibrator.vibrate(200);
        }

        fetchData();

    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchData();
    }
}
