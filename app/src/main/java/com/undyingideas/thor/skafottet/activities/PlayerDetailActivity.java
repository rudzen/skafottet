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

package com.undyingideas.thor.skafottet.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.fragments.PlayerDetailFragment;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

/**
 * An activity representing a single Player detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link PlayerListActivity}.
 */
public class PlayerDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_detail);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloaterClickHandler());

        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            final Bundle arguments = new Bundle();
            arguments.putParcelable(PlayerDetailFragment.ARG_ITEM_ID, getIntent().getParcelableExtra(PlayerDetailFragment.ARG_ITEM_ID));
            final PlayerDetailFragment fragment = PlayerDetailFragment.newInstance(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.player_detail_container, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpTo(new Intent(this, PlayerListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class FloaterClickHandler implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            WindowLayout.showSnack("Challange sent!", view, false);
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(new Intent(this, GameActivity.class));
        super.finish();
    }
}
