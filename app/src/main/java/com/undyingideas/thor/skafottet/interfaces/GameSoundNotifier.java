package com.undyingideas.thor.skafottet.interfaces;

/**
 * Created on 17-01-2016, 18:04.
 * Project : skafottet
 *
 * @author rudz
 */
public interface GameSoundNotifier {

    // so the fragments can notify the acticity which sound effect to play !

    void playGameSound(final int sound);

}
