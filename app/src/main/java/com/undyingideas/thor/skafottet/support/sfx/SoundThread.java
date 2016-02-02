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

package com.undyingideas.thor.skafottet.support.sfx;

import android.media.SoundPool;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created on 17-01-2016, 17:34.
 * Project : skafottet
 * Sound thread to playback sounds.
 *
 * @author rudz
 */
public class SoundThread extends Thread {
    private final SoundPool soundPool;
    public final LinkedBlockingQueue<SoundItem> sounds = new LinkedBlockingQueue<>();
    private volatile boolean stop;

    public SoundThread(final SoundPool soundPool) { this.soundPool = soundPool; }

    public void unloadSound(final int id) { soundPool.unload(id); }

    @Override
    public void run() {
        try {
            SoundItem item;
            while (!stop) {
                item = sounds.take();
                if (item.stop) {
                    stop = true;
                    break;
                }
                soundPool.play(item.id, item.volume, item.volume, 0, 0, 1);
            }
        } catch (final InterruptedException e) {
            Log.d("SoundThread", "Interrupted");
//            e.printStackTrace();
        }
    }
}