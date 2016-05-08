package blake.com.project4;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

/**
 * Activity that appears when a specific card is clicked on
 */
public class VenueActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton dislike;
    private ImageButton like;
    private ImageButton share;
    private TextView title;
    private TextView location;
    private TextView website;
    private TextView phone;
    private TextView description;
    private TextView category;

    private String websiteString;
    private String firebaseKey;
    public final static int RESULT_INTENT = 94;
    public final static String IF_LIKE_INTENT = "LIKED INTENT";

    private boolean hasBeenLiked = false;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue);

        Toolbar toolbar = (Toolbar) findViewById(R.id.venue_activity_toolbar);
        setSupportActionBar(toolbar);
        setViews();
        getVenueInformation();
        setShareClickListener();
        setLikeButton();
        setDisikeButton();
        setButtonsClickable();
    }

    /**
     * Sets the views in the activity
     */
    private void setViews() {
        imageView = (ImageView) findViewById(R.id.venue_image);
        dislike = (ImageButton) findViewById(R.id.venue_dislike);
        like = (ImageButton) findViewById(R.id.venue_like);
        title = (TextView) findViewById(R.id.venue_title);
        location = (TextView) findViewById(R.id.location);
        website = (TextView) findViewById(R.id.website);
        website.setClickable(true);
        website.setMovementMethod(LinkMovementMethod.getInstance());
        phone = (TextView) findViewById(R.id.phone_number);
        phone.setClickable(true);
        phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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
        //Picasso.with(getApplicationContext()).load(imageURL).placeholder(R.drawable.smithriver).into(imageView);
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        ImageSize imageSize = new ImageSize(300, 300);
        imageLoader.displayImage(imageURL, imageView);
        String categoryString = venueIntent.getStringExtra(Main3Activity.CATEGORY_TEXT);
        category.setText("Category: " + categoryString);
        String locationString = venueIntent.getStringExtra(Main3Activity.LOCATION_TEXT);
        location.setText("Location: " + locationString);
        websiteString = venueIntent.getStringExtra(Main3Activity.WEBSITE_TEXT);
        String websiteLink = "<a href ='" + websiteString + "'> Reviews</a>";
        website.setText(Html.fromHtml(websiteLink));
        String phoneString = venueIntent.getStringExtra(Main3Activity.PHONE_TEXT);
        if (phoneString != null) {
            phoneString = phoneString.substring(3);
            phoneString =phoneString.replace("-", "");
        }
        phone.setText(phoneString);
        setPhoneCall(phoneString);
        String descriptionString = venueIntent.getStringExtra(Main3Activity.DESCRIPTION_TEXT);
        description.setText("Description: " + descriptionString);
        hasBeenLiked = venueIntent.getBooleanExtra(LikedActivity.BOOLEAN_INTENT, false);
        firebaseKey = venueIntent.getStringExtra(LikedActivity.FIREBASE_ID);
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
                intent.putExtra(Intent.EXTRA_TEXT, websiteString);
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this place!");
                startActivity(Intent.createChooser(intent, "Share"));
            }
        });
    }

    /**
     * Allows user to dial number
     * @param phoneNumber
     */
    private void setPhoneCall(final String phoneNumber) {
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+phoneNumber);//TODO check if this breaks app on non phone device
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
    }

    /**
     * Sets the like button for the card
     */
    private void setLikeButton() {
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VenueActivity.this, Main3Activity.class);
                intent.putExtra(IF_LIKE_INTENT, true);
                setResult(RESULT_INTENT, intent);
                finish();
            }
        });
    }

    /**
     * Sets the dislike button for the card
     */
    private void setDisikeButton() {
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasBeenLiked) {
                    Toast.makeText(VenueActivity.this, "Item Removed From Liked List", Toast.LENGTH_SHORT).show();
                    Firebase firebaseRef = new Firebase("https://datemate.firebaseio.com");
                    AuthData authData = firebaseRef.getAuth();
                    String uID = authData.getUid();
                    Firebase firebase = new Firebase("https://datemate.firebaseio.com/users/" + uID + "/cards/" + firebaseKey + "/");
                    firebase.removeValue();
                    Intent backToLikeIntent = new Intent(VenueActivity.this, LikedActivity.class);
                    startActivity(backToLikeIntent);
                } else {
                    Intent intent = new Intent(VenueActivity.this, Main3Activity.class);
                    intent.putExtra(IF_LIKE_INTENT, false);
                    setResult(RESULT_INTENT, intent);
                    finish();
                }
            }
        });
    }

    /**
     * If the card has already been liked, it makes the like button non clickable
     */
    private void setButtonsClickable() {
        if (hasBeenLiked) {
            like.setClickable(false);
            like.setAlpha(0.0f);
        }
    }
}
