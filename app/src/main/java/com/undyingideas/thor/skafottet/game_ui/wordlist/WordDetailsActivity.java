/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <s133235@student.dtu> wrote this file. As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return - Rudy Alex Kohn
 * ----------------------------------------------------------------------------
 */

package com.undyingideas.thor.skafottet.game_ui.wordlist;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Responsible for showin the word lists
 * <p>Supports dual layout :</p>
 * <li>
 *     Both lists and words are displayed in landscape
 * </li>
 * <li>
 *     Lists are shown as default in portrait, when clicking on it, the words show
 * </li>
 * @author rudz
 */
public class WordDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* check if in landscape mode */
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
        } else if (savedInstanceState == null) {
            /* instance was null, create the fragment and place it */
            final WordDetailsFragment details = new WordDetailsFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        /* TESTING */
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }
}