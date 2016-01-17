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
    private boolean isLoaded;
    private SoundItem[] soundItems = new SoundItem[7];
    private int[] sounds = new int[7];
    protected SoundPoolHelper soundPoolHelper;

    private SoundThread soundThread;



    private @RawRes final int[] sound_raw = {
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

        soundPoolHelper = new SoundPoolHelper();

        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(soundPoolHelper);

        loadSounds();

        soundThread = new SoundThread(soundPool);


        soundThread.start();

        /* configure soundItems */


    }

    protected void loadSounds() {
        for (int i = 0; i < sound_raw.length; i++) {
            soundItems[i] = new SoundItem(soundPool.load(this, sound_raw[i], 1), volume);
//            soundThread.sounds.add(new SoundItem(soundPool))
//            sounds[i] = soundPool.load(this, sound_raw[i], 1);
        }
    }

    protected void playSound(final int index) {
        soundThread.sounds.add(soundItems[index]);
//        if (isLoaded) {
//            soundPool.play(sounds[index], volume, volume, 1, 0, 1f);
//        }
    }

    private class SoundPoolHelper implements SoundPool.OnLoadCompleteListener {
        @Override
        public void onLoadComplete(final SoundPool soundPool, final int sampleId, final int status) {
            isLoaded = true;
        }
    }


}
