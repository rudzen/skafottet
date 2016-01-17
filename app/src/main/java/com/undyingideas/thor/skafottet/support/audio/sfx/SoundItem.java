package com.undyingideas.thor.skafottet.support.audio.sfx;

/**
 * Created on 17-01-2016, 17:32.
 * Project : skafottet
 * Container class for sound item
 *
 * @author rudz
 */
public class SoundItem {

    public int id;
    public float volume;
    public boolean stop;

    public SoundItem(final int id, final float volume) {
        this.id = id;
        this.volume = volume;
    }

    public SoundItem(final boolean stop) { this.stop = stop; }
}