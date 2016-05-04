package blake.com.project4.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;

/**
 * Created by Raiders on 5/3/16.
 */
public class Project4 extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
    }
}
