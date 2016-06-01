package blake.com.project4.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.uber.sdk.android.rides.UberSdk;

import blake.com.project4.Keys;

/**
 * Created by Raiders on 5/3/16.
 * Instantiates firebase and facebook sdk and uber sdk
 */
public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
        UberSdk.initialize(this, Keys.UBER_CLIENT_ID);
        UberSdk.setRedirectUri("project4://oauth/callback");
    }
}
