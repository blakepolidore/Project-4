package blake.com.project4.apicalls;

import blake.com.project4.foursquareModel.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Raiders on 4/29/16.
 */
public interface FoursquareAPIService {

    @GET("search")
    Call<Response> search(@Query("near") String near, @Query("client_id") String clientID,
                          @Query("client_secret") String clientSecret, @Query("v") String date,
                          @Query("m") String responseMode);
}
