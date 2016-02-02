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
