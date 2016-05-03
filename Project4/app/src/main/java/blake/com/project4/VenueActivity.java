package blake.com.project4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity that appears when a specific card is clicked on
 */
public class VenueActivity extends AppCompatActivity {

    @BindView(R.id.venue_image)
    ImageView imageView;
    @BindView(R.id.venue_dislike)Button dislike;
    @BindView(R.id.venue_like)Button like;
    @BindView(R.id.venue_title)TextView title;
    @BindView(R.id.location)TextView location;
    @BindView(R.id.website)TextView website;
    @BindView(R.id.phone_number)TextView phone;
    @BindView(R.id.description)TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.venue_activity_toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        title = (TextView) findViewById(R.id.venue_title);
        getVenueInformation();
    }

    /**
     * Creates toolbar for the activity
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liked_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Gets information for the venue from the main activity
     */
    private void getVenueInformation() {
        Intent venueIntent = getIntent();
        String titleString = venueIntent.getStringExtra(MainActivity.TITLE_TEXT);
        title.setText(titleString);
    }
}
