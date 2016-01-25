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

    private final static String TAG = "MusicPlayService";

    public static final String ACTION_PLAY = "SKAFOTMUSIK";
    public static final String ACTION_STOP = "STOPSKAFOTT";
    private static final int music = R.raw.insidious;
    private static MusicPlay musicPlayInstance;

    private MediaPlayer mMediaPlayer;

    private byte state;
    private static final byte STATE_RETRIEVING = 0;
    private static final byte STATE_STOPPED = 1;
    private static final byte STATE_PREPARING = 2;
    private static final byte STATE_PLAYING = 3;
    private static final byte STATE_PAUSED = 4;

    @Override
    public void onCreate() {
        musicPlayInstance = this;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d("Player", "onStartCommand");
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_PLAY)) {
                Log.d("Player", "Filter was correct");
//            InputStream inputStream = getResources().openRawResource(R.raw.reign_supreme);
                mMediaPlayer = MediaPlayer.create(this, music); // initialize it here
                mMediaPlayer.setVolume(0.3f, 0.3f);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                Foreground.get(musicPlayInstance).addListener(this);
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
        } catch (final IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        state = STATE_PREPARING;
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
            Foreground.get(musicPlayInstance).removeListener(this);
        }
        state = STATE_RETRIEVING;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    private void pauseMusic() {
        if (state == STATE_PLAYING) {
            mMediaPlayer.pause();
            state = STATE_PAUSED;
        }
    }

    private void startMusic() {
        try {
            mMediaPlayer.start();
            state = STATE_PLAYING;
        } catch (final Exception e) {
            e.printStackTrace();
            state = STATE_STOPPED;
        }
//        if (state != STATE_PREPARING && state != STATE_RETRIEVING) {
//        } else {
//        }
    }

    private void stopMusic() {
        if (state == STATE_PLAYING || state == STATE_PAUSED) {
            mMediaPlayer.stop();
            state = STATE_STOPPED;
        }
    }

    public boolean isPlaying() {
        return state == STATE_PLAYING;
    }

    public static MusicPlay getInstance() {
        return musicPlayInstance;
    }

    @Override
    public void onBecameForeground() {
        Log.d(TAG, "Music started");
        startMusic();
    }

    @Override
    public void onBecameBackground() {
        Log.d(TAG, "Music stopped");
        pauseMusic();
    }

}