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
