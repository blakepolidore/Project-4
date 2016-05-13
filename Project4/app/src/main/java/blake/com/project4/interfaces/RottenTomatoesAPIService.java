package blake.com.project4.interfaces;

import blake.com.project4.models.rottenTomatoesModel.rottenTomatoesModel.RTRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Raiders on 5/7/16.
 */
public interface RottenTomatoesAPIService {

    @GET("in_theaters.json")
    Call<RTRoot> searchInTheaters(@Query("apikey") String apiKey, @Query("page_limit") String limit);
}
