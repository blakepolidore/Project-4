package blake.com.project4.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

/**
 * Created by Raiders on 5/3/16.
 * Instantiates firebase and facebook sdk
 */
public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
    }
}
