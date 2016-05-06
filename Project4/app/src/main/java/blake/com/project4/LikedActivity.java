package blake.com.project4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.List;

import blake.com.project4.cardModelAndAdapter.Cards;
import blake.com.project4.feedRecyclerviewAdapter.LikedFeedAdapter;

/**
 * Activity that shows all the users favorited cards
 */
public class LikedActivity extends AppCompatActivity {

    private static final String TAG = "LikedActivity: ";

    private List<Cards> cardsList;
    private LikedFeedAdapter likedFeedAdapter;
    private RecyclerView recyclerView;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        Toolbar toolbar = (Toolbar) findViewById(R.id.liked_activity_toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        setLikedCards();
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

    private void setLikedCards() {
        String userID = getAuthData();
        Firebase firebase = new Firebase("https://datemate.firebaseio.com/users/" + userID + "/cards/");
        FirebaseRecyclerAdapter<Cards, LikedFeedAdapter.FeedViewHolder> adapter = new FirebaseRecyclerAdapter<Cards, LikedFeedAdapter.FeedViewHolder>(Cards.class, R.layout.recycler_layout, LikedFeedAdapter.FeedViewHolder.class, firebase) {
            @Override
            protected void populateViewHolder(LikedFeedAdapter.FeedViewHolder feedViewHolder, Cards cards, int i) {
                feedViewHolder.title.setText(cards.getTitle());
                feedViewHolder.location.setText(cards.getLocation());
                feedViewHolder.contact.setText(cards.getCategory());
                String imageUrl = cards.getImageUrl();
                imageUrl = imageUrl.replaceAll("/o.", "/ms.");
                imageLoader.init(ImageLoaderConfiguration.createDefault(getApplicationContext()));
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

    private String getAuthData() {
        Firebase firebase = new Firebase("https://datemate.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }
}

