package blake.com.project4.cardModelAndAdapter;

/**
 * Created by Raiders on 5/1/16.
 */
public class Cards {

    String title;
    String imageUrl;
    String location;
    String category;

    public void setCategory(String category) {
        this.category = category;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {

        return category;
    }
}
