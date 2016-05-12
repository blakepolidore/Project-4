package blake.com.project4.models.fourSquareModels;

/**
 * Created by Raiders on 5/1/16.
 */
public class LocationItems {

    String address;
    String crossStreet;
    String postalCode;
    String cc;
    String city;
    String state;
    String country;
    String[] formattedAddress;

    public String getAddress() {
        return address;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCc() {
        return cc;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String[] getFormattedAddress() {
        return formattedAddress;
    }
}
