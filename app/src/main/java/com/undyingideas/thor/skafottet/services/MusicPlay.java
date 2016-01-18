package com.undyingideas.thor.skafottet.services;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.support.Foreground;

public class MusicPlay extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

    public static Intent intent = new Intent();

    private static final String ACTION_PLAY = "SKAFOTMUSIK";
    private static final int music = R.raw.reign_supreme;
    private static MusicPlay mInstance;

    private MediaPlayer mMediaPlayer;

    private static byte STATE;
    private static final byte STATE_RETRIEVING = 0;
    private static final byte STATE_STOPPED = 1;
    private static final byte STATE_PREPARING = 2;
    private static final byte STATE_PLAYING = 3;
    private static final byte STATE_PAUSED = 4;

    Foreground.Listener myListener = new ForegroundListener();

    @Override
    public void onCreate() {
        mInstance = this;
        Foreground.get(mInstance).addListener(myListener);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("Player", "onStartCommand");
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_PLAY)) {
            Log.d("Player", "Filter was correct");
            mMediaPlayer = MediaPlayer.create(this, music); // initialize it here
            mMediaPlayer.setVolume(0.3f, 0.3f);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            initMediaPlayer();
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            return -1;
        }

        return START_STICKY;
    }

    private void initMediaPlayer() {


        // currently not used!

//        try {
//
////            mMediaPlayer.setDataSource(mUrl);
//        } catch (IllegalArgumentException | IOException | IllegalStateException e) {
//            e.printStackTrace();
//        }

        try {
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        STATE = STATE_PREPARING;
    }

    @Override
    public void onPrepared(final MediaPlayer player) {
        startMusic();
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        // not really used atm...
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            Foreground.get(mInstance).removeListener(myListener);
        }
        STATE = STATE_RETRIEVING;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void pauseMusic() {
        if (STATE == STATE_PLAYING) {
            mMediaPlayer.pause();
            STATE = STATE_PAUSED;
        }
    }

    public void startMusic() {
        mMediaPlayer.start();
        STATE = STATE_PLAYING;

        // this guard is not needed for now in this app.

//        if (mState != State.Preparing && mState != State.Retrieving) {
//        }
    }

    public void stopMusic() {
        if (STATE == STATE_PLAYING || STATE == STATE_PAUSED) {
            mMediaPlayer.stop();
            STATE = STATE_STOPPED;
        }
    }

    public boolean isPlaying() {
        return STATE == STATE_PLAYING;
    }

    public static MusicPlay getInstance() {
        return mInstance;
    }

    private class ForegroundListener implements Foreground.Listener {
        @Override
        public void onBecameForeground() {
            Log.d("Foreground" , "Became foreground!");
            startMusic();
        }

        @Override
        public void onBecameBackground() {
            Log.d("Foreground" , "Became background!");
            pauseMusic();
        }
    }
}