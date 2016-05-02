package blake.com.project4.cardModelAndAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import blake.com.project4.R;

/**
 * Created by Raiders on 5/1/16.
 */
public class CardsAdapter extends ArrayAdapter<Cards> {

    List<Cards> cardsList;

    public CardsAdapter(Context context, LinkedList list) {
        super(context, -1, list);
        this.cardsList = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cardsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        TextView titleText = (TextView) cardsView.findViewById(R.id.card_title);
        TextView locationText = (TextView) cardsView.findViewById(R.id.card_location);
        ImageView image = (ImageView) cardsView.findViewById(R.id.swipableImage);
        Cards cards = cardsList.get(position);
        titleText.setText(cards.title);
        locationText.setText(cards.location);
        Picasso.with(parent.getContext()).load(cards.imageUrl).into(image);
        return cardsView;
    }
}
