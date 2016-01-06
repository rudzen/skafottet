package com.undyingideas.thor.skafottet;

import android.webkit.WebView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 04-01-2016, 06:34.
 * Project : skafottet
 *
 * @author Gump
 */
public class HighScoreController {

    final ArrayList<PlayerScore> players = new ArrayList<>();
    Firebase ref;
    int limit;
    WebView theWebView;

    public HighScoreController(final Firebase firebaseRef, final int limit, final ArrayList<PlayerScore> list, final WebView theWebView) {
        //Setting up reference to firebase.
        this.limit = limit;
        ref = firebaseRef;
        if (!players.isEmpty()) players.clear();
        players.addAll(list);
        this.theWebView = theWebView;
        ref = new Firebase("https://hangmandtu.firebaseio.com/");
        //Query orders
//        Query query = ref.orderByValue().limitToFirst(limit);
//        query.addValueEventListener(new fireBaseValueEventListener());
        players.add(new PlayerScore("Sam Wise", 10));
        ref.addValueEventListener(new FireBaseEventListener());

    }

    //Method creates a new Highscore, currently does not update i player exists.
    public void createHighScore(final PlayerScore p) {
        final Firebase postRef = ref;
        //This genetates uniqe ID in firebase
        final Firebase newPostRef = postRef.push();

        //Putting the player object into a hashmap with "player" key.

        // TODO : Replace with SparseArray if possible
        final Map<String, PlayerScore> post1 = new HashMap<>();
        post1.put("playerscore", p);
        newPostRef.setValue(post1);

        // Get the unique ID generated by push()
        final String postId = newPostRef.getKey();


    }


    class FireBaseEventListener implements ValueEventListener {

        @Override
        public void onDataChange(final DataSnapshot snapshot) {
            players.add(new PlayerScore("Sam Damn wise", 10));

            for (final DataSnapshot shot : snapshot.getChildren()) {
                for (final DataSnapshot dataSnapshot : shot.getChildren()) {
                    final PlayerScore playerScore = dataSnapshot.getValue(PlayerScore.class);
                    players.add(playerScore);
                }
            }
            final StringBuilder sb = new StringBuilder(100);
            sb.append("<html><body><ol>");
            for (final PlayerScore p : players) {
                sb.append(wrap(p.toString()));
            }
            sb.append("</ol></body></html>");

            theWebView.loadData(sb.toString(), "text/html; charset=UTF-8", null);
        }

        @Override
        public void onCancelled(final FirebaseError firebaseError) {

        }
    }

    private static String wrap(final String text) {
        return "<li>" + text + "</li>";
    }


}