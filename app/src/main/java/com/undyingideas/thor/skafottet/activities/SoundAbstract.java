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

package com.undyingideas.thor.skafottet.activities;

import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.annotation.RawRes;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.sfx.SoundItem;
import com.undyingideas.thor.skafottet.support.sfx.SoundThread;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

/**
 * Created on 17-01-2016, 17:10.
 * Project : skafottet
 *
 * @author rudz
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class SoundAbstract extends BaseActivity implements SoundPool.OnLoadCompleteListener {

    private final static String TAG = "SoundAbstract";

    /* sound stuff */
    @RawRes
    final
    private int[] mSoundRaw = {
            R.raw.guess_wrong,
            R.raw.guess_right,
            R.raw.intro,
            R.raw.menu_click,
            R.raw.lost,
            R.raw.challenge
    };

    private SoundPool mSoundPool;
    private float mVolume;

    private final static int SOUND_COUNT = 6;
    private final SoundItem[] mSoundItems = new SoundItem[SOUND_COUNT];

    SoundThread mSoundThread;
    private boolean mLoaded;

    void initSound() {
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mVolume = actVolume / maxVolume;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mSoundPool = makeSoundPool();
        mSoundPool.setOnLoadCompleteListener(this);

        loadSounds();

        // If the thread isnt initialized or it was accidently GC'd..
        if (mSoundThread == null) {
            mSoundThread = new SoundThread(mSoundPool);
            mSoundThread.start();
        }
    }

    @SuppressWarnings("deprecation")
    private static SoundPool makeSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            return new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            return new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    private void loadSounds() {
        for (int i = 0; i < SOUND_COUNT; i++) {
            mSoundItems[i] = new SoundItem(mSoundPool.load(this, mSoundRaw[i], 1), mVolume);
        }
    }

    void playSound(final int index) {
        Log.d(TAG, String.valueOf(GameUtility.getSettings().prefsSfx));
        if (GameUtility.getSettings().prefsSfx) {
            if (mLoaded) {
                mSoundThread.mSoundItems.add(mSoundItems[index]);
            } else {
                WindowLayout.showSnack("Lydfil ikke indhentet endnu,", findViewById(R.id.fragment_content), true);
            }
        }
    }

    @Override
    public void onLoadComplete(final SoundPool soundPool, final int sampleId, final int status) {
        mLoaded = true;
    }
}
