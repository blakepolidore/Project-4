package blake.com.project4.cardModelAndAdapter;

/**
 * Created by Raiders on 5/1/16.
 * Model for the cards to be displayed and swiped
 */
public class Cards {

    String title;
    String imageUrl;
    String location;
    String category;
    String website;
    String phone;
    String description;
    String uniqueFirebaseKey;

    public void setUniqueFirebaseKey(String uniqueFirebaseKey) {
        this.uniqueFirebaseKey = uniqueFirebaseKey;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public String getWebsite() {
        return website;
    }

    public String getPhone() {
        return phone;
    }

    public String getDescription() {
        return description;
    }

    public String getUniqueFirebaseKey() {
        return uniqueFirebaseKey;
    }
}
