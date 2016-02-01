package com.undyingideas.thor.skafottet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.login.CreateAccountActivity;
import com.undyingideas.thor.skafottet.activities.login.LoginActivity;
import com.undyingideas.thor.skafottet.activities.support.Foreground;
import com.undyingideas.thor.skafottet.firebase.auth.AuthDataHolder;
import com.undyingideas.thor.skafottet.firebase.auth.AuthListener;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

/**
 * BaseActivity class is used as a base class for all activities in the app
 * It implements GoogleApiClient callbacks to enable "Logout" in all activities
 * and defines variables that are being shared across all activities
 */
public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        AuthDataHolder.AuthListenerData,
        Foreground.Listener
{
    protected String mProvider, mEncodedEmail;
    /* Client used to interact with Google APIs. */
    protected GoogleApiClient mGoogleApiClient;

    protected AuthListener mAuthListener;
    protected AuthDataHolder authDataHolder;

    // TODO : Move connection state observer to here

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Foreground.get(this).addListener(this);

        if (GameUtility.getConnectionStatus() > -1 && !GameUtility.isLoggedIn()) {
        /* Setup the Google API object to allow Google logins */
            final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            /**
             * Build a GoogleApiClient with access to the Google Sign-In API and the
             * options specified by gso.
             */

        /* Setup the Google API object to allow Google+ logins */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            /**
             * Getting mProvider and mEncodedEmail from SharedPreferences
             */
            /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
            mEncodedEmail = GameUtility.getPrefs().getString(Constant.KEY_ENCODED_EMAIL, null);
            mProvider = GameUtility.getPrefs().getString(Constant.KEY_PROVIDER, null);

            if (!(this instanceof LoginActivity || this instanceof CreateAccountActivity)) {
//                GameUtility.setFirebase(new Firebase(Constant.FIREBASE_URL));
                mAuthListener = new AuthListener();
                authDataHolder = new AuthDataHolder(this);
                mAuthListener.addSlave(authDataHolder);
                GameUtility.getFirebase().addAuthStateListener(mAuthListener);
            }
        } else {
//            WindowLayout.showSnack("Ingen internetforbindelse.", findViewById(R.id.fragment_content), true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (!(this instanceof LoginActivity || this instanceof CreateAccountActivity) && GameUtility.getFirebase() != null) {
            GameUtility.getFirebase().removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) { super.onSaveInstanceState(outState); }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        /* Inflate the menu; this adds items to the action bar if it is present. */
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        boolean returnVal = false;

        if (id == android.R.id.home) {
            onBackPressed();
            returnVal = true;
        } else if (id == R.id.action_logout) {
            logout();
            returnVal = true;
        }
        return returnVal || super.onOptionsItemSelected(item);
    }

    protected void initializeBackground(LinearLayout linearLayout) {

//        /**
//         * Set different background image for landscape and portrait layouts
//         */
//        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            linearLayout.setBackgroundResource(R.drawable.background_loginscreen_land);
//        } else {
//            linearLayout.setBackgroundResource(R.drawable.background_loginscreen);
//        }
    }

    /**
     * Logs out the user from their current session and starts LoginActivity.
     * Also disconnects the mGoogleApiClient if connected and provider is Google
     */
    protected void logout() {
        /* Logout if mProvider is not null */
        if (mProvider != null) {

            if (mProvider.equals(Constant.GOOGLE_PROVIDER)) {
                /* Logout from Google+ */
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new GoogleStatusResultCallback());
            }
        }
        GameUtility.setIsLoggedIn(false);
        GameUtility.getFirebase().unauth();
    }

    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        // nothing here yet...
    }

    @Override
    public void firebaseAuthDataReceived(final boolean loggedIn) {
        WindowLayout.showSnack("Du er nu logged " + (loggedIn ? "ind." : "ud."), getWindow().getDecorView(), true);
        GameUtility.setIsLoggedIn(loggedIn);
        if (!loggedIn) {
            GameUtility.getPrefs().remove(Constant.KEY_ENCODED_EMAIL);
            GameUtility.getPrefs().remove(Constant.KEY_PROVIDER);
        }
    }

    @Override
    public void onBecameForeground() {

    }

    @Override
    public void onBecameBackground() {

    }

    private static class GoogleStatusResultCallback implements ResultCallback<Status> {
        @Override
        public void onResult(@NonNull final Status status) {
            //nothing
        }
    }

    private class FirebaseAuthStateListener implements Firebase.AuthStateListener {
        @Override
        public void onAuthStateChanged(final AuthData authData) {
            if (authData == null) {
                /* The user has been logged out */
                /* Clear out shared preferences */
                GameUtility.getPrefs().remove(Constant.KEY_ENCODED_EMAIL);
                GameUtility.getPrefs().remove(Constant.KEY_PROVIDER);
                takeUserToLoginScreenOnUnAuth();
            }
        }
    }
}
