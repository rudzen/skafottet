package com.undyingideas.thor.skafottet.firebase.help;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase.help<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 16:21
 * </p>
 * Slave class for {@link HelpFileListener}.<br>
 * It uses {@link HelpFileDTO} to store any updated help text received from firebase.
 * It will save the new data received (if any), and the game should automaticly detect the new information when entering the help screen.<br>
 * It will notify the player that a new help file has been downloaded and that the game will use that from now on.
 */
public class HelpFileListenerSlave implements Runnable {
    // TODO : A whole bunch of crap.

    @Override
    public void run() {

    }
}
