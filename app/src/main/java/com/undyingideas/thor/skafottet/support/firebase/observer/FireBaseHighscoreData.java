///*
// * Copyright 2016 Rudy Alex Kohn
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.undyingideas.thor.skafottet.support.firebase.observer;
//
//import android.util.Log;
//
//import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
//
///**
// * Created on 29-01-2016, 07:28.
// * Project : skafottet<br>
// * Firebase highscore observer data class.<br>
// * Combines observer and DTO for login response status.
// * For use with {@link PlayerController}.<br>
// * - Updated for skafottet's data structure.<br>
// * @author rudz
// */
//public class FireBaseHighscoreData extends WeakReferenceHolder<FireBaseHighscoreData.FirebaseHighScoreResponse> implements Runnable {
//
//    /* the TAG so we know who we are */
//    private final static String TAG = "HighscoreNotifier";
//
//    /* the is there data availble?, this is set through the reciever itself */
//    private boolean data; // is true if logged in, false if not!
//
//    /**
//     * This interface has to be implemented by the target, this ensures that we can send data to it.<br>
//     * This is a combination of controlling the runnable interface + the needs for the {@link PlayerController}.<br>
//     * In essence, it "replaces" the Runnable interface with a more controlled one.<br>
//     * @author rudz
//     */
//    public interface FirebaseHighScoreResponse {
//        void oHighScoreResponse(boolean result);
//    }
//
//    /**
//     * Construct a new Runnable takeover class for firebases login response.
//     * @param firebaseLoginResponse The object which implements the interface.
//     */
//    public FireBaseHighscoreData(final FirebaseHighScoreResponse firebaseLoginResponse) {
//        super(firebaseLoginResponse);
//    }
//
//    /**
//     * The runnable interface is started here, this will make sure there still is a reference to the master class.
//     * If the references is obtained, it will send the data back to it through its own interface.
//     */
//    @Override
//    public void run() {
//        Log.d(TAG, "Observer runnable started.");
//        final FirebaseHighScoreResponse internetRecieverInterface = weakReference.get();
//        if (internetRecieverInterface != null) {
//            internetRecieverInterface.oHighScoreResponse(data);
//        }
//    }
//
//    /**
//     * Simple setter..
//     * @param newValue the new value to apply
//     */
//    public void setData(final boolean newValue) {
//        data = newValue;
//    }
//
//
//}
