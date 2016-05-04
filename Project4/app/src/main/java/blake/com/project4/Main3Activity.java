package blake.com.project4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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

    private SharedPreferences sharedPreferences;

    private boolean deviceLocationToggle = false;
    private boolean foodQueryToggle = false;
    private boolean drinkQueryToggle = false;
    private boolean locationQueryToggle = false;
    private boolean eventsQueryToggle = false;

    private final String FOOD_BOOLEAN_CODE = "food";
    private final String DRINK_BOOLEAN_CODE = "drink";
    private final String LOCATION_BOOLEAN_CODE = "location";
    private final String EVENTS_BOOLEAN_CODE = "events";
    private final String DEVICE_LOCATION_BOOLEAN_CODE = "device";

    private String locationInput;
    private final String LOCATION_INPUT_CODE = "user input";

    private int seekBarValue;
    private final String SEEKBAR_CODE = "seekbar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        setViews();
        radiusSeekbar.setProgress(25);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        locationSelection();
        setSeekBar();
        checkVenueSwitches(foodSwitch);
        checkVenueSwitches(drinkSwitch);
        checkVenueSwitches(locationsSwitch);
        checkVenueSwitches(eventsSwitch);

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
        seekbarProgress = (TextView) scrollView.findViewById(R.id.seekbar_progress);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    private void setSeekBar() {
        radiusSeekbar.setMax(100);
        radiusSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekbarProgress.setText(String.valueOf(progress));
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

    private void locationSelection() {
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
        locationInput = locationEditText.getText().toString();
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
        if (deviceLocationToggle) {
            locationEditText.setText(sharedPreferences.getString(LOCATION_INPUT_CODE, locationInput));
        }
        radiusSeekbar.setProgress(sharedPreferences.getInt(SEEKBAR_CODE, seekBarValue));
    }
}
