package blake.com.project4.foursquareModel;

/**
 * Created by Raiders on 5/1/16.
 */
public class Venues {

    String name;
    ContactItems contact;
    LocationItems location;
    CategoryItems[] categories;
    String url;

    public String getName() {
        return name;
    }

    public ContactItems getContact() {
        return contact;
    }

    public LocationItems getLocation() {
        return location;
    }

    public CategoryItems[] getCategories() {
        return categories;
    }

    public String getUrl() {
        return url;
    }
}
