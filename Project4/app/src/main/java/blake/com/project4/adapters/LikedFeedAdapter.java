package blake.com.project4.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

import blake.com.project4.R;
import blake.com.project4.models.cardsModel.Cards;

/**
 * Created by Raiders on 5/2/16.
 * Recycler adapter for the feed
 */
public class LikedFeedAdapter extends RecyclerView.Adapter<LikedFeedAdapter.FeedViewHolder>{

    private List<Cards> cardsList;

    /**
     * View holder class
     */
    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        public TextView title, location, contact;
        public ImageView image;

        /**
         * View holder constructor
         * @param view
         */
        public FeedViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.recycler_title);
            location = (TextView) view.findViewById(R.id.recycler_location);
            contact = (TextView) view.findViewById(R.id.recycler_contact);
            image = (ImageView) view.findViewById(R.id.recycler_image);
        }
    }

    /**
     * Constructor for the adapter
     * @param cardsList
     */
    public LikedFeedAdapter(List<Cards> cardsList) {
        this.cardsList = cardsList;
    }

    /**
     * Tells the adapter which layout to inflate
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_layout, parent, false);

        return new FeedViewHolder(itemView);
    }

    /**
     * Puts the pertinent data into the views
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(FeedViewHolder holder, int position) {
        Cards cards = cardsList.get(position);
        holder.title.setText(cards.getTitle());
        holder.location.setText(cards.getLocation());
        //holder.contact.setText(cards.getContact());
        ImageLoader imageLoader = ImageLoader.getInstance();
        Context context = holder.image.getContext();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        Bitmap bitmap = imageLoader.loadImageSync(cards.getImageUrl());
        holder.image.setImageBitmap(bitmap);
    }

    /**
     * Gets count of items in the list
     * @return
     */
    @Override
    public int getItemCount() {
        return cardsList.size();
    }
}
