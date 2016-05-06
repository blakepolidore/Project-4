package blake.com.project4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Activity that appears when a specific card is clicked on
 */
public class VenueActivity extends AppCompatActivity {

    ImageView imageView;
    ImageButton dislike;
    ImageButton like;
    ImageButton share;
    TextView title;
    TextView location;
    TextView website;
    TextView phone;
    TextView description;
    TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.venue_activity_toolbar);
        setSupportActionBar(toolbar);
        setViews();

        getVenueInformation();
        setShareClickListener();
    }

    private void setViews() {
        imageView = (ImageView) findViewById(R.id.venue_image);
        dislike = (ImageButton) findViewById(R.id.venue_dislike);
        like = (ImageButton) findViewById(R.id.venue_like);
        title = (TextView) findViewById(R.id.venue_title);
        location = (TextView) findViewById(R.id.location);
        website = (TextView) findViewById(R.id.website);
        phone = (TextView) findViewById(R.id.phone_number);
        description = (TextView) findViewById(R.id.description);
        category = (TextView) findViewById(R.id.category_venue);
        share = (ImageButton) findViewById(R.id.share_button);
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
        String titleString = venueIntent.getStringExtra(Main3Activity.TITLE_TEXT);
        title.setText(titleString);
        String imageURL = venueIntent.getStringExtra(Main3Activity.IMAGE_TEXT);
        Picasso.with(getApplicationContext()).load(imageURL).placeholder(R.drawable.smithriver).into(imageView);
        String categoryString = venueIntent.getStringExtra(Main3Activity.CATEGORY_TEXT);
        category.setText(categoryString);
        String locationString = venueIntent.getStringExtra(Main3Activity.LOCATION_TEXT);
        location.setText(locationString);
        String websiteString = venueIntent.getStringExtra(Main3Activity.WEBSITE_TEXT);
        website.setText(websiteString);
        String phoneString = venueIntent.getStringExtra(Main3Activity.PHONE_TEXT);
        phone.setText(phoneString);
        String descriptionString = venueIntent.getStringExtra(Main3Activity.DESCRIPTION_TEXT);
        description.setText(descriptionString);
    }

    /**
     * Sets up the functionality to allow user to share venue across different communication apps
     */
    private void setShareClickListener() {
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "");//TODO add shareable url
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this place!");
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
    }
}
