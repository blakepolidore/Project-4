package blake.com.project4.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import blake.com.project4.Main3Activity;
import blake.com.project4.R;

/**
 * Created by Raiders on 5/8/16.
 */
public class VenueFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_venue, container, false);
        //setSupportActionBar(toolbar);
        setViews(view);
        getVenueInformation();
        setShareClickListener();
        setLikeButton();
        setDisikeButton();
        setButtonsClickable();
        return view;
    }

    private void setViews(View view) {
        //Toolbar toolbar = (Toolbar) view.findViewById(R.id.venue_activity_toolbar);
        imageView = (ImageView) view.findViewById(R.id.venue_image);
        dislike = (ImageButton) view.findViewById(R.id.venue_dislike);
        like = (ImageButton) view.findViewById(R.id.venue_like);
        title = (TextView) view.findViewById(R.id.venue_title);
        location = (TextView) view.findViewById(R.id.location);
        website = (TextView) view.findViewById(R.id.website);
        website.setClickable(true);
        website.setMovementMethod(LinkMovementMethod.getInstance());
        phone = (TextView) view.findViewById(R.id.phone_number);
        phone.setClickable(true);
        phone.setPaintFlags(phone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        description = (TextView) view.findViewById(R.id.description);
        category = (TextView) view.findViewById(R.id.category_venue);
        share = (ImageButton) view.findViewById(R.id.share_button);
    }

    /**
     * Gets information for the venue from the main activity
     */
    private void getVenueInformation() {
//        Intent venueIntent = getIntent();
        String titleString = getArguments().getString(Main3Activity.TITLE_TEXT);
        title.setText(titleString);
        String imageURL = getArguments().getString(Main3Activity.IMAGE_TEXT);
        //Picasso.with(getApplicationContext()).load(imageURL).placeholder(R.drawable.smithriver).into(imageView);
        imageLoader.init(ImageLoaderConfiguration.createDefault(getContext()));
        ImageSize imageSize = new ImageSize(300, 300);
        imageLoader.displayImage(imageURL, imageView);
        String categoryString = getArguments().getString(Main3Activity.CATEGORY_TEXT);
        category.setText("Category: " + categoryString);
        String locationString = getArguments().getString(Main3Activity.LOCATION_TEXT);
        location.setText("Location: " + locationString);
        websiteString = getArguments().getString(Main3Activity.WEBSITE_TEXT);
        String websiteLink = "<a href ='" + websiteString + "'> Reviews</a>";
        website.setText(Html.fromHtml(websiteLink));
        String phoneString = getArguments().getString(Main3Activity.PHONE_TEXT);
        if (phoneString != null) {
            phoneString = phoneString.substring(3);
            phoneString =phoneString.replace("-", "");
        }
        phone.setText(phoneString);
        setPhoneCall(phoneString);
        String descriptionString = getArguments().getString(Main3Activity.DESCRIPTION_TEXT);
        description.setText("Description: " + descriptionString);
//        hasBeenLiked = getArguments().getBoolean(LikedActivity.BOOLEAN_INTENT, false);
//        firebaseKey = getArguments().getString(LikedActivity.FIREBASE_ID);
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

    private void setPhoneCall(final String phoneNumber) {
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+phoneNumber);
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });
    }

    private void setLikeButton() {
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(VenueActivity.this, Main3Activity.class);
//                intent.putExtra(IF_LIKE_INTENT, true);
//                setResult(RESULT_INTENT, intent);
//                finish();

            }
        });
    }

    private void setDisikeButton() {
//        dislike.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (hasBeenLiked) {
//                    Toast.makeText(VenueActivity.this, "Item Removed From Liked List", Toast.LENGTH_SHORT).show();
//                    Firebase firebaseRef = new Firebase("https://datemate.firebaseio.com");
//                    AuthData authData = firebaseRef.getAuth();
//                    String uID = authData.getUid();
//                    Firebase firebase = new Firebase("https://datemate.firebaseio.com/users/" + uID + "/cards/" + firebaseKey + "/");
//                    firebase.removeValue();
//                    Intent backToLikeIntent = new Intent(VenueActivity.this, LikedActivity.class);
//                    startActivity(backToLikeIntent);
//                } else {
//                    Intent intent = new Intent(VenueActivity.this, Main3Activity.class);
//                    intent.putExtra(IF_LIKE_INTENT, false);
//                    setResult(RESULT_INTENT, intent);
//                    finish();
//                }
//            }
//        });
    }

    private void setButtonsClickable() {
//        if (hasBeenLiked) {
//            like.setClickable(false);
//            like.setAlpha(0.0f);
//        }
    }
}
