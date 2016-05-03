package blake.com.project4;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import java.util.List;

import blake.com.project4.cardModelAndAdapter.Cards;
import blake.com.project4.feedRecyclerviewAdapter.LikedFeedAdapter;

/**
 * Activity that shows all the users favorited cards
 */
public class LikedActivity extends AppCompatActivity {

    private List<Cards> cardsList;
    private LikedFeedAdapter likedFeedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked);

        Toolbar toolbar = (Toolbar) findViewById(R.id.liked_activity_toolbar);
        setSupportActionBar(toolbar);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        likedFeedAdapter = new LikedFeedAdapter(cardsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(likedFeedAdapter);

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
}
