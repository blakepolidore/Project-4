package blake.com.project4.apicalls;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Raiders on 4/29/16.
 */
public interface FoursquareAPIService {

    @GET("search")
    Call<> getTemperature(@Query("client_id") String clientID, @Query("client_secret") String clientSecret);
}
