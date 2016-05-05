package blake.com.project4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import blake.com.project4.apicalls.FoursquareAPIService;
import blake.com.project4.cardModelAndAdapter.Cards;
import blake.com.project4.cardModelAndAdapter.CardsAdapter;
import blake.com.project4.foursquareModel.Root;
import blake.com.project4.foursquareModel.foursquarePhotoModel.PhotoRoot;
import blake.com.project4.swipefling.SwipeFlingAdapterView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Main activity contains the swiped cards the user can play with.
 * User can choose to save or dislike each card.
 * Settings and liked cards can be accessed from this activity.
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private ImageButton dislikeButton;
    private ImageButton likeButton;
    private SwipeFlingAdapterView flingContainer;
    private FoursquareAPIService foursquareAPIService;
    public static final String TITLE_TEXT = "TITLE TEXT";
    private LinkedList<Cards> cardsList;
    private ArrayAdapter<Cards> cardsArrayAdapter;

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private String latitude;
    private String longitude;

    Firebase firebaseRef;
    Firebase firebaseCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        setViews();
        String userID = getAuthData();
        firebaseRef = new Firebase("https://datemate.firebaseio.com/users/" + userID);
        firebaseCards = firebaseRef.child("cards");
//        toolbar.setLogo(R.drawable.nyt_logo);
//        toolbar.setLogoDescription(getResources().getString(R.string.logo_desc));
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        cardsList = new LinkedList<>();
        Cards cards = new Cards();
        cards.setImageUrl("https://pbs.twimg.com/profile_images/672132153900183553/zVFIAIDi.jpg");
        cards.setLocation("New York");
        cards.setTitle("Park");
        cardsList.add(cards);

        setGoogleServices();

        //yelpAPISearchCall();
        foursquareAPICall();

        cardsArrayAdapter = new CardsAdapter(this, cardsList);

        intializeCardSwipes();
        setCardClickListener();
        setLikeButton();
        setDislikeButton();
    }

    /**
     * Creates toolbar for the activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Creates methods for when items are selected in toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.liked_activities:
                Intent likedVenuesIntent = new Intent(MainActivity.this, LikedActivity.class);
                startActivity(likedVenuesIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setViews() {
        dislikeButton = (ImageButton) findViewById(R.id.dislikeButton);
        likeButton = (ImageButton) findViewById(R.id.likeButton);
    }

    /**
     * Method makes API call to yelp and retrieves data based on search query
     */
    private void yelpAPISearchCall() {

        Map<String, String> params = new HashMap<>();
        params.put("term", "food");
        params.put("sort", "2");
        params.put("radius_filter", "10000");

        YelpAPIFactory apiFactory = new YelpAPIFactory(Keys.YELP_CONSUMER_KEY, Keys.YELP_CONSUMER_SECRET, Keys.YELP_TOKEN, Keys.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                for (int i = 0; i < 20; i++) {
                    String name = response.body().businesses().get(i).name();
                    String url = response.body().businesses().get(i).url();
                    String phone = response.body().businesses().get(i).displayPhone();
                    String address = response.body().businesses().get(i).location().displayAddress().get(0);
                    String imageURL = response.body().businesses().get(i).imageUrl();
                    imageURL.replaceAll("ms", "ls");
                    String category = response.body().businesses().get(i).categories().get(0).name();
                    Cards cards = new Cards();
                    cards.setTitle(name);
                    cards.setLocation(address);
                    cards.setImageUrl(imageURL);
                    cards.setCategory(category);
                    cardsList.add(i,cards);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

            }
        });
    }

    /**
     * Method makes a call to foursquare and gets data based on search query
     */
    private void foursquareAPICall() {
                Retrofit retrofitFourSquare = new Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/venues/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foursquareAPIService = retrofitFourSquare.create(FoursquareAPIService.class);

        Call<blake.com.project4.foursquareModel.Root> call =
                foursquareAPIService.searchWithNear("San Francisco", Keys.FOURSQUARE_ID, Keys.FOURSQUARE_SECRET, "20160501", "foursquare");
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<blake.com.project4.foursquareModel.Root> call, Response<blake.com.project4.foursquareModel.Root> response) {
                for (int i = 0; i < response.body().getResponse().getVenues().length; i++) {
                    String name = response.body().getResponse().getVenues()[i].getName();
                    String address = response.body().getResponse().getVenues()[i].getLocation().getFormattedAddress()[0];
                    String category = "";
                    if (response.body().getResponse().getVenues()[i].getCategories().length > 0) {
                        category = response.body().getResponse().getVenues()[i].getCategories()[0].getName();
                    }
                    String id = response.body().getResponse().getVenues()[i].getId();
                    getFourSquareImages(id, name, address, category);
                }
            }

            @Override
            public void onFailure(Call<blake.com.project4.foursquareModel.Root> call, Throwable t) {
                Log.d("MAIN ACTIVITY", "Test Failed");
                t.printStackTrace();
            }
        });
    }

    /**
     * Method makes an api call to foursquare, gets photos based on venue id, then put the items into
     * the list to be displayed to user
     * @param id
     * @param name
     * @param address
     * @param category
     */
    private void getFourSquareImages(String id, final String name, final String address, final String category) {
        final String[] image = new String[2];
        Call<PhotoRoot> photoRootCall = foursquareAPIService.photoSearch(id, Keys.FOURSQUARE_ID, Keys.FOURSQUARE_SECRET, "20160501", "foursquare");
        photoRootCall.enqueue(new Callback<PhotoRoot>() {
            @Override
            public void onResponse(Call<PhotoRoot> call, Response<PhotoRoot> response) {
                if (response.body().getResponse().getPhotos().getItems().length > 0) {
                    String prefix = response.body().getResponse().getPhotos().getItems()[0].getPrefix();
                    String suffix = response.body().getResponse().getPhotos().getItems()[0].getSuffix();
                    image[0] = prefix;
                    image[1] = suffix;
                }
                Cards cards = new Cards();
                cards.setTitle(name);
                cards.setLocation(address);
                String imageURL = image[0] + "cap300" + image[1];
                cards.setImageUrl(imageURL);
                cards.setCategory(category);
                cardsList.add(cards);
            }

            @Override
            public void onFailure(Call<PhotoRoot> call, Throwable t) {
                Log.d("onFailure", "Photo failure");
            }
        });
    }

    /**
     * Overrides the methods for when cards are swiped
     */
    private void intializeCardSwipes() {
        //set the listener and the adapter
        flingContainer.setAdapter(cardsArrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
                firebaseCards.push().setValue(cardsList.get(0));
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });
    }

    /**
     * Initializes the card click listener
     */
    private void setCardClickListener() {
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent venueIntent = new Intent(MainActivity.this, VenueActivity.class);
                venueIntent.putExtra(TITLE_TEXT, cardsList.get(0).getTitle());
                venueIntent.putExtra(TITLE_TEXT, cardsList.get(0).getCategory());
                venueIntent.putExtra(TITLE_TEXT, cardsList.get(0).getImageUrl());
                venueIntent.putExtra(TITLE_TEXT, cardsList.get(0).getLocation());
                startActivity(venueIntent);

            }
        });
    }

    private void setGoogleServices() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this, this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        lastLocation = LocationServices.FusedLocationApi.getLastLocation(
//                googleApiClient);
//        if (lastLocation != null) {
//            latitude = String.valueOf(lastLocation.getLatitude());
//            longitude = String.valueOf(lastLocation.getLongitude());
//            Log.d("OnConnected", latitude + longitude);
//        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latitude = String.valueOf(lastLocation.getLatitude());
            longitude = String.valueOf(lastLocation.getLongitude());
            Log.d("OnConnected", latitude + longitude);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this, "Cannot Connect to Google Location Services", Toast.LENGTH_SHORT).show();
    }

    private void setDislikeButton() {
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setLikeButton() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseCards.push().setValue(cardsList.get(0));
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    private String getAuthData() {
        Firebase firebase = new Firebase("https://datemate.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }
}

