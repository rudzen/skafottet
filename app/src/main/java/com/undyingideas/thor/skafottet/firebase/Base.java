/*
 * Copyright 2016 Rudy Alex Kohn
 *
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

package com.undyingideas.thor.skafottet.firebase;

import android.content.Context;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.support.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.support.utility.Constant;

/**
 * Created on 23-01-2016, 19:24.
 * Project : skafottet
 * Base class that holds the basic stuff for firebase.
 * @author rudz
 */
public final class Base {

    @SuppressWarnings("StaticVariableOfConcreteClass")
    public static MultiplayerController mpc;
    public static Firebase firebase;

    public static void initFb(final Context context) {
        Firebase.setAndroidContext(context);
        firebase = new Firebase(Constant.HANGMANDTU_FIREBASEIO);
        firebase.keepSynced(true);
        mpc = new MultiplayerController(firebase);
    }

}
