package ie.dylancaulfield.realtimeinfo.activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Toast;

import com.google.gson.Gson;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.models.TLocation;

public class LocationActivity extends AppCompatActivity {

    private TLocation mTLocation;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        String json = getIntent().getStringExtra("tlocation");
        mTLocation = gson.fromJson(json, TLocation.class);


        Toast.makeText(getApplicationContext(), mTLocation.getName(), Toast.LENGTH_SHORT).show();

    }
}
