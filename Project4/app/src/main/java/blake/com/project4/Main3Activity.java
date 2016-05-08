package blake.com.project4;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.Collections;
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

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Main Activity";

    //region views
    private NavigationView navigationView;
    private ScrollView scrollView;
    private TextView seekbarProgress;
    private EditText locationEditText;
    private SeekBar radiusSeekbar;
    private Switch deviceLocationSwitch;
    private Switch foodSwitch;
    private Switch drinkSwitch;
    private Switch locationsSwitch;
    private Switch eventsSwitch;
    private ImageButton dislikeButton;
    private ImageButton likeButton;
    private SwipeFlingAdapterView flingContainer;
    private FoursquareAPIService foursquareAPIService;
    private LinkedList<Cards> cardsList;
    private ArrayAdapter<Cards> cardsArrayAdapter;
    private Button logOut;
    //endregion views

    //region intent strings
    public static final String TITLE_TEXT = "TITLE TEXT";
    public static final String LOCATION_TEXT = "LOCATION TEXT";
    public static final String WEBSITE_TEXT = "WEBSITE TEXT";
    public static final String PHONE_TEXT = "PHONE TEXT";
    public static final String DESCRIPTION_TEXT = "DESCRIPTION TEXT";
    public static final String IMAGE_TEXT = "IMAGE TEXT";
    public static final String CATEGORY_TEXT = "CATEGORY TEXT";
    //endregion intent strings

    //region googlelocation
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private String latitude;
    private String longitude;
    //endregion googlelocation

    //region firebase
    Firebase firebaseRef;
    Firebase firebaseCards;
    //endregion firebase

    //region sharedpreferences
    private SharedPreferences sharedPreferences;
    //endregion sharedpreferences

    //region switch booleans
    private boolean deviceLocationToggle = true;
    private boolean foodQueryToggle = true;
    private boolean drinkQueryToggle = true;
    private boolean locationQueryToggle = true;
    private boolean eventsQueryToggle = true;
    //endregion switch booleans

    //region boolean codes
    private final String FOOD_BOOLEAN_CODE = "food";
    private final String DRINK_BOOLEAN_CODE = "drink";
    private final String LOCATION_BOOLEAN_CODE = "location";
    private final String EVENTS_BOOLEAN_CODE = "events";
    private final String DEVICE_LOCATION_BOOLEAN_CODE = "device";
    //endregion boolean codes

    //region user input locations
    private String locationInput;
    private final String LOCATION_INPUT_CODE = "user input";
    private String locationForQuery;
    //endregion user input locations

    //region seekbar
    private int seekBarValue;
    private final String SEEKBAR_CODE = "seekbar";
    //endregion seek bar

    //region permissions
    private int PERMISSION_ACCESS_COARSE_LOCATION = 22;
    public static int INTENT_FOR_RESULT = 23;
    //endregion permission

    //region login fragment
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    //endregion login fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        String userID = getAuthData();

        firebaseRef = new Firebase("https://datemate.firebaseio.com/users/" + userID);
        firebaseCards = firebaseRef.child("cards");
//        toolbar.setLogo(R.drawable.ic_action_icon);
//        toolbar.setLogoDescription("DateMate");
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

//        LoginFragment loginFragment = new LoginFragment();
//        fragmentManager = getSupportFragmentManager();
//        fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.login_fragment_container, loginFragment);
//        fragmentTransaction.commit();

        checkPermissions();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.syncState();
        setViews();
        locationEditText.setEnabled(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setSeekBar();
        checkVenueSwitches(foodSwitch);
        checkVenueSwitches(drinkSwitch);
        checkVenueSwitches(locationsSwitch);
        checkVenueSwitches(eventsSwitch);

        cardsList = new LinkedList<>();
        Cards cards = new Cards();
        cards.setImageUrl("http://www.blastr.com/sites/blastr/files/Marvel-Civil-War-alternate-poster.jpg");
        cards.setLocation("At A Theater Near You!");
        cards.setTitle("Captain America: Civil War");
        cards.setCategory("Movie");
        cardsList.add(cards);

        setGoogleServices();
        toggleLocationUIChoice();

        cardsArrayAdapter = new CardsAdapter(this, cardsList);

        intializeCardSwipes();
        setCardClickListener();
        setLikeButton();
        setDislikeButton();
        setLogOut();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
    }

    private void setViews() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        scrollView = (ScrollView) navigationView.findViewById(R.id.nav_scrollview);
        locationEditText = (EditText) scrollView.findViewById(R.id.location_editText);
        radiusSeekbar = (SeekBar) scrollView.findViewById(R.id.search_radius_seekbar);
        deviceLocationSwitch = (Switch) scrollView.findViewById(R.id.phone_location_switch);
        foodSwitch = (Switch) scrollView.findViewById(R.id.food_search_switch);
        drinkSwitch = (Switch) scrollView.findViewById(R.id.drink_search_switch);
        locationsSwitch = (Switch) scrollView.findViewById(R.id.activities_search_switch);
        eventsSwitch = (Switch) scrollView.findViewById(R.id.events_search_switch);
        logOut = (Button) scrollView.findViewById(R.id.logoutButton);
        seekbarProgress = (TextView) scrollView.findViewById(R.id.seekbar_progress);
        dislikeButton = (ImageButton) findViewById(R.id.dislikeButton);
        likeButton = (ImageButton) findViewById(R.id.likeButton);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            setStartLocationOption();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.liked_activities:
                Intent likedVenuesIntent = new Intent(Main3Activity.this, LikedActivity.class);
                startActivity(likedVenuesIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setSeekBar() {
        radiusSeekbar.setMax(100);
        radiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarProgress.setText(progress + " miles");
                seekBarValue = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void toggleLocationUIChoice() {
        deviceLocationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceLocationSwitch.isChecked()) {
                    deviceLocationToggle = true;
                    locationEditText.setEnabled(false);
                } else {
                    deviceLocationToggle = false;
                    locationEditText.setEnabled(true);
                }
            }
        });
    }

    private void checkVenueSwitches(final Switch s) {
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggled = s.isChecked();
                switch (s.getId()) {
                    case R.id.food_search_switch:
                        if (toggled) {
                            foodQueryToggle = true;
                        } else {
                            foodQueryToggle = false;
                        }
                        break;
                    case R.id.drink_search_switch:
                        if (toggled) {
                            drinkQueryToggle = true;
                        } else {
                            drinkQueryToggle = false;
                        }
                        break;
                    case R.id.activities_search_switch:
                        if (toggled) {
                            locationQueryToggle = true;
                        } else {
                            locationQueryToggle = false;
                        }
                        break;
                    case R.id.events_search_switch:
                        if (toggled) {
                            eventsQueryToggle = true;
                        } else {
                            eventsQueryToggle = false;
                        }
                        break;
                    default:
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FOOD_BOOLEAN_CODE, foodQueryToggle);
        editor.putBoolean(DRINK_BOOLEAN_CODE, drinkQueryToggle);
        editor.putBoolean(LOCATION_BOOLEAN_CODE, locationQueryToggle);
        editor.putBoolean(EVENTS_BOOLEAN_CODE, eventsQueryToggle);
        editor.putBoolean(DEVICE_LOCATION_BOOLEAN_CODE, deviceLocationToggle);
        if (!deviceLocationToggle) {
            locationInput = locationEditText.getText().toString();
        }
        editor.putString(LOCATION_INPUT_CODE, locationInput);
        editor.putInt(SEEKBAR_CODE, seekBarValue);
    }

    @Override
    protected void onResume() {
        super.onResume();
        foodQueryToggle = sharedPreferences.getBoolean(FOOD_BOOLEAN_CODE, foodQueryToggle);
        foodSwitch.setChecked(foodQueryToggle);
        drinkQueryToggle = sharedPreferences.getBoolean(DRINK_BOOLEAN_CODE, drinkQueryToggle);
        drinkSwitch.setChecked(drinkQueryToggle);
        locationQueryToggle = sharedPreferences.getBoolean(LOCATION_BOOLEAN_CODE, locationQueryToggle);
        locationsSwitch.setChecked(locationQueryToggle);
        eventsQueryToggle = sharedPreferences.getBoolean(EVENTS_BOOLEAN_CODE, eventsQueryToggle);
        eventsSwitch.setChecked(eventsQueryToggle);
        deviceLocationToggle = sharedPreferences.getBoolean(DEVICE_LOCATION_BOOLEAN_CODE, deviceLocationToggle);
        deviceLocationSwitch.setChecked(deviceLocationToggle);
        if (!deviceLocationToggle) {
            locationEditText.setText(sharedPreferences.getString(LOCATION_INPUT_CODE, locationInput));
        }
        int seekBarValue = sharedPreferences.getInt(SEEKBAR_CODE, 25);
        if (seekBarValue == 0) {
            radiusSeekbar.setProgress(25);
        } else {
            radiusSeekbar.setProgress(sharedPreferences.getInt(SEEKBAR_CODE, seekBarValue));
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

    /**
     * Method makes API call to yelp and retrieves data based on search query and users coordinates
     */
    private void yelpAPISearchCallCoordinates(String query) {

        Map<String, String> params = new HashMap<>();
        params.put("term", query);
        params.put("sort", "2");
        params.put("radius_filter", convertRadiusToKM());

        YelpAPIFactory apiFactory = new YelpAPIFactory(Keys.YELP_CONSUMER_KEY, Keys.YELP_CONSUMER_SECRET, Keys.YELP_TOKEN, Keys.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        CoordinateOptions coordinateOptions = CoordinateOptions.builder().latitude(Double.valueOf(latitude)).longitude(Double.valueOf(longitude)).build();
        Call<SearchResponse> call = yelpAPI.search(coordinateOptions, params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                for (int i = 0; i < 20; i++) {
                    String name = response.body().businesses().get(i).name();
                    String url = response.body().businesses().get(i).url();
                    String phone = response.body().businesses().get(i).displayPhone();
                    String snippet = response.body().businesses().get(i).snippetText();
                    String address = response.body().businesses().get(i).location().displayAddress().get(0);
                    String city = response.body().businesses().get(i).location().city();
                    String fullAddress = address + ", " + city;
                    String imageURL = response.body().businesses().get(i).imageUrl();
                    imageURL = imageURL.replaceAll("ms", "o");
                    String category = response.body().businesses().get(i).categories().get(0).name();
                    createYelpCards(name, fullAddress, category, imageURL, url, phone, snippet);
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

            }
        });
    }

    /**
     * Method makes API call to yelp and retrieves data based on search query and users coordinates
     */
    private void yelpAPISearchCallLocation(String query) {

        Map<String, String> params = new HashMap<>();
        params.put("term", query);
        params.put("sort", "2");
        params.put("radius_filter", convertRadiusToKM());

        YelpAPIFactory apiFactory = new YelpAPIFactory(Keys.YELP_CONSUMER_KEY, Keys.YELP_CONSUMER_SECRET, Keys.YELP_TOKEN, Keys.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        Call<SearchResponse> call = yelpAPI.search(locationForQuery, params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                for (int i = 0; i < 20; i++) {
                    String name = response.body().businesses().get(i).name();
                    String url = response.body().businesses().get(i).url();
                    String phone = response.body().businesses().get(i).displayPhone();
                    String snippet = response.body().businesses().get(i).snippetText();
                    String address = response.body().businesses().get(i).location().displayAddress().get(0);
                    String city = response.body().businesses().get(i).location().city();
                    String fullAddress = address + ", " + city;
                    String imageURL = response.body().businesses().get(i).imageUrl();
                    imageURL = imageURL.replaceAll("ms", "o");
                    String category = response.body().businesses().get(i).categories().get(0).name();
                    createYelpCards(name, fullAddress, category, imageURL, url, phone, snippet);
                }
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

            }
        });
    }

    /**
     * Method makes a call to foursquare and gets data based on search query and using a user specified location
     */
    private void foursquareAPICallNear(String query) {
        Retrofit retrofitFourSquare = new Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/venues/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foursquareAPIService = retrofitFourSquare.create(FoursquareAPIService.class);

        Call<blake.com.project4.foursquareModel.Root> call =
                foursquareAPIService.searchWithNear(locationForQuery, query, Keys.FOURSQUARE_ID, Keys.FOURSQUARE_SECRET, "20160501", "foursquare");
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
     * Method makes a call to foursquare and gets data based on search query and using a user specified location
     */
    private void foursquareAPICallLL(String query) {
        Retrofit retrofitFourSquare = new Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/venues/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        foursquareAPIService = retrofitFourSquare.create(FoursquareAPIService.class);

        Call<blake.com.project4.foursquareModel.Root> call =
                foursquareAPIService.searchWithLL(locationForQuery,query, Keys.FOURSQUARE_ID, Keys.FOURSQUARE_SECRET, "20160501", "foursquare");
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
     * Creates cards objects from yelp api call and puts in cardslist
     * @param name
     * @param address
     * @param category
     * @param imageUrl
     */
    private void createYelpCards(String name, String address, String category, String imageUrl, String website, String phone, String description) {
        Cards cards = new Cards();
        cards.setTitle(name);
        cards.setLocation(address);
        cards.setImageUrl(imageUrl);
        cards.setCategory(category);
        cards.setWebsite(website);
        cards.setPhone(phone);
        cards.setDescription(description);
        cardsList.add(cards);
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

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Firebase firebaseRef = firebaseCards.push();
                cardsList.get(0).setUniqueFirebaseKey(firebaseRef.getKey());
                firebaseRef.setValue(cardsList.get(0));
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                //setStartLocationOption();
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
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent venueIntent = new Intent(Main3Activity.this, VenueActivity.class);
                venueIntent.putExtra(TITLE_TEXT, cardsList.get(0).getTitle());
                venueIntent.putExtra(CATEGORY_TEXT, cardsList.get(0).getCategory());
                venueIntent.putExtra(IMAGE_TEXT, cardsList.get(0).getImageUrl());
                venueIntent.putExtra(LOCATION_TEXT, cardsList.get(0).getLocation());
                venueIntent.putExtra(DESCRIPTION_TEXT, cardsList.get(0).getDescription());
                venueIntent.putExtra(PHONE_TEXT, cardsList.get(0).getPhone());
                venueIntent.putExtra(WEBSITE_TEXT, cardsList.get(0).getWebsite());
                startActivityForResult(venueIntent, INTENT_FOR_RESULT);

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
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latitude = String.valueOf(lastLocation.getLatitude());
            longitude = String.valueOf(lastLocation.getLongitude());
            locationForQuery = latitude + "," + longitude;
            if (cardsList.size() == 1) {
                setStartLocationOption();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(Main3Activity.this, "Cannot Connect to Google Location Services", Toast.LENGTH_SHORT).show();
    }

    private void setDislikeButton() {
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });
    }

    private void setLikeButton() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });
    }

    private String getAuthData() {
        Firebase firebase = new Firebase("https://datemate.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
    }

    private void setStartLocationOption() {
        if (deviceLocationSwitch.isChecked()) {
            locationForQuery = latitude + "," + longitude;
            cardsList.clear();
            makeCoordinateAPICalls();
        } else {
            locationForQuery = locationEditText.getText().toString();
            if (!locationForQuery.isEmpty()) {
                cardsList.clear();
                makeUserLocationInputAPICalls();
            }
        }
    }

    private void makeCoordinateAPICalls() {
        if (foodQueryToggle) {
            yelpAPISearchCallCoordinates("food");
            //foursquareAPICallLL("restaurants");
            Collections.shuffle(cardsList);
        }
        if (drinkQueryToggle) {
            yelpAPISearchCallCoordinates("drinks");
            //foursquareAPICallLL("bars");
            Collections.shuffle(cardsList);
        }
        if (eventsQueryToggle) {
            yelpAPISearchCallCoordinates("Movies");
            yelpAPISearchCallCoordinates("music");
            yelpAPISearchCallCoordinates("concert");
            yelpAPISearchCallCoordinates("auditorium");
            //foursquareAPICallLL("Movies");
            Collections.shuffle(cardsList);
        }
        if (locationQueryToggle) {
            yelpAPISearchCallCoordinates("park");
            yelpAPISearchCallCoordinates("museum");
            //foursquareAPICallLL("park");
            //foursquareAPICallLL("museum");
            Collections.shuffle(cardsList);
        }

    }

    private void makeUserLocationInputAPICalls() {
        if (foodQueryToggle) {
            yelpAPISearchCallLocation("food");
            Collections.shuffle(cardsList);
        }
        if (drinkQueryToggle) {
            yelpAPISearchCallLocation("drinks");
            Collections.shuffle(cardsList);
        }
        if (eventsQueryToggle) {
            yelpAPISearchCallLocation("Movies");
            yelpAPISearchCallLocation("music");
            yelpAPISearchCallLocation("concert");
            yelpAPISearchCallLocation("auditorium");
            Collections.shuffle(cardsList);
        }
        if (locationQueryToggle) {
            yelpAPISearchCallLocation("park");
            yelpAPISearchCallLocation("museum");
            Collections.shuffle(cardsList);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            Boolean venueBoolean = data.getBooleanExtra(VenueActivity.IF_LIKE_INTENT, false);
            if (venueBoolean) {
                flingContainer.getTopCardListener().selectRight();
                firebaseCards.push().setValue(cardsList.get(0));
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            } else {
                flingContainer.getTopCardListener().selectLeft();
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    private String convertRadiusToKM() {
        int radiusValue = 0;
        if (seekBarValue > 25) {
            radiusValue = 25 * 1609;
        } else {
            radiusValue = seekBarValue * 1609;
        }
        return String.valueOf(radiusValue);
    }

    private void setLogOut() {
        final Firebase firebase = new Firebase("https://datemate.firebaseio.com");
        final AuthData authData = firebase.getAuth();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authData != null) {
                    firebase.unauth();
                    LoginManager.getInstance().logOut();
                    Intent loginIntent = new Intent(Main3Activity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }
}
