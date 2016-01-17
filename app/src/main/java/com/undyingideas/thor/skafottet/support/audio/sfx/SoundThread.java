package com.undyingideas.thor.skafottet.support.audio.sfx;

import android.media.SoundPool;

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
    public LinkedBlockingQueue<SoundItem> sounds = new LinkedBlockingQueue<>();
    public volatile boolean stop;

    public SoundThread(final SoundPool soundPool) {
        this.soundPool = soundPool;
    }

    public void unloadSound(final int id) {
        soundPool.unload(id);
    }

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
            e.printStackTrace();
        }
    }
}