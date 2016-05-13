package blake.com.project4.activities;

import android.Manifest;
import android.content.DialogInterface;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import blake.com.project4.CheckInternetConnection;
import blake.com.project4.GetFirebaseUniqueId;
import blake.com.project4.Keys;
import blake.com.project4.R;
import blake.com.project4.adapters.CardsAdapter;
import blake.com.project4.models.cardsModel.Cards;
import blake.com.project4.swipefling.SwipeFlingAdapterView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main Activity for the app
 * Has a nav drawer for the user queries
 * Shows cards based on use queries
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Main Activity";

    //region views
    private NavigationView navigationView;
    private ScrollView scrollView;
    private TextView seekbarProgress;
    private EditText locationEditText;
    private EditText userQueryEditText;
    private SeekBar radiusSeekbar;
    private Switch deviceLocationSwitch;
    private Switch restaurantSwitch;
    private Switch drinkSwitch;
    private Switch artsSwitch;
    private Switch activeSwitch;
    private ImageButton dislikeButton;
    private ImageButton likeButton;
    private SwipeFlingAdapterView flingContainer;
    private LinkedList<Cards> cardsList;
    private ArrayAdapter<Cards> cardsArrayAdapter;
    private Button logOut;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;
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
    private boolean isDeviceLocationToggle = true;
    private boolean isFoodQueryToggle = true;
    private boolean isDrinkQueryToggle = true;
    private boolean isArtsQueryToggle = true;
    private boolean isActiveQueryToggle = true;
    //endregion switch booleans

    //region boolean codes
    private final String FOOD_BOOLEAN_CODE = "food";
    private final String DRINK_BOOLEAN_CODE = "drink";
    private final String Arts_BOOLEAN_CODE = "arts";
    private final String ACTIVE_BOOLEAN_CODE = "active";
    private final String DEVICE_LOCATION_BOOLEAN_CODE = "device";
    //endregion boolean codes

    //region user input locations
    private final String LOCATION_INPUT_CODE = "user input";
    private String locationForQuery;
    //endregion user input locations

    //region user query
    private String userQuery;
    private final String USER_QUERY_CODE = "user query";
    //endregion user query

    //region seekbar
    private int seekBarValue;
    private final String SEEKBAR_CODE = "seekbar";
    //endregion seek bar

    //region permissions
    private int PERMISSION_ACCESS_COARSE_LOCATION = 22;
    public static int INTENT_FOR_RESULT = 23;
    //endregion permission

    //region counter
    private int timesAPICalledCoordinates = 0;
    private final String COORDINATES_COUNTER_KEY = "counter coordinates";
    private int timesAPICalledUserLocation = 0;
    private final String USERPICK_COUNTER_KEY = "counter user location";
    //endregion counter

    //region duplicates
    LinkedList<String> duplicateList = new LinkedList<>();
    //endregion duplicates

    //region api call counts
    int callCount = 0;
    int numCalls = 0;
    //endregion api call counts

    //region no more results boolean
    private boolean noMoreResults = false;
    //endregion no more results boolean

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        String userID = GetFirebaseUniqueId.getAuthData();

        firebaseRef = new Firebase("https://datemate.firebaseio.com/users/" + userID);
        firebaseCards = firebaseRef.child("cards");

        setDrawer();
        setGoogleServices();
        checkPermissions();
        setViews();
        locationEditText.setEnabled(false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setSeekBar();
        checkVenueSwitches(restaurantSwitch);
        checkVenueSwitches(drinkSwitch);
        checkVenueSwitches(artsSwitch);
        checkVenueSwitches(activeSwitch);

        cardsList = new LinkedList<>();
        locationViewsEnabled();

        intializeCardSwipes();
        cardsArrayAdapter = new CardsAdapter(this, cardsList);
        setLogOut();
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the toolbar
     */
    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    /**
     * Sets the navigation drawer
     */
    private void setDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        toggle.syncState();
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     * Instantiate views in the activity
     */
    private void setViews() {
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        scrollView = (ScrollView) navigationView.findViewById(R.id.nav_scrollview);
        locationEditText = (EditText) scrollView.findViewById(R.id.location_editText);
        userQueryEditText = (EditText) scrollView.findViewById(R.id.userQuery_editText);
        radiusSeekbar = (SeekBar) scrollView.findViewById(R.id.search_radius_seekbar);
        deviceLocationSwitch = (Switch) scrollView.findViewById(R.id.phone_location_switch);
        restaurantSwitch = (Switch) scrollView.findViewById(R.id.food_search_switch);
        drinkSwitch = (Switch) scrollView.findViewById(R.id.drink_search_switch);
        artsSwitch = (Switch) scrollView.findViewById(R.id.arts_search_switch);
        activeSwitch = (Switch) scrollView.findViewById(R.id.active_search_switch);
        logOut = (Button) scrollView.findViewById(R.id.logoutButton);
        seekbarProgress = (TextView) scrollView.findViewById(R.id.seekbar_progress);
        dislikeButton = (ImageButton) findViewById(R.id.dislikeButton);
        likeButton = (ImageButton) findViewById(R.id.likeButton);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        progressBar = (ProgressBar) findViewById(R.id.loading_progress_bar);
    }

    /**
     * Creates the api calls when the nav drawer is closed
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            drawer.closeDrawer(GravityCompat.START);
            cardsList.clear();
            cardsArrayAdapter.notifyDataSetChanged();
            setStartLocationOption();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Inflates the menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Creates intents when options are clicked on
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
            case R.id.information_main:
                setDialog(getString(R.string.information), getString(R.string.main_instructions), android.R.drawable.ic_dialog_info);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Sets and grabs the seek bar value and places into the textview and the seekBarValue int
     */
    private void setSeekBar() {
        radiusSeekbar.setMax(50);
        radiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarProgress.setText(progress + getString(R.string.miles));
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

    /**
     * Sets the on click for the location switch.
     * Disables or enables the edit text depeding on whether the user wants to use the phones
     * location or a custom location for the query
     */
    private void locationViewsEnabled() {
        deviceLocationSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceLocationSwitch.isChecked()) {
                    isDeviceLocationToggle = true;
                    locationEditText.setEnabled(false);
                } else {
                    isDeviceLocationToggle = false;
                    locationEditText.setEnabled(true);
                }
            }
        });
    }

    /**
     * Switches the associated booleans with the switch when the switch is toggled
     * @param s
     */
    private void checkVenueSwitches(final Switch s) {
        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toggled = s.isChecked();
                switch (s.getId()) {
                    case R.id.food_search_switch:
                        if (toggled) {
                            isFoodQueryToggle = true;
                        } else {
                            isFoodQueryToggle = false;
                        }
                        break;
                    case R.id.drink_search_switch:
                        if (toggled) {
                            isDrinkQueryToggle = true;
                        } else {
                            isDrinkQueryToggle = false;
                        }
                        break;
                    case R.id.arts_search_switch:
                        if (toggled) {
                            isArtsQueryToggle = true;
                        } else {
                            isArtsQueryToggle = false;
                        }
                        break;
                    case R.id.active_search_switch:
                        if (toggled) {
                            isActiveQueryToggle = true;
                        } else {
                            isActiveQueryToggle = false;
                        }
                        break;
                    default:
                }
            }
        });
    }

    /**
     * Navigation item selected override
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Places switch booleans and navigation drawer values into shared preferences
     */
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FOOD_BOOLEAN_CODE, isFoodQueryToggle);
        editor.putBoolean(DRINK_BOOLEAN_CODE, isDrinkQueryToggle);
        editor.putBoolean(Arts_BOOLEAN_CODE, isArtsQueryToggle);
        editor.putBoolean(ACTIVE_BOOLEAN_CODE, isActiveQueryToggle);
        editor.putBoolean(DEVICE_LOCATION_BOOLEAN_CODE, isDeviceLocationToggle);
        editor.putInt(COORDINATES_COUNTER_KEY, timesAPICalledCoordinates);
        editor.putInt(USERPICK_COUNTER_KEY, timesAPICalledUserLocation);
        if (!isDeviceLocationToggle) {
            locationForQuery = locationEditText.getText().toString();
            editor.putString(LOCATION_INPUT_CODE, locationForQuery);
        }
        editor.putInt(SEEKBAR_CODE, seekBarValue);
//        if (!userQueryEditText.getText().toString().isEmpty()) {
//            userQuery = userQueryEditText.getText().toString();
//            editor.putString(USER_QUERY_CODE, userQuery);
//        }
        editor.apply();
    }

    /**
     * Grabs the values in the shared preferences and sets the items in the nav drawer with the values
     */
    @Override
    protected void onResume() {
        super.onResume();
        isFoodQueryToggle = sharedPreferences.getBoolean(FOOD_BOOLEAN_CODE, isFoodQueryToggle);
        restaurantSwitch.setChecked(isFoodQueryToggle);
        isDrinkQueryToggle = sharedPreferences.getBoolean(DRINK_BOOLEAN_CODE, isDrinkQueryToggle);
        drinkSwitch.setChecked(isDrinkQueryToggle);
        isArtsQueryToggle = sharedPreferences.getBoolean(Arts_BOOLEAN_CODE, isArtsQueryToggle);
        artsSwitch.setChecked(isArtsQueryToggle);
        isActiveQueryToggle = sharedPreferences.getBoolean(ACTIVE_BOOLEAN_CODE, isActiveQueryToggle);
        activeSwitch.setChecked(isActiveQueryToggle);
        isDeviceLocationToggle = sharedPreferences.getBoolean(DEVICE_LOCATION_BOOLEAN_CODE, isDeviceLocationToggle);
        deviceLocationSwitch.setChecked(isDeviceLocationToggle);
        if (!isDeviceLocationToggle) {
            locationEditText.setText(sharedPreferences.getString(LOCATION_INPUT_CODE, locationForQuery));
        }
        seekBarValue = sharedPreferences.getInt(SEEKBAR_CODE, 25);
        if (seekBarValue == 0) {
            radiusSeekbar.setProgress(25);
        } else {
            radiusSeekbar.setProgress(sharedPreferences.getInt(SEEKBAR_CODE, seekBarValue));
        }
        timesAPICalledCoordinates = sharedPreferences.getInt(COORDINATES_COUNTER_KEY, timesAPICalledCoordinates);
        timesAPICalledUserLocation = sharedPreferences.getInt(USERPICK_COUNTER_KEY, timesAPICalledUserLocation);
        userQuery = sharedPreferences.getString(USER_QUERY_CODE, userQuery);
//        if (userQuery != null) {
//            userQueryEditText.setText(userQuery);
//        }
    }

    /**
     * Connects the google api client
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    /**
     * disconnects the google api client
     */
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
        params.put("category_filter", query);
        params.put("sort", "2");
        params.put("limit", "20");
        params.put("offset", String.valueOf(timesAPICalledCoordinates));
        params.put("radius_filter", convertRadiusToKM());

        YelpAPIFactory apiFactory = new YelpAPIFactory(Keys.YELP_CONSUMER_KEY, Keys.YELP_CONSUMER_SECRET, Keys.YELP_TOKEN, Keys.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
//
        CoordinateOptions coordinateOptions = CoordinateOptions.builder().latitude(Double.valueOf(latitude)).longitude(Double.valueOf(longitude)).build();
        Call<SearchResponse> call = yelpAPI.search(coordinateOptions, params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                for (int i = 0; i < response.body().businesses().size(); i++) {
                    if (response.body() != null) {
                        //SearchResponse searchResponse = response.body();
                        String name = response.body().businesses().get(i).name();
                        String url = response.body().businesses().get(i).url();
                        String phone = response.body().businesses().get(i).displayPhone();
                        String snippet = response.body().businesses().get(i).snippetText();
                        String address = response.body().businesses().get(i).location().displayAddress().get(0);
                        String city = response.body().businesses().get(i).location().city();
                        String fullAddress = address + ", " + city;
                        String imageURL = response.body().businesses().get(i).imageUrl();
                        if (imageURL != null) {
                            imageURL = imageURL.replaceAll("ms", "o");
                        }
                        String category = response.body().businesses().get(i).categories().get(0).name();
                        createYelpCards(name, fullAddress, category, imageURL, url, phone, snippet);
                    }
                }
                afterSuccessfulApiCallCommands();
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                createNoMatchesDialog();
            }
        });
    }

    /**
     * Method makes API call to yelp and retrieves data based on search query and users coordinates
     */
    private void yelpAPISearchCallLocation(String query) {

        Map<String, String> params = new HashMap<>();
        params.put("category_filter", query);
        params.put("sort", "2");
        params.put("limit", "20");
        params.put("offset", String.valueOf(timesAPICalledUserLocation));
        params.put("radius_filter", convertRadiusToKM());

        YelpAPIFactory apiFactory = new YelpAPIFactory(Keys.YELP_CONSUMER_KEY, Keys.YELP_CONSUMER_SECRET, Keys.YELP_TOKEN, Keys.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        Call<SearchResponse> call = yelpAPI.search(locationForQuery, params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                for (int i = 0; i < response.body().businesses().size(); i++) {
                    if (response.body() != null) {
                        String name = response.body().businesses().get(i).name();
                        String url = response.body().businesses().get(i).url();
                        String phone = response.body().businesses().get(i).displayPhone();
                        String snippet = response.body().businesses().get(i).snippetText();
                        String address = response.body().businesses().get(i).location().displayAddress().get(0);
                        String city = response.body().businesses().get(i).location().city();
                        String fullAddress = address + ", " + city;
                        String imageURL = response.body().businesses().get(i).imageUrl();
                        if (imageURL != null) {
                            imageURL = imageURL.replaceAll("ms", "o");
                        }
                        String category = response.body().businesses().get(i).categories().get(0).name();
                        createYelpCards(name, fullAddress, category, imageURL, url, phone, snippet);
                    }
                }
                afterSuccessfulApiCallCommands();
            }
            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.INVISIBLE);
                createNoMatchesDialog();
            }
        });
    }

    /**
     * Methods done after a successful api call to yelp
     * Shuffles the list
     * Sets ui
     * checks if there are results from the call
     * notifies the adapter if all the calls have been made
     */
    private void afterSuccessfulApiCallCommands() {
        Collections.shuffle(cardsList);
        if (cardsList.size() == 0) {
            setDialog(getString(R.string.servers_unavailable), getString(R.string.unable_retrieve), R.drawable.alert);
        }
        setCardClickListener();
        setLikeButton();
        setDislikeButton();

        callCount = callCount + 1;
        if(callCount == numCalls) {
            Log.d(TAG, numCalls + " callCount:" + callCount);
            progressBar.setVisibility(View.INVISIBLE);
            cardsArrayAdapter = new CardsAdapter(MainActivity.this, cardsList);
            flingContainer.setAdapter(cardsArrayAdapter);
            cardsArrayAdapter.notifyDataSetChanged();
            if (cardsList.size() < numCalls * 20) {
                noMoreResults = true;
                Log.d(TAG, String.valueOf(cardsArrayAdapter.getCount()));
            }
            numCalls = 0;
            callCount = 0;
        }
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
        flingContainer.setAdapter(cardsArrayAdapter);
        //set the listener
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                boolean isEqual = false;
                checkForDuplicateSavedValues();
                for (int i = 0; i < duplicateList.size(); i++) {
                    if (duplicateList.get(i).equals(cardsList.get(0).getTitle())) {
                        isEqual = true;
                    }
                }
                //ensures no duplicates pushed to firebase
                if (!isEqual) {
                    Firebase firebaseRef = firebaseCards.push();
                    cardsList.get(0).setUniqueFirebaseKey(firebaseRef.getKey());
                    Date date = new Date();
                    firebaseRef.setValue(cardsList.get(0));
                    firebaseRef.setPriority(0 - date.getTime());
                }
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (!noMoreResults) {
                    if (cardsArrayAdapter.getCount() > 3) {
                        timesAPICalledUserLocation += 20;
                        timesAPICalledCoordinates += 20;
                        setStartLocationOption();
                        Log.d("onAdapterAboutToEmpty", "called");
                    }
                } else {
                    if (cardsList.size() == 0) {
                        setDialog(getString(R.string.low_matches_title), getString(R.string.low_matches_message), R.drawable.alert);
                        noMoreResults = false;
                    }
                }
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
                Intent venueIntent = new Intent(MainActivity.this, VenueActivity.class);
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

    /**
     * Creates google api client if it hasnt been created
     */
    private void setGoogleServices() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this, this, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * When the device is connected the google services and the permissions have been set,
     * the devices location is received and an api call is made
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (CheckInternetConnection.isNetworkAvailable(MainActivity.this)) {
            getLatLongCoordinates();
        }
        else {
            setDialog(getString(R.string.no_internet), getString(R.string.no_internet_message), R.drawable.baby_crying);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * connection suspended override
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * connection failed override
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(MainActivity.this, R.string.no_location_serv, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the dislike button and removes the card
     */
    private void setDislikeButton() {
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
            }
        });
    }

    /**
     * Sets the like button and removes the card and pushes the value to firebase
     */
    private void setLikeButton() {
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });
    }

    /**
     * Checks phones permissions to get location
     */
    private void checkPermissions() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int code = api.isGooglePlayServicesAvailable(MainActivity.this);
        if (code == ConnectionResult.SUCCESS) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSION_ACCESS_COARSE_LOCATION);
            }
        }
        else {
            progressBar.setVisibility(View.INVISIBLE);
            setDialog(getString(R.string.no_google_serv), getString(R.string.no_google_serv_message), R.drawable.alert);
        }
    }

    /**
     * Makes the api called based on whether the user chose the device location or a custom location
     */
    private void setStartLocationOption() {
        if (deviceLocationSwitch.isChecked()) {
            if (latitude != null && longitude != null) {
                locationForQuery = latitude + "," + longitude;
                if (CheckInternetConnection.isNetworkAvailable(MainActivity.this)) {
                    makeCoordinateAPICalls();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    setDialog(getString(R.string.no_internet), getString(R.string.no_internet_message), R.drawable.baby_crying);
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.no_location_determined, Toast.LENGTH_SHORT).show();
            }
        } else {
            locationForQuery = locationEditText.getText().toString();
            if (!locationForQuery.isEmpty()) {
                if (CheckInternetConnection.isNetworkAvailable(MainActivity.this)) {
                    makeUserLocationInputAPICalls();
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    setDialog(getString(R.string.no_internet), getString(R.string.no_internet_message), R.drawable.baby_crying);
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.enter_valid_location, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * makes api calls based on device coordinates
     */
    private void makeCoordinateAPICalls() {
        if (isFoodQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallCoordinates("restaurants");
        }
        if (isDrinkQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallCoordinates("nightlife");
        }
        if (isActiveQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallCoordinates("active");
        }
        if (isArtsQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallCoordinates("arts");
        }
        if (!userQueryEditText.getText().toString().isEmpty()) {
            numCalls = numCalls + 1;
            yelpAPISearchCallCoordinates(userQueryEditText.getText().toString().toLowerCase());
        }
    }

    /**
     * makes api calls based on user input location
     */
    private void makeUserLocationInputAPICalls() {
        if (isFoodQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallLocation("restaurants");
        }
        if (isDrinkQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallLocation("nightlife");
        }
        if (isActiveQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallLocation("active");
        }
        if (isArtsQueryToggle) {
            numCalls = numCalls + 1;
            yelpAPISearchCallLocation("arts");
        }
        if (!userQueryEditText.getText().toString().isEmpty()) {
            numCalls = numCalls + 1;
            yelpAPISearchCallLocation(userQueryEditText.getText().toString().toLowerCase());
        }
    }

    /**
     * Tells whether the user liked or disliked the card and makes the appropriate action
     * @param requestCode
     * @param resultCode
     * @param data
     */
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

    /**
     * Converts the value from the seekbar to km for the api call
     * @return
     */
    private String convertRadiusToKM() {
        int radiusValue = 0;
        if (seekBarValue > 25) {
            radiusValue = 25 * 1609;
        } else {
            radiusValue = seekBarValue * 1609;
        }
        return String.valueOf(radiusValue);
    }

    /**
     * logs user out of the app
     */
    private void setLogOut() {
        final Firebase firebase = new Firebase("https://datemate.firebaseio.com");
        final AuthData authData = firebase.getAuth();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authData != null) {
                    firebase.unauth();
                    LoginManager.getInstance().logOut();
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });
    }

    /**
     * Creates dialog box for when no matches meet the search query
     */
    private void createNoMatchesDialog() {
        new AlertDialog.Builder(MainActivity.this).setTitle(R.string.no_matches)
                .setMessage(getString(R.string.no_matches_message))
                .setPositiveButton(getString(R.string.new_search), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    }
                })
                .setIcon(R.drawable.alert)
                .show();
    }

    /**
     * Gets the saved values from firebase and checks puts them into a list to make sure there is no duplicates in the list
     */
    private void checkForDuplicateSavedValues() {
        firebaseCards.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshots: dataSnapshot.getChildren()) {
                    HashMap<String, String> fbMap = (HashMap<String, String>) snapshots.getValue();
                    duplicateList.add(fbMap.get("title"));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Sees what permissions were granted and calls the getLatLongCoordinates
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getLatLongCoordinates();
    }

    /**
     * gets the lat and long and makes api call
     */
    private void getLatLongCoordinates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            latitude = String.valueOf(lastLocation.getLatitude());
            longitude = String.valueOf(lastLocation.getLongitude());
            locationForQuery = latitude + "," + longitude;
            if (cardsList.size() == 0) {
                setStartLocationOption();
            }
        }
    }

    /**
     * Creates empty dialog for message error
     * @param title
     * @param message
     * @param image
     */
    private void setDialog(String title, String message, int image) {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(image)
                .show();
    }
}
