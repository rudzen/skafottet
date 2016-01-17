//package com.undyingideas.thor.skafottet.support.audio.sfx;
//
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.SoundPool;
//import android.os.Handler;
//import android.support.annotation.RawRes;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//
//import com.undyingideas.thor.skafottet.R;
//
//import java.lang.ref.WeakReference;
//import java.util.Objects;
//import java.util.Random;
//
///**
// * Created on 17-01-2016, 14:02.
// * Project : skafottet
// * This is not done yet!!!...
// * @author rudz
// */
//public class SfxPlay implements SoundPool.OnLoadCompleteListener, Runnable {
//
//    public static final int GAME_WRONG_1 = 0;
//    public static final int GAME_WRONG_2 = 1;
//    public static final int GAME_RIGHT = 2;
//    public static final int MENU_CLICK = 3;
//    public static final int WON = 4;
//    public static final int LOST = 5;
//    public static final int CHALLENGE = 6;
//
//    private SoundPool soundPool;
//    private float actVolume, maxVolume, volume;
//    private AudioManager audioManager;
//
//    private @RawRes int[] sound_raw = {
//            R.raw.game_wrong1,
//            R.raw.game_wrong2,
//            R.raw.game_right,
//            R.raw.menu_click,
//            R.raw.won,
//            R.raw.lost,
//            R.raw.challenge
//    };
//
//    private int[] sounds = new int[7];
//    private boolean[] ready = new boolean[7];
//    private int playIndex;
//
//    private static final String TAG = "SfxPlay";
//
//    private WeakReference<AppCompatActivity> appCompatActivityWeakReference;
//
//    public SfxPlay(final AppCompatActivity appCompatActivity) {
//        reconfigure(appCompatActivity);
//    }
//
//    public void reconfigure(final AppCompatActivity appCompatActivity) {
//        appCompatActivityWeakReference = new WeakReference<>(appCompatActivity);
//    }
//
//    private void setup() {
//        final AppCompatActivity appCompatActivity = appCompatActivityWeakReference.get();
//        if (appCompatActivity != null) {
//            audioManager = (AudioManager) appCompatActivity.getSystemService(AppCompatActivity.AUDIO_SERVICE);
//            actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//            maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//            volume = actVolume / maxVolume;
//            appCompatActivity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
//            soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
//            soundPool.setOnLoadCompleteListener(this);
//            for (int i = 0; i < ready.length; i++) {
//                ready[i] = false;
//                sounds[i] = soundPool.load(appCompatActivity, sound_raw[i], 1);
//            }
//        }
//    }
//
//    @Override
//    public void onLoadComplete(final SoundPool soundPool, final int sampleId, final int status) {
//        final boolean isLoaded = status == 0 && Objects.equals(soundPool, this.soundPool);
//        if (isLoaded) {
//            for (int i = 0; i < sounds.length; i++) {
//                if (sampleId == sounds[i]) {
//                    ready[i] = true;
//                    Log.d(TAG, "Sound " + i + " is loaded.");
//                    break;
//                }
//            }
//        }
//    }
//
//    @Override
//    public void run() {
//        if (ready[playIndex]) {
//            soundPool.play(sounds[playIndex], volume, volume, 1, 0, 1f);
//        }
//    }
//
//    public void playSound(final int index) {
//        if (ready[index]) {
//            if (index > 1) {
//                playIndex = index;
//            } else {
//                playIndex = (int) System.nanoTime() % 2;
//                Log.d(TAG, "Wrong sound : " + playIndex);
//            }
//        }
//    }
//
//
//}
