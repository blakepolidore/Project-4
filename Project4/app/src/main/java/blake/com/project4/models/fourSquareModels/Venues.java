package blake.com.project4.models.fourSquareModels;

import blake.com.project4.models.fourSquareModels.CategoryItems;
import blake.com.project4.models.fourSquareModels.ContactItems;
import blake.com.project4.models.fourSquareModels.LocationItems;

/**
 * Created by Raiders on 5/1/16.
 */
public class Venues {

    String name;
    ContactItems contact;
    LocationItems location;
    CategoryItems[] categories;
    String url;
    String id;

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

    public String getId() {
        return id;
    }
}
