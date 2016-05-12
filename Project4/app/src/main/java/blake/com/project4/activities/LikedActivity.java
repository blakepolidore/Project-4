package blake.com.project4.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import blake.com.project4.GetUId;
import blake.com.project4.R;
import blake.com.project4.models.cardsModel.Cards;
import blake.com.project4.feedRecyclerviewAdapter.ClickListener;
import blake.com.project4.feedRecyclerviewAdapter.LikedFeedAdapter;
import blake.com.project4.feedRecyclerviewAdapter.RecyclerTouchListener;

/**
 * Activity that shows all the users favorited cards
 */
public class LikedActivity extends AppCompatActivity {

    private static final String TAG = "LikedActivity: ";
    public static final String BOOLEAN_INTENT = "Venue has been liked";
    public static final String FIREBASE_ID = "Firebase id";

    private LikedFeedAdapter likedFeedAdapter;
    private RecyclerView recyclerView;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private FirebaseRecyclerAdapter<Cards, LikedFeedAdapter.FeedViewHolder> adapter;
    Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        setToolbar();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
        recyclerView.setLayoutManager(layoutManager);
        setLikedCards();
        setListClickListener();
    }

    /**
     * Sets the toolbar
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.liked_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
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
                AlertDialog dialog = new AlertDialog.Builder(LikedActivity.this).setTitle(getString(R.string.information))
                        .setMessage(getString(R.string.liked_instructions))
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
     * Grabs the liked cards from firebase and places them into the recycler view
     */
    private void setLikedCards() {
        String userID = GetUId.getAuthData();
        firebase = new Firebase("https://datemate.firebaseio.com/users/" + userID + "/cards/");
        firebase.orderByPriority();
        adapter = new FirebaseRecyclerAdapter<Cards, LikedFeedAdapter.FeedViewHolder>(Cards.class, R.layout.recycler_layout, LikedFeedAdapter.FeedViewHolder.class, firebase) {
            @Override
            protected void populateViewHolder(LikedFeedAdapter.FeedViewHolder feedViewHolder, Cards cards, int i) {
                feedViewHolder.title.setText(cards.getTitle());
                feedViewHolder.location.setText(cards.getLocation());
                feedViewHolder.contact.setText(cards.getCategory());
                String imageUrl = cards.getImageUrl();
                imageUrl = imageUrl.replaceAll("/o.", "/ls.");
                ImageSize imageSize = new ImageSize(100, 100);
                imageLoader.displayImage(imageUrl, feedViewHolder.image, imageSize);
            }
        };

        if (adapter.getItemCount() > 0){
            for (int i=0; i<adapter.getItemCount(); i++){
                Log.d(TAG, "Card object from Firebase: "+adapter.getItem(i).getTitle());
            }
        } else {
            Log.d(TAG, "Adapter returned with 0 items in the list");
        }

        recyclerView.setAdapter(adapter);
    }

    /**
     * Sets the listener for if user clicks or long clicks on list item.
     * Long click deletes the item, click sends the user to the venue activity
     */
    private void setListClickListener() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent venueIntent = new Intent(LikedActivity.this, VenueActivity.class);
                venueIntent.putExtra(Main3Activity.TITLE_TEXT, adapter.getItem(position).getTitle());
                venueIntent.putExtra(Main3Activity.CATEGORY_TEXT, adapter.getItem(position).getCategory());
                venueIntent.putExtra(Main3Activity.IMAGE_TEXT, adapter.getItem(position).getImageUrl());
                venueIntent.putExtra(Main3Activity.LOCATION_TEXT, adapter.getItem(position).getLocation());
                venueIntent.putExtra(Main3Activity.DESCRIPTION_TEXT, adapter.getItem(position).getDescription());
                venueIntent.putExtra(Main3Activity.PHONE_TEXT, adapter.getItem(position).getPhone());
                venueIntent.putExtra(Main3Activity.WEBSITE_TEXT, adapter.getItem(position).getWebsite());
                venueIntent.putExtra(FIREBASE_ID, adapter.getItem(position).getUniqueFirebaseKey());
                venueIntent.putExtra(BOOLEAN_INTENT, true);
                startActivity(venueIntent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(LikedActivity.this, "Item Removed From List", Toast.LENGTH_SHORT).show();
                adapter.getRef(position).removeValue();
                adapter.notifyDataSetChanged();
            }
        }));
    }
}

