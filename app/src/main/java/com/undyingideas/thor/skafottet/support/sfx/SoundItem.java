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

package com.undyingideas.thor.skafottet.support.sfx;

/**
 * Created on 17-01-2016, 17:32.
 * Project : skafottet
 * Container class for sound item
 *
 * @author rudz
 */
public class SoundItem {

    public int id;
    public float volume;
    public boolean stop;

    public SoundItem(final int id, final float volume) {
        this.id = id;
        this.volume = volume;
    }

    public SoundItem(final boolean stop) { this.stop = stop; }
}