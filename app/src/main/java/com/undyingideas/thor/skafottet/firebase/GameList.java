package com.undyingideas.thor.skafottet.firebase;

/**
 * <p>
 * Project : skafottet<br>
 * Package : com.undyingideas.thor.skafottet.firebase<br>
 * Created by : rudz<br>
 * On : jan.30.2016 - 16:25
 * </p>
 * Implementation of the GameList class for which all non-annonymous users will utilize to handle multiplayer games.
 */
public class GameList {

    private String key; // holds the lobby key for this game
    private String opponent; // who are you playing against?
    private String theWord; // what word are being guessed on?
    private boolean isPlayed; // have you played this game?
    private int score = -10000; // how many points did you get? -10000 if not played yet.



}
