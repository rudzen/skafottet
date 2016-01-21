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

package com.undyingideas.thor.skafottet.activities.support;

import java.lang.ref.WeakReference;

/**
 * Created on 20-01-2016, 17:25.
 * Project : skafottet
 * // not used for anyting right now.
 * @author rudz
 */
public class WeakReferenceHolder<E> {

    protected final WeakReference<E> weakReference;

    public WeakReferenceHolder(final E objectReference) {
        weakReference = new WeakReference<>(objectReference);
    }

}