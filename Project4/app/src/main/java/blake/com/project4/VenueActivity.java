package blake.com.project4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VenueActivity extends AppCompatActivity {

    @BindView(R.id.venue_image) ImageView imageView;
    @BindView(R.id.venue_dislike)Button dislike;
    @BindView(R.id.venue_like)Button like;
    @BindView(R.id.venue_title)TextView title;
    @BindView(R.id.location)TextView location;
    @BindView(R.id.website)TextView website;
    @BindView(R.id.phone_number)TextView phone;
    @BindView(R.id.description)TextView description;
    @BindView(R.id.venue_activity_toolbar)Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.liked_menu, menu);
        return true;
    }
}
