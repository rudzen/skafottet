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

package com.undyingideas.thor.skafottet.activities;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RawRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.sfx.SoundItem;
import com.undyingideas.thor.skafottet.support.sfx.SoundThread;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

/**
 * Created on 17-01-2016, 17:10.
 * Project : skafottet
 *
 * @author rudz
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class SoundAbstract extends AppCompatActivity implements SoundPool.OnLoadCompleteListener {

    private final static String TAG = "SoundAbstract";

    /* sound stuff */
    @RawRes
    final
    private int[] sound_raw = {
            R.raw.guess_wrong,
            R.raw.guess_right,
            R.raw.intro,
            R.raw.menu_click,
            R.raw.won,
            R.raw.lost,
            R.raw.challenge
    };

    private SoundPool soundPool;
    private float volume;

    private final static int SOUND_COUNT = 7;
    private final SoundItem[] soundItems = new SoundItem[SOUND_COUNT];

    protected SoundThread soundThread;
    private boolean loaded;

    protected void initSound() {
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = makeSoundPool();
        soundPool.setOnLoadCompleteListener(this);

        loadSounds();

        // If the thread isnt initialized or it was accidently GC'd..
        if (soundThread == null) {
            soundThread = new SoundThread(soundPool);
            soundThread.start();
        }
    }

    @SuppressWarnings("deprecation")
    private SoundPool makeSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            return new SoundPool.Builder()
                    .setMaxStreams(4)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            return new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    private void loadSounds() {
        for (int i = 0; i < SOUND_COUNT; i++) {
            soundItems[i] = new SoundItem(soundPool.load(this, sound_raw[i], 1), volume);
        }
    }

    void playSound(final int index) {
        Log.d(TAG, String.valueOf(GameUtility.settings.PREFS_SFX));
        if (GameUtility.settings.PREFS_SFX) {
            if (loaded) {
                soundThread.sounds.add(soundItems[index]);
            }
        }
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        loaded = true;
    }
}
