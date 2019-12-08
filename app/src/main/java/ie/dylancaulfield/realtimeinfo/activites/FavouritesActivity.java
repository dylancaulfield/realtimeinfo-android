package ie.dylancaulfield.realtimeinfo.activites;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ie.dylancaulfield.realtimeinfo.R;
import ie.dylancaulfield.realtimeinfo.adapters.LocationsListViewAdapter;
import ie.dylancaulfield.realtimeinfo.models.TLocation;
import ie.dylancaulfield.realtimeinfo.models.TLocationParent;

public class FavouritesActivity extends AppCompatActivity implements ListView.OnItemClickListener, ListView.OnItemLongClickListener, PopupMenu.OnMenuItemClickListener {

    private ArrayList<TLocation> mFavourites = new ArrayList<>();
    private Gson mGson = new Gson();
    private LocationsListViewAdapter mAdapter;
    private int mIndexOfLongHold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        Toolbar toolbar = findViewById(R.id.toolbar_favourites);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        ListView listView = findViewById(R.id.listView_favourites);
        mAdapter = new LocationsListViewAdapter(getApplicationContext(), R.layout.list_location, mFavourites);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        loadFavourites();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: {

                finish();

            }

        }

        return true;

    }

    private void loadFavourites() {

        SharedPreferences preferences = getSharedPreferences("favourites", MODE_PRIVATE);
        String json = preferences.getString("favourites.json", "[]");

        ArrayList<TLocation> tLocations = mGson.fromJson(json, new TypeToken<ArrayList<TLocation>>() {
        }.getType());
        mFavourites.addAll(tLocations);
        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TLocation tLocation = mFavourites.get(position);

        Intent i = new Intent(getApplicationContext(), LocationActivity.class);
        i.putExtra("stopid", tLocation.getStopid());
        startActivity(i);


    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        mIndexOfLongHold = position;

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.favourite_popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();

        return true;

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete: {

                deleteFavourite();
                break;

            }

            case R.id.action_rename: {

                renameFavourite();
                break;

            }

        }


        return true;
    }

    private void deleteFavourite() {

        SharedPreferences preferences = getSharedPreferences("favourites", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        mFavourites.remove(mIndexOfLongHold);
        mAdapter.notifyDataSetChanged();

        String newJson = mGson.toJson(mFavourites);
        editor.putString("favourites.json", newJson);
        editor.apply();

    }

    private void renameFavourite() {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(FavouritesActivity.this);

        View view = getLayoutInflater().inflate(R.layout.dialog_edittext, null);

        final TextInputEditText input = view.findViewById(R.id.textinput_rename_favourite);
        builder.setView(view);

        builder.setPositiveButton("Rename", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (input.getText().toString().trim().equals("")){
                    return;
                }

                SharedPreferences preferences = getSharedPreferences("favourites", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                mFavourites.get(mIndexOfLongHold).setFullname(input.getText().toString());
                mFavourites.get(mIndexOfLongHold).setDisplaystopid(input.getText().toString());
                mAdapter.notifyDataSetChanged();

                String newJson = mGson.toJson(mFavourites);
                editor.putString("favourites.json", newJson);
                editor.apply();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }

}
