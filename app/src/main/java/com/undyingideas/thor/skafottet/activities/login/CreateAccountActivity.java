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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.BaseActivity;
import com.undyingideas.thor.skafottet.support.firebase.Utils;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.utility.Constant;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents Sign up screen and functionality of the app
 */
public class CreateAccountActivity extends BaseActivity {
    private static final String LOG_TAG = CreateAccountActivity.class.getSimpleName();

    //    private ProgressDialog mAuthProgressDialog;

    private Firebase mFirebaseRef;

    private EditText mEditTextUsernameCreate;
    private EditText mEditTextEmailCreate;

    private String mUserName;
    private String mUserEmail;
    private String mPassword;

    private final SecureRandom mRandom = new SecureRandom();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        /**
         * Create Firebase references
         */
        mFirebaseRef = new Firebase(Constant.FIREBASE_URL);

        /**
         * Link layout elements from XML and setup the progress dialog
         */
        initializeScreen();
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

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
            WindowLayout.hideStatusBar(getWindow(), null);
        }
    }

    /**
     * Link layout elements from XML and setup the progress dialog
     */
    public void initializeScreen() {
        mEditTextUsernameCreate = (EditText) findViewById(R.id.edit_text_username_create);
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
//        LinearLayout linearLayoutCreateAccountActivity = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
//        initializeBackground(linearLayoutCreateAccountActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        WindowLayout.setMd(new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.progress_dialog_loading))
                .content(getResources().getString(R.string.progress_dialog_check_inbox))
                .cancelable(false)
                .progress(true, 0));
    }

    /**
     * Open LoginActivity when user taps on "Sign in" textView
     */
    public void onSignInPressed(final View view) {
        final Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Create new account using Firebase email/password provider
     */
    public void onCreateAccountPressed(final View view) {
        mUserName = mEditTextUsernameCreate.getText().toString();
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = new BigInteger(130, mRandom).toString(32);

        /**
         * Check that email and user name are okay
         */
        if (!isEmailValid(mUserEmail) || !isUserNameValid(mUserName)) return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        WindowLayout.getMd().show();

        /**
         * Create new user with specified email and password
         */
        mFirebaseRef.createUser(mUserEmail, mPassword, new CreateUserValueResultHandler());


    }

    /**
     * Creates a new user in Firebase from the Java POJO
     */
    private void createUserInFirebaseHelper(final String authUserId) {
        final String encodedEmail = Utils.encodeEmail(mUserEmail);

        /**
         * Create the user and uid mapping
         */
        final HashMap<String, Object> userAndUidMapping = new HashMap<>();

        /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
        final HashMap<String, Object> timestampJoined = new HashMap<>();
        timestampJoined.put(Constant.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

        /* Create a HashMap version of the user to add */
        final PlayerDTO newUser = new PlayerDTO(mUserName, encodedEmail, timestampJoined);
        final HashMap<String, Object> newUserMap = (HashMap<String, Object>) new ObjectMapper().convertValue(newUser, Map.class);

        /* Add the user and UID to the update map */
        userAndUidMapping.put('/' + Constant.FIREBASE_LOCATION_USERS + '/' + encodedEmail, newUserMap);
        userAndUidMapping.put('/' + Constant.FIREBASE_LOCATION_UID_MAPPINGS + '/' + authUserId, encodedEmail);

        /* Try to update the database; if there is already a user, this will fail */
        mFirebaseRef.updateChildren(userAndUidMapping, new UserCreateCompletionListener(authUserId, encodedEmail));
    }

    private boolean isEmailValid(final String email) {
        final boolean isGoodEmail = email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid), email));
        }
        return isGoodEmail;
    }

    private boolean isUserNameValid(final String userName) {
        if (userName.isEmpty()) {
            mEditTextUsernameCreate.setError(getResources().getString(R.string.error_cannot_be_empty));
            return false;
        }
        return true;
    }


    /**
     * Show error toast to users
     */
    private void showErrorToast(final String message) {
        WindowLayout.showSnack(message, mEditTextEmailCreate, false);
    }

    private class UserCreateCompletionListener implements Firebase.CompletionListener {
        private final String authUserId;
        private final String encodedEmail;

        public UserCreateCompletionListener(final String authUserId, final String encodedEmail) {
            this.authUserId = authUserId;
            this.encodedEmail = encodedEmail;
        }

        @Override
        public void onComplete(final FirebaseError firebaseError, final Firebase firebase) {
            if (firebaseError != null) {
                /* Try just making a uid mapping */
                mFirebaseRef.child(Constant.FIREBASE_LOCATION_UID_MAPPINGS).child(authUserId).setValue(encodedEmail);
            }
            /**
             *  The value has been set or it failed; either way, log out the user since
             *  they were only logged in with a temp password
             **/
            mFirebaseRef.unauth();
        }
    }

    private class CreateUserValueResultHandler implements Firebase.ValueResultHandler<Map<String, Object>> {
        @Override
        public void onSuccess(final Map<String, Object> result) {
            /**
             * If user was successfully created, run resetPassword() to send temporary 24h
             * password to the user's email and make sure that user owns specified email
             */
            mFirebaseRef.resetPassword(mUserEmail, new ResetPasswordResultHandler(result));


        }

        @Override
        public void onError(final FirebaseError firebaseError) {
            /* Error occurred, log the error and dismiss the progress dialog */
            Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError);
            WindowLayout.getMd().dismiss();
            /* Display the appropriate error message */
            if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                mEditTextEmailCreate.setError(getString(R.string.error_email_taken));
            } else {
                showErrorToast(firebaseError.getMessage());
            }

        }

        private class ResetPasswordResultHandler implements Firebase.ResultHandler {
            private final Map<String, Object> result;

            public ResetPasswordResultHandler(final Map<String, Object> result) {
                this.result = result;
            }

            @Override
            public void onSuccess() {

                mFirebaseRef.authWithPassword(mUserEmail, mPassword, new AuthResultHandler());

            }

            @Override
            public void onError(final FirebaseError firebaseError) {
                /* Error occurred, log the error and dismiss the progress dialog */
                Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError);
                WindowLayout.getMd().dismiss();
            }

            private class AuthResultHandler implements Firebase.AuthResultHandler {
                @Override
                public void onAuthenticated(final AuthData authData) {
                    WindowLayout.getMd().dismiss();
                    Log.i(LOG_TAG, getString(R.string.log_message_auth_successful));

                    /**
                     * Save name and email to sharedPreferences to create User database record
                     * when the registered user will sign in for the first time
                     */
                    GameUtility.getPrefs().putString(Constant.KEY_SIGNUP_EMAIL, mUserEmail);

                    /**
                     * Encode user email replacing "." with ","
                     * to be able to use it as a Firebase db key
                     */
                    createUserInFirebaseHelper((String) result.get("uid"));

                    /**
                     *  Password reset email sent, open app chooser to pick app
                     *  for handling inbox email intent
                     */
                    final Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    try {
                        startActivity(intent);
                        finish();
                    } catch (final android.content.ActivityNotFoundException ex) {
                        /* User does not have any app to handle email */
                    }
                }

                @Override
                public void onAuthenticationError(final FirebaseError firebaseError) {
                    Log.e(LOG_TAG, firebaseError.getMessage());
                }
            }
        }
    }
}
