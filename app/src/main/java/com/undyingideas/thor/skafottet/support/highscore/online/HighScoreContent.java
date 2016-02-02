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

package com.undyingideas.thor.skafottet.support.highscore.online;

import android.util.Log;

import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public final class HighScoreContent {

    /**
     * An array of sample (dummy) items.
     */
    private static final List<HighScoreItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, HighScoreItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 25;


    //MAKEING TEST FILLING FOR HIGHSCORE
//    static {
//         Add PlayerDTO and arrayList<LobbyPlayerStatus> in constructer
//        PlayerDTO p;
//        ArrayList<LobbyPlayerStatus> lobby = new ArrayList<>();
//        ArrayList<WordStatus> word = new ArrayList<>();
//        word.add(new WordStatus("Mordor",100));
//        lobby.add(new LobbyPlayerStatus("Frodo " ,word));
//
//        for (int i = 1; i <= COUNT; i++) {
//             addItem(createHighScoreItem(i, p, lobby ));
//        }
//    }

    public static void initItems(final int position, final PlayerDTO p, final ArrayList<LobbyPlayerStatus> lobby){
        for (int i = 1; i <= COUNT; i++) {
            addItem(createHighScoreItem(i, p, lobby ));
        }
    }

    public static void addItem(final HighScoreItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static HighScoreItem createHighScoreItem(final int position, final PlayerDTO player, final ArrayList<LobbyPlayerStatus> gamesPlayedByPlayer)  {
        //Creates
        return new HighScoreItem(String.valueOf(position), player.getName() + " : Score " + player.getScore(), makeDetails(position,gamesPlayedByPlayer,player.getName()));
    }

    //This is a list of games that the player has played.
    private static String makeDetails(final int position, final ArrayList<LobbyPlayerStatus> playedGames, final String playerName) {
        //This list should be sorted for the player only.
        final StringBuilder builder = new StringBuilder();
       builder.append("Rank ").append(position).append("\nWords Guessed\n");
        for (final LobbyPlayerStatus game : playedGames) {
            //Adds nextline (Word : Score xxxx) to details
            Log.d("highscore", "heloo");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class HighScoreItem {
        public final String id;
        public final String content;
        public final String details;

        public HighScoreItem(final String id, final String content, final String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
