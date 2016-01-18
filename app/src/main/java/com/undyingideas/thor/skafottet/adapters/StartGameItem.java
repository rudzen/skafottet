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

package com.undyingideas.thor.skafottet.adapters;

import android.support.annotation.DrawableRes;

/**
 * Created on 27-12-2015, 10:01.
 * Project : R-TicTacToe
 *
 * @author rudz
 */
public class StartGameItem {

    public final int itemId;
    public final CharSequence itemName;
    public final CharSequence description;
    public @DrawableRes
    final int resourceIcon;

    // constructor
    public StartGameItem(final int itemId, final CharSequence itemName, final CharSequence description, final @DrawableRes int resourceIcon) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.resourceIcon = resourceIcon;
    }
}
