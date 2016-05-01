package blake.com.project4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

public class VenueActivity extends AppCompatActivity {

//    @BindView(R.id.venue_image) ImageView imageView;
//    @BindView(R.id.venue_dislike)Button dislike;
//    @BindView(R.id.venue_like)Button like;
//    @BindView(R.id.venue_title)TextView title;
//    @BindView(R.id.location)TextView location;
//    @BindView(R.id.website)TextView website;
//    @BindView(R.id.phone_number)TextView phone;
//    @BindView(R.id.description)TextView description;
//    @BindView(R.id.venue_activity_toolbar)Toolbar toolbar;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        //ButterKnife.bind(this);
        title = (TextView) findViewById(R.id.venue_title);
        getVenueInformation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.liked_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void getVenueInformation() {
        Intent venueIntent = getIntent();
        String titleString = venueIntent.getStringExtra(MainActivity.TITLE_TEXT);
        title.setText(titleString);
    }
}
