package ie.dylancaulfield.realtimeinfo.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import ie.dylancaulfield.realtimeinfo.models.TLocation;

public class Favourites {

    private static Gson gson = new Gson();

    public static ArrayList<TLocation> getFavourites(InputStream inputStream) {

        InputStreamReader reader = new InputStreamReader(inputStream);

        ArrayList<TLocation> favs = gson.fromJson(reader, new TypeToken<ArrayList<TLocation>>() {
        }.getType());

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return favs;

    }

    public static void setFavourites(OutputStream outputStream, ArrayList<TLocation> favs) {

        OutputStreamWriter writer = new OutputStreamWriter(outputStream);

        String json = gson.toJson(favs);

        try {
            writer.write(json);
            writer.flush();
            writer.close();

            outputStream.close();

        } catch (IOException e) {
            Log.e("Realtimeinfo/Favs", e.toString());
        }


    }

}
