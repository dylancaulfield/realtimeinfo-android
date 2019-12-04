package ie.dylancaulfield.realtimeinfo.utility;

import ie.dylancaulfield.realtimeinfo.models.LiveDataParent;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LiveDataService {

    @GET("cgi-bin/rtpi/realtimebusinformation?format=json")
    Call<LiveDataParent> getData(@Query("stopid") String id);

}
