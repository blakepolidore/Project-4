package blake.com.project4.cardModelAndAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.LinkedList;
import java.util.List;

import blake.com.project4.R;

/**
 * Created by Raiders on 5/1/16.
 * Custom adapter for the cards
 */
public class CardsAdapter extends ArrayAdapter<Cards> {

    List<Cards> cardsList;
    ImageLoader imageLoader = ImageLoader.getInstance();

    /**
     * Constructor
     * @param context
     * @param list
     */
    public CardsAdapter(Context context, LinkedList list) {
        super(context, -1, list);
        this.cardsList = list;
    }

    /**
     * Sets the views on the cards with approriate data
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cardsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        TextView titleText = (TextView) cardsView.findViewById(R.id.card_title);
        TextView locationText = (TextView) cardsView.findViewById(R.id.card_location);
        ImageView image = (ImageView) cardsView.findViewById(R.id.swipableImage);
        Cards cards = cardsList.get(position);
        titleText.setText(cards.title);
        locationText.setText(cards.location);
        //TODO add a placeholder image (.placeholder)
        //Picasso.with(parent.getContext()).load(cards.imageUrl).resize(350, 350).into(image);
        imageLoader.init(ImageLoaderConfiguration.createDefault(parent.getContext()));
        ImageSize imageSize = new ImageSize(300,300);
        imageLoader.displayImage(cards.imageUrl, image);
        return cardsView;
    }
}
