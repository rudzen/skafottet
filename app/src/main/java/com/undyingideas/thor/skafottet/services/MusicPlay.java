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

package com.undyingideas.thor.skafottet.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.activities.support.Foreground;

import java.io.IOException;

public class MusicPlay extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, Foreground.Listener {

    private static final String TAG = "MusicPlayService";

    public static final String ACTION_PLAY = "SKAFOTMUSIK";
    public static final String ACTION_STOP = "STOPSKAFOTT";
    private static final int music = R.raw.insidious;
    private static final String PLAYER = "Player";
    private static MusicPlay mMusicPlayInstance;

    private MediaPlayer mMediaPlayer;

    private byte mState;
    private static final byte STATE_RETRIEVING = 0;
    private static final byte STATE_STOPPED = 1;
    private static final byte STATE_PREPARING = 2;
    private static final byte STATE_PLAYING = 3;
    private static final byte STATE_PAUSED = 4;

    @Override
    public void onCreate() {
        mMusicPlayInstance = this;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(PLAYER, "onStartCommand");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                Log.d(PLAYER, "Filter was correct");
//            InputStream inputStream = getResources().openRawResource(R.raw.reign_supreme);
                mMediaPlayer = MediaPlayer.create(this, music); // initialize it here
                mMediaPlayer.setVolume(0.3f, 0.3f);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Foreground.get(mMusicPlayInstance).addListener(this);
                initMediaPlayer();
            } else if (intent.getAction().equals(ACTION_STOP)) {
                if (isPlaying()) {
                    stopMusic();
                }
            }
        } else {
//            if (mMediaPlayer != null) {
//                mMediaPlayer.stop();
//                mMediaPlayer.release();
//            }
//            return -1;
        }

        return START_STICKY;
    }

    private void initMediaPlayer() {
        try {
            final AssetFileDescriptor afd = getApplicationContext().getResources().openRawResourceFd(music);
            if (afd == null) return;
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
            mState = STATE_PREPARING;
            return;
        } catch (final IllegalStateException ise) {
            Log.d(TAG, "initMediaPlayer: IllegaState, perhaps it's too eager!");
        } catch (final IOException e) {
            e.printStackTrace();
        }
        mState = STATE_STOPPED;
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
            Foreground.get(mMusicPlayInstance).removeListener(this);
        }
        mState = STATE_RETRIEVING;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private void pauseMusic() {
        if (mState == STATE_PLAYING) {
            mMediaPlayer.pause();
            mState = STATE_PAUSED;
        }
    }

    private void startMusic() {
        try {
            mMediaPlayer.start();
            mState = STATE_PLAYING;
        } catch (final Exception e) {
            e.printStackTrace();
            mState = STATE_STOPPED;
        }
//        if (mState != STATE_PREPARING && mState != STATE_RETRIEVING) {
//        } else {
//        }
    }

    private void stopMusic() {
        if (mState == STATE_PLAYING || mState == STATE_PAUSED) {
            mMediaPlayer.stop();
            mState = STATE_STOPPED;
        }
    }

    public boolean isPlaying() {
        return mState == STATE_PLAYING;
    }

    public static MusicPlay getInstance() {
        return mMusicPlayInstance;
    }

    @Override
    public void onBecameForeground() {
        Log.d(TAG, "Music started");
        startMusic();
    }

    @Override
    public void onBecameBackground() {
        Log.d(TAG, "Music stopped");
        stopMusic();
    }

}