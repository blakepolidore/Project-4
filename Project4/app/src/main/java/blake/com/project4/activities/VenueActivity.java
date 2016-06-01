package blake.com.project4.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.uber.sdk.android.rides.RideParameters;
import com.uber.sdk.android.rides.RideRequestButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import blake.com.project4.R;

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
    private RideRequestButton uberButton;

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

        setToolbar();
        setViews();
        getVenueInformation();
        setShareClickListener();
        setLikeButton();
        setDisikeButton();
        setButtonsClickable();
        setUberRide();
    }

    /**
     * Sets the toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.venue_activity_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
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
        uberButton = (RideRequestButton) findViewById(R.id.uber_button);
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
     * Creates intents when options are clicked on
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.information:
                AlertDialog dialog = new AlertDialog.Builder(VenueActivity.this).setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.instructions_venue))
                        .setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gets information for the venue from the main activity
     */
    private void getVenueInformation() {
        Intent venueIntent = getIntent();
        getIntentStringsSetTextViews(venueIntent, MainActivity.TITLE_TEXT, title);
        String imageURL = venueIntent.getStringExtra(MainActivity.IMAGE_TEXT);
        Picasso.with(getApplicationContext()).load(imageURL).resize(900, 600).placeholder(R.drawable.arrows).into(imageView);
//        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
//        ImageSize imageSize = new ImageSize(300, 300);
//        imageLoader.displayImage(imageURL, imageView);
        getIntentStringsSetTextViews(venueIntent, MainActivity.CATEGORY_TEXT, category);
        getIntentStringsSetTextViews(venueIntent, MainActivity.LOCATION_TEXT, location);
        websiteString = venueIntent.getStringExtra(MainActivity.WEBSITE_TEXT);
        String websiteLink = "<a href ='" + websiteString + getString(R.string.reviews);
        website.setText(Html.fromHtml(websiteLink));
        String phoneString = venueIntent.getStringExtra(MainActivity.PHONE_TEXT);
        if (phoneString != null) {
            phoneString = phoneString.substring(3);
            phoneString =phoneString.replace("-", "");
        }
        phone.setText(phoneString);
        if (!isTablet(VenueActivity.this)) {
            setPhoneCall(phoneString);
        }
        getIntentStringsSetTextViews(venueIntent, MainActivity.DESCRIPTION_TEXT, description);
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
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.check_this_out));
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
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
                Intent intent = new Intent(VenueActivity.this, MainActivity.class);
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
                Intent intent = new Intent(VenueActivity.this, MainActivity.class);
                intent.putExtra(IF_LIKE_INTENT, false);
                setResult(RESULT_INTENT, intent);
                finish();
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
            dislike.setClickable(false);
            dislike.setAlpha(0.0f);
        }
    }

    private boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void getIntentStringsSetTextViews(Intent intent, String code, TextView textView) {
        String stringValue = intent.getStringExtra(code);
        textView.setText(stringValue);
    }

    private void setUberRide() {
        if (MainActivity.lastLocation != null) {
            RideParameters rideParameters = new RideParameters.Builder()
                    .setPickupLocation(MainActivity.lastLocation.getLatitude(), MainActivity.lastLocation.getLongitude(), "Wherever You Are", "In This World")
                    .setDropoffLocation(getLatLong()[0], getLatLong()[1], title.getText().toString(), location.getText().toString())
                    .build();
            uberButton.setRideParameters(rideParameters);
        }
    }

    private Double[] getLatLong() {
        Double[] latlong = new Double[2];
        Address address = new Address(Locale.US);
        Geocoder geocoder = new Geocoder(VenueActivity.this, Locale.US);
        List<Address> listOfAddress;

        try {
            if (!location.getText().toString().isEmpty()) {
                listOfAddress = geocoder.getFromLocationName(location.getText().toString(), 1);
                if (listOfAddress.size() > 0) {
                    address = listOfAddress.get(0);
                    latlong[0] = address.getLatitude();
                    latlong[1] = address.getLongitude();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return latlong;
    }
}
