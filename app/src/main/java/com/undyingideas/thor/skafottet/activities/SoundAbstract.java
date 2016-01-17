package com.undyingideas.thor.skafottet.activities;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RawRes;
import android.support.v7.app.AppCompatActivity;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.support.audio.sfx.SoundItem;
import com.undyingideas.thor.skafottet.support.audio.sfx.SoundThread;

/**
 * Created on 17-01-2016, 17:10.
 * Project : skafottet
 *
 * @author rudz
 */
public class SoundAbstract extends AppCompatActivity {

    // empty shell abstraction for sound playback

    /* sound stuff */
    private SoundPool soundPool;
    private float actVolume, maxVolume, volume;
    private AudioManager audioManager;
    private SoundItem[] soundItems = new SoundItem[7];
//    protected SoundPoolHelper soundPoolHelper;

    private SoundThread soundThread;

    private @RawRes
    int[] sound_raw = {
            R.raw.game_wrong1,
            R.raw.game_wrong2,
            R.raw.game_right,
            R.raw.menu_click,
            R.raw.won,
            R.raw.lost,
            R.raw.challenge
    };

    protected void initSound() {
        /* set up the sound stuff */
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

//        soundPoolHelper = new SoundPoolHelper();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//        soundPool.setOnLoadCompleteListener(soundPoolHelper);

        loadSounds();

        if (soundThread == null) {
            soundThread = new SoundThread(soundPool);
        }
        soundThread.start();
    }

    @Override
    protected void onStop() {
        soundThread.stop = true;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        soundItems = null;
        sound_raw = null;
        soundThread = null;
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        soundThread.stop = true;
        super.onPause();
    }

    @Override
    protected void onResume() {
        soundThread.stop = false;
        try {
            soundThread.start();
        } catch (final IllegalThreadStateException itse) {
            // it was already started.
        }
        super.onResume();
    }

    protected void loadSounds() {
        for (int i = 0; i < sound_raw.length; i++) {
            soundItems[i] = new SoundItem(soundPool.load(this, sound_raw[i], 1), volume);
        }
    }

    protected void playSound(final int index) {
        soundThread.sounds.add(soundItems[index]);
    }
}
