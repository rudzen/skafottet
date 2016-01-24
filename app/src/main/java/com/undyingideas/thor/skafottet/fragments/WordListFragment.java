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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.adapters.WordListAdapter;
import com.undyingideas.thor.skafottet.adapters.WordTitleLocalAdapter;
import com.undyingideas.thor.skafottet.adapters.WordTitleRemoteAdapter;
import com.undyingideas.thor.skafottet.broadcastrecievers.InternetRecieverData;
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.controller.WordListController;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.StringHelper;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordListDownloader;

import net.steamcrafted.loadtoast.LoadToast;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.regex.Pattern;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_wordController;

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
public class WordListFragment extends Fragment implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener,
        InternetRecieverData.InternetRecieverInterface {

    private static final String TAG = "WordListFragment";

    private WordListAdapter mAdapter;
    public DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;

    private Toolbar toolbar;

    private ListView listRemote, listLocal;

    public MaterialDialog md; // for add list

    private WordTitleLocalAdapter adapterLocal;
    private WordTitleRemoteAdapter adapterRemote;

    private Handler handler;
    private Runnable refreshStopper;

    private InternetRecieverData internetRecieverData;

    private static IFragmentFlipper iFragmentFlipper;

    public static WordListFragment newInstance() {
        final WordListFragment wordListFragment = new WordListFragment();
        final Bundle args = new Bundle();
        // TODO : Insert arguments
        wordListFragment.setArguments(args);
        return wordListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_word_list, container, false);

        /* set up the view */
        refreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.word_list_refresh_layout);
        stickyList = (StickyListHeadersListView) root.findViewById(R.id.list);
        stickyList.addHeaderView(inflater.inflate(R.layout.word_list_header, null));
        stickyList.addFooterView(inflater.inflate(R.layout.word_list_footer, null));
        stickyList.setEmptyView(root.findViewById(R.id.word_list_empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);

        toolbar = (Toolbar) root.findViewById(R.id.toolbar);

        mDrawerLayout = (DrawerLayout) root.findViewById(R.id.word_item_drawer_layout);

        /* configure the side bar lists */
        listRemote = (ListView) mDrawerLayout.findViewById(R.id.nav_drawer_remote_lists);
        listLocal = (ListView) mDrawerLayout.findViewById(R.id.nav_drawer_local_lists);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        /* configure the view */
        refreshLayout.setOnRefreshListener(new SwipeOnRefreshListener(this));

        mAdapter = new WordListAdapter(getContext(), s_wordController.getCurrentList());

        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);
        stickyList.setAdapter(mAdapter);
        stickyList.setStickyHeaderTopOffset(0);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.word_list_title));
        toolbar.setSubtitle("Antal Lister : " + s_wordController.getListCount()); // "<nuværende liste>"); // vil blive sat dynamisk ved klik på liste og ved opstart!
        toolbar.setCollapsible(false);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setLogoDescription("Applikations logo");
        toolbar.setNavigationContentDescription("Home icon");
        toolbar.inflateMenu(R.menu.menu_word_list);

        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.word_list_drawer_open, R.string.word_list_drawer_close);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        /* configure the adapters for the side bar lists */
        adapterLocal = new WordTitleLocalAdapter(getContext(), R.layout.word_list_nav_drawer_list, s_wordController.getLocalWords());
        adapterRemote = new WordTitleRemoteAdapter(getContext(), R.layout.word_list_nav_drawer_list, WordListController.getKeyList());

        listRemote.setAdapter(adapterRemote);
        listLocal.setAdapter(adapterLocal);

        listRemote.setOnItemClickListener(new ListRemoteTitleClickListener());
        listLocal.setOnItemClickListener(new ListLocalTitleClickListener());

        WindowLayout.s_LoadToast = new LoadToast(getContext());
        WindowLayout.s_LoadToast.setProgressColor(Color.BLACK);
        WindowLayout.s_LoadToast.setTextColor(Color.WHITE);
        WindowLayout.s_LoadToast.setBackgroundColor(Color.RED);
        WindowLayout.s_LoadToast.setTranslationY(WindowLayout.screenDimension.y / 3);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();

        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshStopper = new RefreshStopper();
        handler = new Handler();
    }

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
    public void onDetach() {
        iFragmentFlipper = null;
        super.onDetach();
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
        final int id = item.getItemId();
        final boolean returnValue;
        if (id == R.id.action_word_list_add) {
            if (GameUtility.getConnectionStatus() > -1) {
            /* show Yes/No dialog here! */
                new MaterialDialog.Builder(getContext())
                        .content("Vil du tilføje en ordliste?")
                        .cancelable(true)
                        .onPositive(new AddWordListDialogListener(this))
                        .positiveText(R.string.dialog_yes)
                        .negativeText(R.string.dialog_no)
                        .title("Tilføj Ordliste")
                        .show();
            } else {
                WindowLayout.showSnack("Ingen forbindelse til internettet.", stickyList, true);
            }
            returnValue = true;
        } else if (id == R.id.action_word_list_remove) {

            final StringBuilder oldListName = new StringBuilder(20);
            oldListName.append('\'');
            if (s_wordController.isLocal()) {
                oldListName.append(s_wordController.getLocalWords().get(s_wordController.getIndexLocale()).getTitle());
            } else {
                oldListName.append(s_wordController.getIndexRemote());
            }

            oldListName.append("' ");

            if (s_wordController.removeCurrentList()) {
                oldListName.append("er slettet, liste sat til ");
                oldListName.append(s_wordController.getLocalWords().get(s_wordController.getIndexLocale()).getTitle());
                refreshList();
            } else {
                oldListName.append("kunne ikke slettes");
                if (s_wordController.isLocal()) {
                    if (s_wordController.getIndexLocale() == 0) {
                        oldListName.append(", da det er en indbygget liste.");
                    } else {
                        oldListName.append('.');
                    }
                } else {
                    oldListName.append(", da det er en firebase liste.");
                }
            }
            WindowLayout.showSnack(oldListName, stickyList, false);
            returnValue = true;
        } else if (id == R.id.action_word_list_update) {
            updateCurrentList();
            returnValue = true;
        } else {
            returnValue = mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
        return returnValue;
    }

    private boolean updateCurrentList() {
        if (s_wordController.isLocal() && s_wordController.getIndexLocale() > 0) {
            WindowLayout.s_LoadToast.show();
            new WordListDownloader(this, s_wordController.getLocalWords().get(s_wordController.getIndexLocale()).getTitle(), s_wordController.getLocalWords().get(s_wordController.getIndexLocale()).getUrl()).execute();
            return true;
        }
        WindowLayout.showSnack("Denne liste kan ikke opdateres.", refreshLayout, true);
        return false;
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

    /**
     * Updates the lists, both the shown words and the drawer layouts..
     */
    public void refreshList() {
        adapterLocal = new WordTitleLocalAdapter(getContext(), R.layout.word_list_nav_drawer_list, s_wordController.getLocalWords());
        adapterRemote = new WordTitleRemoteAdapter(getContext(), R.layout.word_list_nav_drawer_list, WordListController.getKeyList());
        adapterLocal.notifyDataSetChanged();
        adapterRemote.notifyDataSetChanged();
        toolbar.setSubtitle("Antal lister : " + s_wordController.getListCount());
        if (!s_wordController.getCurrentList().isEmpty()) {
            mAdapter.restore(s_wordController.getCurrentList());
        } else {
            mAdapter.clear();
            WindowLayout.showSnack("Listen er tom fætter.", refreshLayout, false);
        }
        Log.d("Refresh", s_wordController.getCurrentList().toString());
    }

    /**
     * Handles the functionality when the user has pressed accept for adding a custom list.
     *
     * @param title
     *         The title of the list
     * @param url
     *         The URL of the list (guarenteed VALID web address!
     */
    private void onFinishAddWordDialog(final String title, final String url) {
        /* the recieved input from the dual-edittext dialog fragment */
        /* this function is only triggered if the user input was valid */

        Log.d("AddListFinished", "Title : " + title);
        Log.d("AddListFinished", "URL   : " + url);

        WindowLayout.s_LoadToast.show();

        new WordListDownloader(this, title, url).execute();
    }

    @Override
    public void onInternetStatusChanged(final int connectionState) {
        GameUtility.setConnectionStatus(connectionState);
    }

    @Override
    public void onInternetStatusChanged(final String connectionState) {
        GameUtility.setConnectionStatusName(connectionState);
        if (stickyList != null) {
            WindowLayout.showSnack(connectionState + " forbindelse oprettet.", stickyList, true);
        }
    }

    /* ************************************************************************ */
    /* ************************************************************************ */
    /* *********************** Helper Classes ********************************* */
    /* ************************************************************************ */
    /* ************************************************************************ */

    private class RefreshStopper implements Runnable {
        @Override
        public void run() {
            refreshLayout.setRefreshing(false);
        }
    }

    private static class SwipeOnRefreshListener extends WeakReferenceHolder<WordListFragment> implements SwipeRefreshLayout.OnRefreshListener {

        public SwipeOnRefreshListener(final WordListFragment wordListFragment) {
            super(wordListFragment);
        }

        @Override
        public void onRefresh() {
            final WordListFragment wordListFragment = weakReference.get();
            if (wordListFragment != null) {
                if (!wordListFragment.updateCurrentList()) {
                    wordListFragment.handler.postDelayed(wordListFragment.refreshStopper, 500);
                } else {
                    wordListFragment.handler.post(wordListFragment.refreshStopper);
                }
            }
        }
    }

    /**
     * Listener for adding list dialog !
     */
    private static class OnAddWordResponseCallback extends WeakReferenceHolder<WordListFragment> implements MaterialDialog.SingleButtonCallback {

        public OnAddWordResponseCallback(final WordListFragment wordListFragment) {
            super(wordListFragment);
        }

        private static boolean isValid(@NonNull final String title, @NonNull final String url) {
            return !title.isEmpty() && !url.isEmpty() && Pattern.compile(StringHelper.VALID_URL).matcher(url).find();
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final WordListFragment wordListFragment = weakReference.get();
            if (wordListFragment != null) {
                if (which == DialogAction.POSITIVE) {
                    /* let's inflate the dialog view... */
                    final View dialogCustomView = dialog.getCustomView();
                    if (dialogCustomView != null) {
                        final EditText editTextTitle = (EditText) dialogCustomView.findViewById(R.id.dialog_add_word_list_edit_title);
                        final EditText editTextURL = (EditText) dialogCustomView.findViewById(R.id.dialog_add_word_list_edit_url);
                        final String title = editTextTitle.getText().toString().trim();
                        final String url = editTextURL.getText().toString().trim();
                        Log.d("DL DIAG", title);
                        Log.d("DL DIAG", url);
                        Log.d("DL DIAG", String.valueOf(isValid(title, url)));

                        if (isValid(title, url)) {
                            wordListFragment.onFinishAddWordDialog(title, url);
                        } else {
                            WindowLayout.showSnack("Forkert indtastede informationer", wordListFragment.stickyList, true);
//                            Toast.makeText(wordListFragment, "Forkert indtastede informationer.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                dialog.dismiss();
//                wordListFragment.onWindowFocusChanged(true);
            }
        }
    }

    private static class AddWordListDialogListener implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<WordListFragment> wordListActivityWeakReference;

        public AddWordListDialogListener(final WordListFragment wordListActivity) {
            wordListActivityWeakReference = new WeakReference<>(wordListActivity);
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final WordListFragment wordListFragment = wordListActivityWeakReference.get();
            if (wordListFragment != null) {
                if (which == DialogAction.POSITIVE) {
                    wordListFragment.md = new MaterialDialog.Builder(wordListFragment.getContext())
                            .customView(R.layout.wordlist_add, false)
                            .positiveText("Ok")
                            .negativeText("Afbryd")
                            .onAny(new OnAddWordResponseCallback(wordListFragment))
                            .title("Tilføj Ordliste")
                            .show();
                }
            }
        }
    }

    private class ListLocalTitleClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            Log.d(TAG, "Local : " + position);
            if (!s_wordController.isLocal() || s_wordController.getIndexLocale() != position) {
                s_wordController.setIndexLocale(position);
                s_wordController.setIsLocal(true);
                WindowLayout.showSnack("Ordliste skiftet til '" + s_wordController.getLocalWords().get(position).getTitle() + "'", stickyList, true);
                refreshList();
            } else {
                WindowLayout.showSnack("'" + s_wordController.getLocalWords().get(position).getTitle() + "' er allerede din aktive ordliste.", stickyList, true);
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private class ListRemoteTitleClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            Log.d(TAG, "Remote : " + view.getTag());
            final String tag = (String) view.getTag();
            if (s_wordController.isLocal() || !Objects.equals(tag, s_wordController.getIndexRemote())) {
                s_wordController.setIndexRemote((String) view.getTag());
                s_wordController.setIsLocal(false);
                s_wordController.setIndexLocale(-1);
                WindowLayout.showSnack("Ordliste skiftet til '" + tag + "'", stickyList, true);
                refreshList();
            } else {
                WindowLayout.showSnack("'" + tag + "' er allerede din aktive ordliste.", stickyList, true);
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Basic class for handling the special case of the navigation drawer.
     */
//    private static class BackPressClick extends WeakReferenceHolder<WordListFragment> implements View.OnKeyListener {
//
//        private final int newMode;
//
//        public BackPressClick(final WordListFragment wordListFragment, final int newMode) {
//            super(wordListFragment);
//            this.newMode = newMode;
//        }
//
//        @Override
//        public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
//            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                final WordListFragment wordListFragment = weakReference.get();
//                if (wordListFragment != null) {
//                    if (wordListFragment.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//                        wordListFragment.mDrawerLayout.closeDrawer(GravityCompat.START);
//                        return true;
//                    } else {
//                        iFragmentFlipper.flipFragment(newMode);
//                        return true;
//                    }
//                } else if (iFragmentFlipper != null) {
//                    iFragmentFlipper.flipFragment(newMode);
//                }
//            }
//            return false;
//        }
//    }


}
