package com.undyingideas.thor.skafottet.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.adapters.WordListAdapter;
import com.undyingideas.thor.skafottet.fragments.dialogs.AddWordListDialog;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.lang.ref.WeakReference;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created on 12-01-2016, 07:24.
 * Project : skafottet
 *
 * @author rudz
 */
public class WordListActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
        StickyListHeadersListView.OnStickyHeaderChangedListener,
        AddWordListDialog.AddWordListListener
{

    private static boolean fadeHeader = true;

    private WordListAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private StickyListHeadersListView stickyList;
    private SwipeRefreshLayout refreshLayout;

    private Button restoreButton;
    private Button updateButton;
    private Button clearButton;

    private CheckBox stickyCheckBox;
    private CheckBox fadeCheckBox;
    private CheckBox drawBehindCheckBox;
    private CheckBox fastScrollCheckBox;
    private Button openExpandableListButton;

    private Toolbar toolbar;

    private View.OnClickListener buttonListener;
    private CompoundButton.OnCheckedChangeListener checkBoxListener;

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
//        FontUtils.setRobotoFont(this, getWindow().getDecorView());
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acticity_word_list);

        buttonListener = new ButtonClickListener();
        checkBoxListener = new CheckBoxOnCheckedChangeListener();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.word_list_refresh_layout);
        refreshLayout.setOnRefreshListener(new SwipeOnRefreshListener());

        mAdapter = new WordListAdapter(this);

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
        toolbar.setSubtitle("<nuværende liste>"); // vil blive sat dynamisk ved klik på liste og ved opstart!
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

        restoreButton = (Button) findViewById(R.id.restore_button);
        restoreButton.setOnClickListener(buttonListener);
        openExpandableListButton = (Button) findViewById(R.id.open_expandable_list_button);
        openExpandableListButton.setOnClickListener(buttonListener);
        updateButton = (Button) findViewById(R.id.update_button);
        updateButton.setOnClickListener(buttonListener);
        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(buttonListener);

        stickyCheckBox = (CheckBox) findViewById(R.id.sticky_checkBox);
        stickyCheckBox.setOnCheckedChangeListener(checkBoxListener);
        fadeCheckBox = (CheckBox) findViewById(R.id.fade_checkBox);
        fadeCheckBox.setOnCheckedChangeListener(checkBoxListener);
        drawBehindCheckBox = (CheckBox) findViewById(R.id.draw_behind_checkBox);
        drawBehindCheckBox.setOnCheckedChangeListener(checkBoxListener);
        fastScrollCheckBox = (CheckBox) findViewById(R.id.fast_scroll_checkBox);
        fastScrollCheckBox.setOnCheckedChangeListener(checkBoxListener);

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
    public boolean onOptionsItemSelected(final MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
        Toast.makeText(this, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(final StickyListHeadersListView l, final View header, final int itemPosition, final long headerId, final boolean currentlySticky) {
        Toast.makeText(this, "Header " + headerId + " currentlySticky ? " + currentlySticky, Toast.LENGTH_SHORT).show();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderOffsetChanged(final StickyListHeadersListView l, final View header, final int offset) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (fadeHeader && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            header.setAlpha(1 - offset / (float) header.getMeasuredHeight());
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onStickyHeaderChanged(final StickyListHeadersListView l, final View header, final int itemPosition, final long headerId) {
        header.setAlpha(1);
    }

    private class ButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            switch (view.getId()) {
                case R.id.restore_button:
                    mAdapter.restore();
                    break;
                case R.id.update_button:
                    mAdapter.notifyDataSetChanged();
                    break;
                case R.id.clear_button:
                    mAdapter.clear();
                    break;
                case R.id.open_expandable_list_button:
                    final Intent intent = new Intent(WordListActivity.this, MenuActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    private class CheckBoxOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.sticky_checkBox:
                    stickyList.setAreHeadersSticky(isChecked);
                    break;
                case R.id.fade_checkBox:
                    //noinspection AssignmentToStaticFieldFromInstanceMethod
                    fadeHeader = isChecked;
                    break;
                case R.id.draw_behind_checkBox:
                    stickyList.setDrawingListUnderStickyHeader(isChecked);
                    break;
                case R.id.fast_scroll_checkBox:
                    stickyList.setFastScrollEnabled(isChecked);
                    stickyList.setFastScrollAlwaysVisible(isChecked);
                    break;
            }
        }
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
    private static class OnYesNoResponseDialog implements MaterialDialog.SingleButtonCallback {

        private final WeakReference<WordListActivity> wordListActivityWeakReference;

        public OnYesNoResponseDialog(final WordListActivity wordListActivity) {
            wordListActivityWeakReference = new WeakReference<>(wordListActivity);
        }

        @Override
        public void onClick(@NonNull final MaterialDialog dialog, @NonNull final DialogAction which) {
            final WordListActivity wordListActivity = wordListActivityWeakReference.get();
            if (wordListActivity != null) {
                if (which == DialogAction.POSITIVE) {
                    final DialogFragment add = AddWordListDialog.newInstance("Indtast information", "Ok", "Afbryd", true);
                    add.show(wordListActivity.getSupportFragmentManager(), "ADDialog");
                }
            }
        }
    }

    @Override
    public void onFinishAddWordListDialog(final String title, final String url, final boolean startDownload) {
        /* the recieved input from the dual-edittext dialog fragment */
        /* this function is only triggered if the user input was valid */

        Log.d("AddListFinished", "Title : " + title);
        Log.d("AddListFinished", "URL   : " + url);
        Log.d("AddListFinished", "Title : " + startDownload);
        final Intent returnIntent = new Intent();
        returnIntent.setType("new_word_item");
        final Bundle bundle = new Bundle();
        bundle.putParcelable("result",new WordItem(title, url, startDownload));
        returnIntent.putExtra ("result", bundle);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//        /* TESTING */
//        final int id = item.getItemId();
//        if (id == android.R.id.home) {
//
//            return true;
//        } else if (id == R.id.action_word_list_add) {
//            /* show Yes/No dialog here! */
//            new MaterialDialog.Builder(this)
//                    .content("Vil du tilføje en ordliste?")
//                    .cancelable(true)
//                    .onAny(new OnYesNoResponseDialog(this))
//                    .positiveText(R.string.dialog_yes)
//                    .negativeText(R.string.dialog_no)
//                    .title("Tilføj Ordliste")
//                    .backgroundColor(Color.BLACK)
//                    .contentColor(getResources().getColor(R.color.colorAccent))
//                    .buttonRippleColor(getResources().getColor(R.color.colorPrimaryDark))
//                    .show()
//            ;
//            return true;
//        } else if (id == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}