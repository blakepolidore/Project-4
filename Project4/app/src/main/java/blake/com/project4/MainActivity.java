package blake.com.project4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.SearchResponse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import blake.com.project4.apicalls.FoursquareAPIService;
import blake.com.project4.cardModelAndAdapter.Cards;
import blake.com.project4.cardModelAndAdapter.CardsAdapter;
import blake.com.project4.swipefling.SwipeFlingAdapterView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.swipableImage)
    ImageView imageView;
    @BindView(R.id.card_title)
    TextView title;
//    ImageView imageView;
//    TextView title;


    public static final String TITLE_TEXT = "TITLE TEXT";

    LinkedList<String> al;
    LinkedList<Cards> cardsList;
    ArrayAdapter<String> arrayAdapter;
    ArrayAdapter<Cards> cardsArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
//        imageView = (ImageView) findViewById(R.id.swipableImage);
//        title = (TextView) findViewById(R.id.card_title);
//        toolbar.setLogo(R.drawable.nyt_logo);
//        toolbar.setLogoDescription(getResources().getString(R.string.logo_desc));

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        cardsList = new LinkedList<>();
        Cards cards = new Cards();
        cards.setImageUrl("https://pbs.twimg.com/profile_images/672132153900183553/zVFIAIDi.jpg");
        cards.setLocation("New York");
        cards.setTitle("Park");
        cardsList.add(cards);

        al = new LinkedList<>();
        al.add("Taco");
        al.add("Burrito");
        al.add("Pizza");
        al.add("Steak");
        yelpAPISearchCall();
        foursquareAPICall();

        //arrayAdapter = new ArrayAdapter<String>(this, R.layout.item, R.id.card_title, al);
        cardsArrayAdapter = new CardsAdapter(this, cardsList);

        //set the listener and the adapter
        flingContainer.setAdapter(cardsArrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                cardsList.remove(0);
                cardsArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(MainActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(MainActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
//                // Ask for more data here
//                al.add("XML ".concat(String.valueOf(i)));
//                arrayAdapter.notifyDataSetChanged();
//                Log.d("LIST", "notified");
//                i++;
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent venueIntent = new Intent(MainActivity.this, VenueActivity.class);
                venueIntent.putExtra(TITLE_TEXT, cardsList.get(0).getTitle());
                startActivity(venueIntent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.liked_activities:
                Intent likedVenuesIntent = new Intent(MainActivity.this, LikedActivity.class);
                startActivity(likedVenuesIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void yelpAPISearchCall() {

        Map<String, String> params = new HashMap<>();
        params.put("term", "food");
        params.put("sort", "2");
        params.put("radius_filter", "10000");

        YelpAPIFactory apiFactory = new YelpAPIFactory(Keys.YELP_CONSUMER_KEY, Keys.YELP_CONSUMER_SECRET, Keys.YELP_TOKEN, Keys.YELP_TOKEN_SECRET);
        YelpAPI yelpAPI = apiFactory.createAPI();
        Call<SearchResponse> call = yelpAPI.search("San Francisco", params);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                for (int i = 0; i < 20; i++) {
                    String name = response.body().businesses().get(i).name();
                    String url = response.body().businesses().get(i).url();
                    String phone = response.body().businesses().get(i).displayPhone();
                    String address = response.body().businesses().get(i).location().displayAddress().get(0);
                    String imageURL = response.body().businesses().get(i).imageUrl();
                    String category = response.body().businesses().get(i).categories().get(0).name();
                    Cards cards = new Cards();
                    cards.setTitle(name);
                    cards.setLocation(address);
                    cards.setImageUrl(imageURL);
                    cards.setCategory(category);
                    cardsList.add(i,cards);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {

            }
        });


    }

    private void foursquareAPICall() {
                Retrofit retrofitFourSquare = new Retrofit.Builder()
                .baseUrl("https://api.foursquare.com/v2/venues/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FoursquareAPIService foursquareAPIService = retrofitFourSquare.create(FoursquareAPIService.class);

//        Calendar currentDate = Calendar.getInstance(); //Get the current date
//        SimpleDateFormat formatter= new SimpleDateFormat("yyyyMMdd"); //format it as per your requirement
//        String dateNow = formatter.format(currentDate.getTime());

        Call<blake.com.project4.foursquareModel.Response> call =
                foursquareAPIService.search("San Francisco", Keys.FOURSQUARE_ID, Keys.FOURSQUARE_SECRET, "20160501", "foursquare");
        call.enqueue(new Callback<blake.com.project4.foursquareModel.Response>() {
            @Override
            public void onResponse(Call<blake.com.project4.foursquareModel.Response> call, Response<blake.com.project4.foursquareModel.Response> response) {
                int error = response.code();
                for (int i = 0; i < response.body().getResponse().length; i++) {
                    String name = response.body().getResponse()[i].getName();
                    al.add(name);
                }
                String code = Integer.toString(error);

                Log.d("MAIN ACTIVITY", code);
            }

            @Override
            public void onFailure(Call<blake.com.project4.foursquareModel.Response> call, Throwable t) {
                Log.d("MAIN ACTIVITY", "Test Failed");
            }
        });
    }
}

