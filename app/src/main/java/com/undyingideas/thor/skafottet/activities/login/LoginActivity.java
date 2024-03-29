/*
 * Copyright 2016 Rudy Alex Kohn [s133235@student.dtu.dk]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.undyingideas.thor.skafottet.activities.login;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Scope;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.BaseActivity;
import com.undyingideas.thor.skafottet.activities.GameActivity;
import com.undyingideas.thor.skafottet.support.firebase.Utils;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;
import static com.undyingideas.thor.skafottet.support.utility.GameUtility.getPrefs;

/**
 * Represents Sign in screen and functionality of the app
 */
public class LoginActivity extends BaseActivity  implements LoaderManager.LoaderCallbacks<Cursor> {

    // TODO : Integrate with application AuthListener.
    // TODO : Make LogIn button like the rest of the application.
    // TODO : Create change password button to the left of LogIn button.

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /* A dialog that is presented until the Firebase authentication finished. */
//    private ProgressDialog mAuthProgressDialog;

    /* References to the Firebase */
//    private Firebase mFirebaseRef;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    private AutoCompleteTextView mEditTextEmailInput;
    private EditText mEditTextPasswordInput;

    private final RelativeLayout[] mButtons = new RelativeLayout[3];

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;
    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         * Create Firebase references
         */
//        mFirebaseRef = GameUtility.getFirebase(); // new Firebase(Constant.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup progress dialog
         */
        initializeScreen();

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mEditTextPasswordInput.setOnEditorActionListener(new PasswordEditActionListener());

        /* if the user has chosen to keep their login/password, restore here and proceed with login */
        if (GameUtility.getSettings().keepLogin && !GameUtility.isLoggedIn()) {
            if (GameUtility.getMe().getEmail() != null) {
                mEditTextEmailInput.setText(Utils.decodeEmail(GameUtility.getMe().getEmail()));
                //mEditTextPasswordInput.setText(GameUtility.getPrefs().getString(Constant.PASSWORD_LAST));
//                signInPassword();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * This is the authentication listener that maintains the current user session
         * and signs in automatically on application launch
         */
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(final AuthData authData) {
                if (WindowLayout.getMd().isShowing()) {
                    WindowLayout.getMd().dismiss();
                }

                /**
                 * If there is a valid session to be restored, start MainActivity.
                 * No need to pass data via SharedPreferences because app
                 * already holds userName/provider data from the latest session
                 */
                if (authData != null && GameUtility.isLoggedIn()) {
                    final Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    LoginActivity.this.startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        };
        /* Add auth listener to Firebase ref */
        GameUtility.getFirebase().addAuthStateListener(mAuthStateListener);

        /**
         * Get the newly registered user email if present, use null as default value
         */
        final String signupEmail = GameUtility.getPrefs().getString(Constant.KEY_SIGNUP_EMAIL, null);

        /**
         * Fill in the email editText and remove value from SharedPreferences if email is present
         */
        if (signupEmail != null) {
            mEditTextEmailInput.setText(signupEmail);

            /**
             * Clear signupEmail sharedPreferences to make sure that they are used just once
             */
            GameUtility.getPrefs().remove(Constant.KEY_SIGNUP_EMAIL);
        }
    }

    /**
     * Cleans up listeners tied to the user's authentication state
     */
    @Override
    public void onPause() {
        super.onPause();
        GameUtility.getFirebase().removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
            WindowLayout.hideStatusBar(getWindow(), null);
        }
    }

    /**
     * Override onCreateOptionsMenu to inflate nothing
     *
     * @param menu The menu with which nothing will happen
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return true;
    }


    /**
     * Sign in with Password provider when user clicks sign in button
     */
    public void onSignInPressed(final View view) {
        signInPassword();
    }

    /**
     * Open CreateAccountActivity when user taps on "Sign up" TextView
     */
    public void onSignUpPressed(final View view) {
        final Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {

        mButtons[0] = (RelativeLayout) findViewById(R.id.login_with_password);
        mButtons[1] = (RelativeLayout) findViewById(R.id.login_button_change_password);
        mButtons[2] = (RelativeLayout) findViewById(R.id.login_button_reset_password);

        final LogInOnClickListener logInOnClickListener = new LogInOnClickListener();

        for (final RelativeLayout relativeLayout : mButtons) {
            relativeLayout.setOnClickListener(logInOnClickListener);
        }

        mEditTextEmailInput = (AutoCompleteTextView) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);
        final LinearLayout linearLayoutLoginActivity = (LinearLayout) findViewById(R.id.linear_layout_login_activity);
        initializeBackground(linearLayoutLoginActivity);
        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        WindowLayout.setMd(new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog_loading)
                .content(R.string.progress_dialog_authenticating_with_firebase)
                .cancelable(false)
                .progress(true, 0));

        /* set up autocomplete for email text field */
        populateAutoComplete();


        /* Setup Google Sign In */
        setupGoogleSignIn();
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mButtons[0], R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {LoginActivity.this.requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);}
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }
    /**
     * Sign in with Password provider (used when user taps "Done" action on keyboard)
     */
    public void signInPassword() {
        final String email = mEditTextEmailInput.getText().toString();
        final String password = mEditTextPasswordInput.getText().toString();

        /**
         * If email and password are not empty show progress dialog and try to authenticate
         */
        if (email.isEmpty()) {
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (password.isEmpty()) {
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        WindowLayout.getMd().show();
        GameUtility.getFirebase().authWithPassword(email, password, new MyAuthResultHandler(Constant.PASSWORD_PROVIDER));
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int i, final Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
        final ArrayList<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> cursorLoader) { }

    private void addEmailsToAutoComplete(final List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        mEditTextEmailInput.setAdapter(adapter);
    }

    private static class GoogleSignInClickListener implements View.OnClickListener {
        private final SignInButton signInButton;

        public GoogleSignInClickListener(final SignInButton signInButton) {this.signInButton = signInButton;}

        @Override
        public void onClick(final View v) {
            WindowLayout.showSnack("Ikke tilføjet endnu.", signInButton, true);
//                onSignInGooglePressed(v);
        }
    }


    /**
     * Handle user authentication that was initiated with mFirebaseRef.authWithPassword
     * or mFirebaseRef.authWithOAuthToken
     */
    private class MyAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;

        public MyAuthResultHandler(final String provider) {
            this.provider = provider;
        }

        /**
         * On successful authentication call setAuthenticatedUser if it was not already
         * called in
         */
        @Override
        public void onAuthenticated(final AuthData authData) {
            WindowLayout.getMd().dismiss();
            Log.i(LOG_TAG, provider + ' ' + getString(R.string.log_message_auth_successful));

            if (authData != null) {
                /**
                 * If user has logged in with Google provider
                 */
                if (authData.getProvider().equals(Constant.PASSWORD_PROVIDER)) {
                    setAuthenticatedUserPasswordProvider(authData);
                } else
                /**
                 * If user has logged in with Password provider
                 */
                    if (authData.getProvider().equals(Constant.GOOGLE_PROVIDER)) {
                        setAuthenticatedUserGoogle(authData);
                    } else {
                        Log.e(LOG_TAG, getString(R.string.log_error_invalid_provider) + authData.getProvider());
                    }

                /* Save provider name and encodedEmail for later use and start MainActivity */
                GameUtility.getPrefs().putString(Constant.KEY_PROVIDER, authData.getProvider());
                GameUtility.getPrefs().putString(Constant.KEY_ENCODED_EMAIL, mEncodedEmail);

                /* Go to main activity */
                final Intent intent = new Intent(LoginActivity.this, GameActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onAuthenticationError(final FirebaseError firebaseError) {
            WindowLayout.getMd().dismiss();

            /**
             * Use utility method to check the network connection state
             * Show "No network connection" if there is no connection
             * Show Firebase specific error message otherwise
             */
            switch (firebaseError.getCode()) {
                case FirebaseError.INVALID_EMAIL:
                case FirebaseError.USER_DOES_NOT_EXIST:
                    mEditTextEmailInput.setError(getString(R.string.error_message_email_issue));
                    break;
                case FirebaseError.INVALID_PASSWORD:
                    mEditTextPasswordInput.setError(firebaseError.getMessage());
                    break;
                case FirebaseError.NETWORK_ERROR:
                    showErrorToast(getString(R.string.error_message_failed_sign_in_no_network));
                    break;
                default:
                    showErrorToast(firebaseError.toString());
            }
        }
    }

    /**
     * Helper method that makes sure a user is created if the user
     * logs in with Firebase's email/password provider.
     *
     * @param authData AuthData object returned from onAuthenticated
     */
    private void setAuthenticatedUserPasswordProvider(final AuthData authData) {
        final String unprocessedEmail = authData.getProviderData().get(Constant.FIREBASE_PROPERTY_EMAIL).toString().toLowerCase();
        /**
         * Encode user email replacing "." with ","
         * to be able to use it as a Firebase db key
         */
        mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

        final Firebase userRef = new Firebase(Constant.FIREBASE_URL_USERS).child(mEncodedEmail);

        /**
         * Check if current user has logged in at least once
         */
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                GameUtility.setMe(dataSnapshot.getValue(PlayerDTO.class));

                if (GameUtility.getMe() != null) {

                    /**
                     * If recently registered user has hasLoggedInWithPassword = "false"
                     * (never logged in using password provider)
                     */
                    if (!GameUtility.getMe().isHasLoggedInWithPassword()) {
                        /**
                         * Change password if user that just signed in signed up recently
                         * to make sure that user will be able to use temporary password
                         * from the email more than 24 hours
                         */
                        GameUtility.getFirebase().changePassword(unprocessedEmail, mEditTextPasswordInput.getText().toString(), mEditTextPasswordInput.getText().toString(), new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {
                                userRef.child(Constant.FIREBASE_PROPERTY_USER_HAS_LOGGED_IN_WITH_PASSWORD).setValue(true);
                                /* The password was changed */
                                Log.d(LOG_TAG, getString(R.string.log_message_password_changed_successfully) + mEditTextPasswordInput.getText().toString());
                                getPrefs().putString(Constant.PASSWORD_LAST, mEditTextPasswordInput.getText().toString());
                                getPrefs().putString(Constant.USER_LAST, unprocessedEmail);
                            }

                            @Override
                            public void onError(final FirebaseError firebaseError) {
                                Log.d(LOG_TAG, getString(R.string.log_error_failed_to_change_password) + firebaseError);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(final FirebaseError firebaseError) {
                Log.e(LOG_TAG, getString(R.string.log_error_the_read_failed) + firebaseError.getMessage());
            }
        });

    }

    /**
     * Helper method that makes sure a user is created if the user
     * logs in with Firebase's Google login provider.
     *
     * @param authData AuthData object returned from onAuthenticated
     */
    @SuppressWarnings("unchecked")
    private void setAuthenticatedUserGoogle(final AuthData authData) {
        /**
         * If google api client is connected, get the lowerCase user email
         * and save in sharedPreferences
         */
        final String unprocessedEmail;
        if (mGoogleApiClient.isConnected()) {
            unprocessedEmail = mGoogleAccount.getEmail().toLowerCase();
            GameUtility.getPrefs().putString(Constant.KEY_GOOGLE_EMAIL, unprocessedEmail);
        } else {

            /**
             * Otherwise get email from sharedPreferences, use null as default value
             * (this mean that user resumes his session)
             */
            unprocessedEmail = GameUtility.getPrefs().getString(Constant.KEY_GOOGLE_EMAIL, null);
        }

        /**
         * Encode user email replacing "." with "," to be able to use it
         * as a Firebase db key
         */
        mEncodedEmail = Utils.encodeEmail(unprocessedEmail);

        /* Get username from authData */
        final String userName = (String) authData.getProviderData().get(Constant.PROVIDER_DATA_DISPLAY_NAME);

        /* Make a user */
        final Firebase userLocation = new Firebase(Constant.FIREBASE_URL_USERS).child(mEncodedEmail);

        final HashMap<String, Object> userAndUidMapping = new HashMap<>();

        final HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(Constant.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        /* Create a HashMap version of the user to add */
        final PlayerDTO newUser = new PlayerDTO(userName, mEncodedEmail, timestampJoined);
        final HashMap<String, Object> newUserMap = (HashMap<String, Object>) new ObjectMapper().convertValue(newUser, Map.class);

        /* Add the user and UID to the update map */
        userAndUidMapping.put('/' + Constant.FIREBASE_LOCATION_USERS + '/' + mEncodedEmail,
                newUserMap);
        userAndUidMapping.put('/' + Constant.FIREBASE_LOCATION_UID_MAPPINGS + '/' + authData.getUid(), mEncodedEmail);

        /* Update the database; it will fail if a user already exists */
        GameUtility.getFirebase().updateChildren(userAndUidMapping, new FirebaseUserCompletionListener(authData));
    }

    /**
     * Show error toast to users
     */
    private void showErrorToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    /**
     * Signs you into ShoppingList++ using the Google Login Provider
     *
     * @param token A Google OAuth access token returned from Google
     */
    private void loginWithGoogle(final String token) {
        GameUtility.getFirebase().authWithOAuthToken(Constant.GOOGLE_PROVIDER, token, new MyAuthResultHandler(Constant.GOOGLE_PROVIDER));
    }

    /**
     * GOOGLE SIGN IN CODE
     * <p/>
     * This code is mostly boiler plate from
     * https://developers.google.com/identity/sign-in/android/start-integrating
     * and
     * https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java
     * <p/>
     * The big picture steps are:
     * 1. User clicks the sign in with Google button
     * 2. An intent is started for sign in.
     * - If the connection fails it is caught in the onConnectionFailed callback
     * - If it finishes, onActivityResult is called with the correct request code.
     * 3. If the sign in was successful, set the mGoogleAccount to the current account and
     * then call get GoogleOAuthTokenAndLogin
     * 4. getGoogleOAuthTokenAndLogin launches an AsyncTask to get an OAuth2 token from Google.
     * 5. Once this token is retrieved it is available to you in the onPostExecute method of
     * the AsyncTask. **This is the token required by Firebase**
     */


    /* Sets up the Google Sign In Button : https://developers.google.com/android/reference/com/google/android/gms/common/SignInButton */
    private void setupGoogleSignIn() {
        final SignInButton signInButton = (SignInButton) findViewById(R.id.login_with_google);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        // TODO : Use regular click listener.
        signInButton.setOnClickListener(new GoogleSignInClickListener(signInButton));
    }

    /**
     * Sign in with Google plus when user clicks "Sign in with Google" textView (button)
     */
    public void onSignInGooglePressed(final View view) {
        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        WindowLayout.getMd().show();
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult result) {
        /**
         * An unresolvable error has occurred and Google APIs (including Sign-In) will not
         * be available.
         */
        WindowLayout.getMd().dismiss();
        showErrorToast(result.toString());
    }


    /**
     * This callback is triggered when any startActivityForResult finishes. The requestCode maps to
     * the value passed into startActivityForResult.
     */
    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /* Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...); */
        if (requestCode == RC_GOOGLE_LOGIN) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

    }

    private void handleSignInResult(final GoogleSignInResult result) {
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            /* Signed in successfully, get the OAuth token */
            mGoogleAccount = result.getSignInAccount();
            getGoogleOAuthTokenAndLogin();
        } else {
            if (result.getStatus().getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                showErrorToast("The sign in was cancelled. Make sure you're connected to the internet and try again.");
            } else {
                showErrorToast("Error handling the sign in: " + result.getStatus().getStatusMessage());
            }
            WindowLayout.getMd().dismiss();
        }
    }

    /**
     * Gets the GoogleAuthToken and logs in.
     */
    private void getGoogleOAuthTokenAndLogin() {
        /* Get OAuth token in Background */
        final AsyncTask<Void, Void, String> task = new GoogleLoginAsyncTask();

        task.execute();
    }

    private class LogInOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View v) {
            if (v == mButtons[0]) {
                signInPassword();
            } else if (v == mButtons[1]) {
                WindowLayout.showSnack("Ikke tilføjet endnu.", mButtons[1], true);
            } else if (v == mButtons[2]) {
                WindowLayout.showSnack("Ikke tilføjet endnu.", mButtons[2], true);
            }
        }
    }

    private class GoogleLoginAsyncTask extends AsyncTask<Void, Void, String> {
        String mErrorMessage;

        @Override
        protected String doInBackground(final Void... params) {
            String token = null;

            try {
                final String scope = String.format(getString(R.string.oauth2_format), new Scope(Scopes.PROFILE)) + " email";

                token = GoogleAuthUtil.getToken(LoginActivity.this, mGoogleAccount.getEmail(), scope);
            } catch (final IOException transientEx) {
                /* Network or server error */
                Log.e(LOG_TAG, getString(R.string.google_error_auth_with_google) + transientEx);
                mErrorMessage = getString(R.string.google_error_network_error) + transientEx.getMessage();
            } catch (final UserRecoverableAuthException e) {
                Log.w(LOG_TAG, getString(R.string.google_error_recoverable_oauth_error) + e.toString());

                /* We probably need to ask for permissions, so start the intent if there is none pending */
                if (!mGoogleIntentInProgress) {
                    mGoogleIntentInProgress = true;
                    final Intent recover = e.getIntent();
                    startActivityForResult(recover, RC_GOOGLE_LOGIN);
                }
            } catch (final GoogleAuthException authEx) {
                /* The call is not ever expected to succeed assuming you have already verified that
                 * Google Play services is installed. */
                Log.e(LOG_TAG, ' ' + authEx.getMessage(), authEx);
                mErrorMessage = getString(R.string.google_error_auth_with_google) + authEx.getMessage();
            }
            return token;
        }

        @Override
        protected void onPostExecute(final String token) {
            WindowLayout.getMd().dismiss();
            if (token != null) {
                /* Successfully got OAuth token, now login with Google */
                loginWithGoogle(token);
            } else if (mErrorMessage != null) {
                showErrorToast(mErrorMessage);
            }
        }
    }

    private class FirebaseUserCompletionListener implements Firebase.CompletionListener {
        private final AuthData authData;

        public FirebaseUserCompletionListener(final AuthData authData) {this.authData = authData;}

        @Override
        public void onComplete(final FirebaseError firebaseError, final Firebase firebase) {
            if (firebaseError != null) {
                /* Try just making a uid mapping */
                GameUtility.getFirebase().child(Constant.FIREBASE_LOCATION_UID_MAPPINGS).child(authData.getUid()).setValue(mEncodedEmail);
            }
        }
    }

    private class PasswordEditActionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(final TextView textView, final int actionId, final KeyEvent keyEvent) {

            if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                LoginActivity.this.signInPassword();
            }
            return true;
        }
    }
}