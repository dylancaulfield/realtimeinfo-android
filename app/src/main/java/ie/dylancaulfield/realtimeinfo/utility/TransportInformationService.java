package ie.dylancaulfield.realtimeinfo.utility;

import ie.dylancaulfield.realtimeinfo.models.LiveDataParent;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TransportInformationService {

    @GET("cgi-bin/rtpi/realtimebusinformation?stopid={id}&format=json")
    Call<LiveDataParent> getData(@Path("id") String id);

}
