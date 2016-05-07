package blake.com.project4.apicalls;

import blake.com.project4.foursquareModel.Root;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Raiders on 5/7/16.
 */
public interface RottenTomatoesAPIService {

    @GET("in_theaters.json")
    Call<Root> searchInTheaters(@Query("apikey") String apiKey, @Query("page_limit") String limit);
}
