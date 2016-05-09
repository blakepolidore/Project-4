package blake.com.project4;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by Raiders on 5/9/16.
 * Gets firebase unique user id
 */
public class GetUId {
    public static String getAuthData() {
        Firebase firebase = new Firebase("https://datemate.firebaseio.com");
        AuthData authData = firebase.getAuth();
        String uID = authData.getUid();
        return uID;
    }
}
