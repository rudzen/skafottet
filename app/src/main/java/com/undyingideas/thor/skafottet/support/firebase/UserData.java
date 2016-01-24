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

package com.undyingideas.thor.skafottet.support.firebase;

import com.firebase.client.AuthData;

/**
 * Created on 23-01-2016, 18:48.
 * Project : skafottet
 *
 * @author rudz
 */
public class UserData {

    private static final String KEY_USER_DATA = "kuser_data";

    private AuthData authData;
    private UserData userData;

    public UserData(final AuthData authData, final UserData userData) {
        this.authData = authData;
    }

    public void setAuthData(final AuthData newAuthData) {
        authData = newAuthData;
    }

    public AuthData getAuthData() {
        return authData;
    }

    public final String getToken() {
        return authData.getToken();
    }

    public final String getProvider() {
        return authData.getProvider();
    }

}
