package blake.com.project4.apicalls;

import blake.com.project4.models.fourSquareModels.Root;
import blake.com.project4.models.fourSquareModels.fourSquarePhotoModel.PhotoRoot;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Raiders on 4/29/16.
 * Makes api calls to the foursquare server
 */
public interface FoursquareAPIService {

    @GET("search")
    Call<Root> searchWithNear(@Query("near") String near, @Query("query") String query,
                              @Query("client_id") String clientID, @Query("client_secret") String clientSecret,
                              @Query("v") String date, @Query("m") String responseMode);

    @GET("search")
    Call<Root> searchWithLL(@Query("ll") String near, @Query("query") String query,
                            @Query("client_id") String clientID, @Query("client_secret") String clientSecret,
                            @Query("v") String date, @Query("m") String responseMode);

    @GET("{id}/photos")
    Call<PhotoRoot> photoSearch(@Path("id") String id, @Query("client_id") String clientID,
                                @Query("client_secret") String clientSecret, @Query("v") String date,
                                @Query("m") String responseMode);
}
