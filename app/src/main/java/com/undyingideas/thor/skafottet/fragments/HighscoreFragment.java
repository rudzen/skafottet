/*
 * Copyright 2016 Rudy Alex Kohn
 *
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

package com.undyingideas.thor.skafottet.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.adapters.HighscoreListAdapter;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * <p>Created on 12-01-2016, 07:24.<br>
 * Project : skafottet</p>
 * This activity is responsible for a couple of things.
 * <p/>
 * v1.1 - 24.01.2016, rudz<br>
 * - Converted to fragment for better sharing of context in application<br>
 *
 * @author rudz
 */
public class HighscoreFragment extends Fragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener,
        NavigationView.OnNavigationItemSelectedListener
{

    private static final String TAG = "HighscoreFragment";

    // TODO : Create a more suitable adapter to view in the list.
    private HighscoreListAdapter mAdapter;
    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView navigationView;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;

    private Toolbar toolbar;

    private Handler handler;
    private Runnable refreshStopper;

    private static IFragmentFlipper iFragmentFlipper;

    private static boolean local;
    private static final String LOCAL_KEY = "lkey";

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof IFragmentFlipper) {
            iFragmentFlipper = (IFragmentFlipper) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IFragmentFlipper.");
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshStopper = new RefreshStopper();
        handler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_highscore, container, false);

        /* set up the view */
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.highscore_refresh_layout);
        stickyList = (StickyListHeadersListView) root.findViewById(R.id.list);
        stickyList.addHeaderView(inflater.inflate(R.layout.word_list_header, null));
        stickyList.addFooterView(inflater.inflate(R.layout.word_list_footer, null));
        stickyList.setEmptyView(root.findViewById(R.id.highscore_empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);

        toolbar = (Toolbar) root.findViewById(R.id.toolbar);

        mDrawerLayout = (DrawerLayout) root.findViewById(R.id.highscore_drawer_layout);

        navigationView = (NavigationView) root.findViewById(R.id.highscore_nav_view);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        /* configure the view */
        refreshLayout.setOnRefreshListener(new SwipeOnRefreshListener(this));

        mAdapter = new HighscoreListAdapter(getContext(), GameUtility.getHighscoreManager().getScores());

        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);
        stickyList.setAdapter(mAdapter);
        stickyList.setStickyHeaderTopOffset(0);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.highscore_title));
        toolbar.setSubtitle("Lokal");
        toolbar.setCollapsible(false);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setLogoDescription("Applikations logo");
        toolbar.setNavigationContentDescription("Home icon");
//        toolbar.inflateMenu(R.menu.menu_word_list);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.highscore_drawer_open, R.string.highscore_drawer_close);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            local = savedInstanceState.getBoolean(LOCAL_KEY);
        } else {
            local = true;
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onDetach() {
        iFragmentFlipper = null;
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        outState.putBoolean(LOCAL_KEY, local);
        super.onSaveInstanceState(outState);
    }

    public static HighscoreFragment newInstance(final boolean local) {
        final HighscoreFragment highscoreFragment = new HighscoreFragment();
        final Bundle args = new Bundle();
        args.putBoolean(LOCAL_KEY, local);
        highscoreFragment.setArguments(args);
        return highscoreFragment;
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_word_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private boolean updateCurrentList() {
        if (!local) {
            // TODO : re-read data from dataholder into adapter and reset the listview
        }
        return true;
    }

    /* ************************************************************************ */
    /* ************************************************************************ */
    /* ********************** Interface Overrides ***************************** */
    /* ************************************************************************ */
    /* ************************************************************************ */

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        // for planned multiplayer challenge word selection update.
        //Toast.makeText(this, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(final StickyListHeadersListView l, final View header, final int itemPosition, final long headerId, final boolean currentlySticky) {
        // not sure, but fun...
        //Toast.makeText(this, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStickyHeaderOffsetChanged(final StickyListHeadersListView l, final View header, final int offset) {
        header.setAlpha(1 - offset / (float) header.getMeasuredHeight());
    }

    @Override
    public void onStickyHeaderChanged(final StickyListHeadersListView l, final View header, final int itemPosition, final long headerId) {
        header.setAlpha(1);
    }

    /* ************************************************************************ */
    /* ************************************************************************ */
    /* *********************** Helper Methods ********************************* */
    /* ************************************************************************ */
    /* ************************************************************************ */

    public void refreshList() {
        // meeh
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /* ************************************************************************ */
    /* ************************************************************************ */
    /* *********************** Helper Classes ********************************* */
    /* ************************************************************************ */
    /* ************************************************************************ */

    private class RefreshStopper implements Runnable {
        @Override
        public void run() { refreshLayout.setRefreshing(false); }
    }

    private static class SwipeOnRefreshListener extends WeakReferenceHolder<HighscoreFragment> implements SwipeRefreshLayout.OnRefreshListener {

        public SwipeOnRefreshListener(final HighscoreFragment wordListFragment) {
            super(wordListFragment);
        }

        @Override
        public void onRefresh() {
            final HighscoreFragment wordListFragment = weakReference.get();
            if (wordListFragment != null) {
                if (!wordListFragment.updateCurrentList()) {
                    wordListFragment.handler.postDelayed(wordListFragment.refreshStopper, 500);
                } else {
                    wordListFragment.handler.post(wordListFragment.refreshStopper);
                }
            }
        }
    }
}
