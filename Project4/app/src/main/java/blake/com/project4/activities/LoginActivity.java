package blake.com.project4.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import blake.com.project4.R;

public class LoginActivity extends AppCompatActivity {

    private Firebase firebaseRef;
    private Firebase.AuthStateListener authStateListener;
    private AuthData authData;

    private LoginButton facebookLoginButton;
    private CallbackManager facebookCallbackManager;
    private AccessTokenTracker facebookAccessTokenTracker;

    private ProgressDialog authProgressDialog;

    private static final String TAG ="LoginActivity: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setFacebook();
        firebaseRef = new Firebase("https://datemate.firebaseio.com");
        setAuthProgressDialog();
    }

    /**
     * Sets facebook features
     */
    private void setFacebook() {
        facebookCallbackManager = CallbackManager.Factory.create();
        facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login);
        facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.i(TAG, "Facebook.AccessTokenTracker.OnCurrentAccessTokenChanged");
                LoginActivity.this.onFacebookAccessTokenChange(currentAccessToken);
            }
        };
    }

    /**
     * Dialog if successfull login with firebase
     */
    private void setAuthProgressDialog() {
        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setTitle("Loading");
        authProgressDialog.setMessage("Authenticating with Firebase...");
        authProgressDialog.setCancelable(false);
        authProgressDialog.show();

        authStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                authProgressDialog.hide();
                setAuthenticatedUser(authData);
            }
        };
        /* Check if the user is authenticated with Firebase already. If this is the case we can set the authenticated
         * user and hide hide any login buttons */
        firebaseRef.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (facebookAccessTokenTracker != null) {
            facebookAccessTokenTracker.stopTracking();
        }
        // if changing configurations, stop tracking firebase session.
        firebaseRef.removeAuthStateListener(authStateListener);
    }

    /**
     * This method fires when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Unauthenticate from Firebase and from providers where necessary.
     */
    private void logout() {
        if (this.authData != null) {
            /* logout of Firebase */
            firebaseRef.unauth();
            LoginManager.getInstance().logOut();
            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
        }
    }

    /**
     * Once a user is logged in, take the mAuthData provided from Firebase and "use" it.
     */
    private void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            facebookLoginButton.setVisibility(View.GONE);
            goToMainActivity();
            /* show a provider specific status text */
            String name = (String) authData.getProviderData().get("displayName");
        } else {
            /* No authenticated user show all the login buttons */
            facebookLoginButton.setVisibility(View.VISIBLE);
        }
        this.authData = authData;
        /* invalidate options menu to hide/show the logout button */
        supportInvalidateOptionsMenu();
    }

    /**
     * Error message if firebase doesn't work
     * @param message
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Utility class for authentication results
     */
    private class AuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public AuthResultHandler(String provider) {
            this.provider = provider;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            authProgressDialog.hide();
            setAuthenticatedUser(authData);
            Map<String, String> map = new HashMap<String, String>();
            map.put("provider", authData.getProvider());
            if(authData.getProviderData().containsKey("displayName")) {
                map.put("displayName", authData.getProviderData().get("displayName").toString());
            }
            //firebaseRef.child("users").child(authData.getUid()).setValue(map);//Create if not exists
            goToMainActivity();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            authProgressDialog.hide();
            showErrorDialog(firebaseError.toString());
        }
    }

    /**
     * Tokens changes
     * @param token
     */
    private void onFacebookAccessTokenChange(AccessToken token) {
        if (token != null) {
            authProgressDialog.show();
            firebaseRef.authWithOAuthToken("facebook", token.getToken(), new AuthResultHandler("facebook"));
        } else {
            // Logged out of Facebook and currently authenticated with Firebase using Facebook, so do a logout
            if (this.authData != null && this.authData.getProvider().equals("facebook")) {
                firebaseRef.unauth();
                setAuthenticatedUser(null);
            }
        }
    }

    /**
     * Intent to go to main activity
     */
    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }
}
