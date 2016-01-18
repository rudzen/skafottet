package com.undyingideas.thor.skafottet.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.support.Foreground;

public class MusicPlay extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, Foreground.Listener {

    public static Intent intent = new Intent();

    private static final String ACTION_PLAY = "SKAFOTMUSIK";
    private static final int music = R.raw.reign_supreme;
    private static MusicPlay mInstance;

    private MediaPlayer mMediaPlayer;

    public static int lifecounter;

    // interface implementation for fore/background detection
    @Override
    public void onBecameForeground() {

    }

    @Override
    public void onBecameBackground() {

    }



    // indicates the state our service:
    enum State {
        Retrieving,
        Stopped,
        Preparing,
        Playing,
        Paused
    }

    State mState = State.Retrieving;

    @Override
    public void onCreate() {
        mInstance = this;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("Player", "onStartCommand");
        if (intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_PLAY)) {
            Log.d("Player", "Filter was correct");
            mMediaPlayer = MediaPlayer.create(this, music); // initialize it here
            mMediaPlayer.setVolume(0.2f, 0.2f);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setLooping(true);

//            mMediaPlayer.setAudioStreamType(AudioManager.RES);
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
        mState = State.Preparing;
    }

    @Override
    public void onPrepared(final MediaPlayer player) {
        startMusic();
    }

    @Override
    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mState = State.Retrieving;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void pauseMusic() {
        if (mState == State.Playing) {
            mMediaPlayer.pause();
            mState = State.Paused;
        }
    }

    public void startMusic() {
        mMediaPlayer.start();
        mState = State.Playing;
//        if (mState != State.Preparing && mState != State.Retrieving) {
//        }
    }

    public boolean isPlaying() {
        return mState == State.Playing;
    }

    public static MusicPlay getInstance() {
        return mInstance;
    }

}