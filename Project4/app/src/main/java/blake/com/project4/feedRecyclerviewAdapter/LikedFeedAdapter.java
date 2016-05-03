package blake.com.project4.feedRecyclerviewAdapter;

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
import blake.com.project4.cardModelAndAdapter.Cards;

/**
 * Created by Raiders on 5/2/16.
 */
public class LikedFeedAdapter extends RecyclerView.Adapter<LikedFeedAdapter.FeedViewHolder>{

    private List<Cards> cardsList;

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        TextView title, location, contact;
        ImageView image;

        public FeedViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.recycler_title);
            location = (TextView) view.findViewById(R.id.recycler_location);
            contact = (TextView) view.findViewById(R.id.recycler_contact);
            image = (ImageView) view.findViewById(R.id.recycler_image);
        }
    }

    public LikedFeedAdapter(List<Cards> cardsList) {
        this.cardsList = cardsList;
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_layout, parent, false);

        return new FeedViewHolder(itemView);
    }

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

    @Override
    public int getItemCount() {
        return cardsList.size();
    }
}
