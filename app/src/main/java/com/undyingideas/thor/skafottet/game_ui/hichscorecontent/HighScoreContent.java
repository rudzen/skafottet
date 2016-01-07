package com.undyingideas.thor.skafottet.game_ui.hichscorecontent;

import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.WordStatus;

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
public class HighScoreContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<HighScoreItem> ITEMS = new ArrayList<HighScoreItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, HighScoreItem> ITEM_MAP = new HashMap<String, HighScoreItem>();

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

    public static void initItems(int position, PlayerDTO p, ArrayList<LobbyPlayerStatus> lobby){
        for (int i = 1; i <= COUNT; i++) {
            addItem(createHighScoreItem(i, p, lobby ));
        }
    }

    public static void addItem(HighScoreItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static HighScoreItem createHighScoreItem(int position, PlayerDTO player, ArrayList<LobbyPlayerStatus> gamesPlayedByPlayer)  {
        //Creates
        return new HighScoreItem(String.valueOf(position), player.getName() + " : Score " + player.getScore(), makeDetails(position,gamesPlayedByPlayer));
    }

    //This is a list of games that the player has played.
    public static String makeDetails(int position, ArrayList<LobbyPlayerStatus> playedGames) {
        //This list should be sorted for the player only.
        StringBuilder builder = new StringBuilder();
       builder.append("Rank " + position +"\nWords Guessed\n");
        for (int i = 0 ; i < 50 ; i ++) {
            //Adds nextline (Word : Score xxxx) to details
            builder.append("\n" + playedGames.get(0).getWordList().get(0).getWordID())
                    .append(" -> Score : ").append(playedGames.get(0).getWordList().get(0).getScore());
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

        public HighScoreItem(String id, String content, String details) {
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
