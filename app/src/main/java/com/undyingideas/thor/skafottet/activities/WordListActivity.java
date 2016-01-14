package com.undyingideas.thor.skafottet.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.adapters.WordListAdapter;
import com.undyingideas.thor.skafottet.adapters.WordTitleLocalAdapter;
import com.undyingideas.thor.skafottet.adapters.WordTitleRemoteAdapter;
import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListController;
import com.undyingideas.thor.skafottet.support.utility.StringHelper;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.utility.WordListDownloader;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.lang.ref.WeakReference;
import java.util.regex.Pattern;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import static com.undyingideas.thor.skafottet.support.utility.GameUtility.s_wordController;

/**
 * Created on 12-01-2016, 07:24.
 * Project : skafottet
 * This activity is responsible for a couple of things.
 * First off, the lists can be viewed.
 * @author rudz
 */
public class WordListActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener {

    private static final String TAG = "WordListActicity";

    private WordListAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;

    private Toolbar toolbar;

    private MaterialDialog md; // for add list

    private ListView listRemote, listLocal;
    private WordTitleLocalAdapter adapterLocal;
    private WordTitleRemoteAdapter adapterRemote;

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_word_list);

        /* long arsed onCreate, but unfortunatly it's the min. req code */

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.word_list_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeOnRefreshListener());

        mAdapter = new WordListAdapter(this, s_wordController.getCurrentList());

        stickyList = (StickyListHeadersListView) findViewById(R.id.list);
        stickyList.setOnItemClickListener(this);
        stickyList.setOnHeaderClickListener(this);
        stickyList.setOnStickyHeaderChangedListener(this);
        stickyList.setOnStickyHeaderOffsetChangedListener(this);
        stickyList.addHeaderView(getLayoutInflater().inflate(R.layout.word_list_header, null));
        stickyList.addFooterView(getLayoutInflater().inflate(R.layout.word_list_footer, null));
        stickyList.setEmptyView(findViewById(R.id.word_list_empty));
        stickyList.setDrawingListUnderStickyHeader(true);
        stickyList.setAreHeadersSticky(true);
        stickyList.setAdapter(mAdapter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.word_list_title));
        toolbar.setSubtitle("Antal Lister : " + s_wordController.getListCount()); // "<nuværende liste>"); // vil blive sat dynamisk ved klik på liste og ved opstart!
        toolbar.setCollapsible(false);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setLogoDescription("Applikations logo");
        toolbar.setNavigationContentDescription("Home icon");

        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.word_item_drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.word_list_drawer_open, R.string.word_list_drawer_close);

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        /* configure the side bar lists */
        listRemote = (ListView) mDrawerLayout.findViewById(R.id.nav_drawer_remote_lists);
        listLocal = (ListView) mDrawerLayout.findViewById(R.id.nav_drawer_local_lists);

        /* configure the adapters for the side bar lists */
        adapterLocal = new WordTitleLocalAdapter(this, R.layout.word_list_nav_drawer_list, s_wordController.getLocalWords());
        adapterRemote = new WordTitleRemoteAdapter(this, R.layout.word_list_nav_drawer_list, WordListController.getKeyList());

        listRemote.setAdapter(adapterRemote);
        listLocal.setAdapter(adapterLocal);

        listRemote.setOnItemClickListener(new ListRemoteTitleClickListener());
        listLocal.setOnItemClickListener(new ListLocalTitleClickListener());

        stickyList.setStickyHeaderTopOffset(0);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_word_list_add) {
                        /* show Yes/No dialog here! */
            new MaterialDialog.Builder(this)
                    .content("Vil du tilføje en ordliste?")
                    .cancelable(true)
                    .onAny(new AddWordListDialogListener(this))
                    .positiveText(R.string.dialog_yes)
                    .negativeText(R.string.dialog_no)
                    .title("Tilføj Ordliste")
                    .show()
            ;
            return true;
        } else {
            return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
//        Toast.makeText(this, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(final StickyListHeadersListView l, final View header, final int itemPosition, final long headerId, final boolean currentlySticky) {
//        Toast.makeText(this, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderOffsetChanged(final StickyListHeadersListView l, final View header, final int offset) {
        //noinspection PointlessBooleanExpression,ConstantConditions
//        if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        header.setAlpha(1 - offset / (float) header.getMeasuredHeight());
//        }
    }

    @Override
    public void onStickyHeaderChanged(final StickyListHeadersListView l, final View header, final int itemPosition, final long headerId) {
        header.setAlpha(1);
    }

    public void refreshList() {
        mAdapter.restore(s_wordController.getCurrentList());
    }

    private class RefreshStopper implements Runnable {
        @Override
        public void run() {
            refreshLayout.setRefreshing(false);
        }
    }

    private class SwipeOnRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            new Handler().postDelayed(new RefreshStopper(), 1000);
        }
    }

    /**
     * Listener for adding list dialog !
     */
    private static class OnAddWordResponseCallback implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<WordListActivity> wordListActivityWeakReference;

        public OnAddWordResponseCallback(final WordListActivity wordListActivity) {
            wordListActivityWeakReference = new WeakReference<>(wordListActivity);
        }

        private static boolean isValid(final Context context, @NonNull final String title, @NonNull final String url) {
            final boolean validStuff = !title.isEmpty() && !url.isEmpty() && Pattern.compile(StringHelper.VALID_URL).matcher(url).find();
            if (validStuff) return true;
            Toast.makeText(context, "Forkert indtastede informationer.", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final WordListActivity wordListActivity = wordListActivityWeakReference.get();
            if (wordListActivity != null) {
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
                        Log.d("DL DIAG", String.valueOf(isValid(wordListActivity, title, url)));

                        if (isValid(wordListActivity, title, url)) {
                            final CheckBox downloadNow = (CheckBox) dialogCustomView.findViewById(R.id.dialog_add_word_list_chk_download);
                            wordListActivity.onFinishAddWordListDialog(title, url, downloadNow.isChecked());
                        }
                    }
                }
                dialog.dismiss();
                wordListActivity.onWindowFocusChanged(true);
            }
        }
    }

    private static class AddWordListDialogListener implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<WordListActivity> wordListActivityWeakReference;

        public AddWordListDialogListener(final WordListActivity wordListActivity) {
            wordListActivityWeakReference = new WeakReference<>(wordListActivity);
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final WordListActivity wordListActivity = wordListActivityWeakReference.get();
            if (wordListActivity != null) {
                if (which == DialogAction.POSITIVE) {
                    wordListActivity.md = new MaterialDialog.Builder(wordListActivity)
                            .customView(R.layout.dialog_add_wordlist, false)
                            .positiveText("Ok")
                            .negativeText("Afbryd")
                            .onAny(new OnAddWordResponseCallback(wordListActivity))
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
//            Toast.makeText(getApplicationContext(), "Local List title clicked : " + position + " (" + s_wordController.getLocalWords().get(position).getTitle() , Toast.LENGTH_SHORT).show();
            s_wordController.setCurrentLocalList(position);
            s_wordController.setIsLocal(true);
            refreshList();
        }
    }

    private class ListRemoteTitleClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            Log.d(TAG, "Remote : " + view.getTag());
//            Toast.makeText(getApplicationContext(), "List title clicked : " + view.getTag(), Toast.LENGTH_SHORT).show();
            s_wordController.setIndexRemote((String) view.getTag());
            s_wordController.setIsLocal(false);
            refreshList();
        }
    }

    private void onFinishAddWordListDialog(final String title, final String url, final boolean startDownload) {
        /* the recieved input from the dual-edittext dialog fragment */
        /* this function is only triggered if the user input was valid */

        if (md != null && md.isShowing()) md.dismiss();
        onWindowFocusChanged(true);

        Log.d("AddListFinished", "Title : " + title);
        Log.d("AddListFinished", "URL   : " + url);
        Log.d("AddListFinished", "Title : " + startDownload);

        // this is where the list is initiated to be downloaded...
        final WordItem wordItem = new WordItem(title, url, startDownload);

        if (startDownload) {
            new WordListDownloader(this, wordItem).execute();
        } else {
            s_wordController.addLocalWordList(title, url);
        }
    }
}
