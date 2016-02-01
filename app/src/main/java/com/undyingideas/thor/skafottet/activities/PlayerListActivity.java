package com.undyingideas.thor.skafottet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.firebase.players.PlayerListener;
import com.undyingideas.thor.skafottet.firebase.players.PlayerListenerSlave;
import com.undyingideas.thor.skafottet.fragments.PlayerDetailFragment;
import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.util.List;

/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 *
 * @author adam
 * @author rudz
 */
public class PlayerListActivity extends AppCompatActivity implements PlayerListenerSlave.PlayerListenerReceiver {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private RecyclerView mRecyclerView;

    private PlayerListener mPlayerListener;
    private PlayerListenerSlave mPlayerListenerSlave;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if (getSupportActionBar() != null) {
            toolbar.setSubtitle("Hvem er størst?");
            toolbar.setCollapsible(false);
            toolbar.setLogo(R.mipmap.ic_launcher);
            toolbar.setLogoDescription("Applikations logo");
            toolbar.setNavigationContentDescription("Home icon");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloaterClickHandler());

        mRecyclerView = (RecyclerView) findViewById(R.id.player_list);
        assert mRecyclerView != null;
        //Uncomment here if needed.
        //setupRecyclerView((RecyclerView) mRecyclerView);

        mPlayerListener = new PlayerListener();
        mPlayerListenerSlave = new PlayerListenerSlave(this);
        mPlayerListener.addSlave(mPlayerListenerSlave);

        if (findViewById(R.id.player_detail_container) != null) {
            mTwoPane = true;
        }
    }

    @Override
    protected void onPostCreate(final Bundle savedInstanceState) {
        if (getWindow() != null) {
            WindowLayout.hideStatusBar(getWindow(), null);
        }
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        mPlayerListener.setListeners();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mPlayerListener.removeListeners();
        super.onStop();
    }

    @Override
    public void finish() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(new Intent(this, GameActivity.class));
        super.finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int index = item.getItemId();
        if (index == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // not used atm, but planned for future update.
        return super.onCreateOptionsMenu(menu);
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView, final List<PlayerDTO> items) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
    }

    @Override
    public void onPlayerDataReceived(final PlayerListenerSlave playerListenerReceiver) {
        setupRecyclerView(mRecyclerView, playerListenerReceiver.getHighScoreItems());
    }

    @Override
    public void onPlayerDataTransferAbort(final boolean aborted) {
        WindowLayout.showSnack("Spillerliste opdatering afbrudt.", mRecyclerView, false);
    }

    private static class FloaterClickHandler implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            WindowLayout.showSnack("Denne funktionalitet er ikke indlagt her endnu", view, false);
        }
    }

    private class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<PlayerDTO> mValues;

        public SimpleItemRecyclerViewAdapter(final List<PlayerDTO> items) { mValues = items; }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_list_content, parent, false);
            //noinspection ReturnOfInnerClass
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (mValues != null && !mValues.isEmpty()) {
                holder.mItem = mValues.get(position);
                holder.mIdView.setText(mValues.get(position).getName());
                holder.mContentView.setText(Integer.toString(mValues.get(position).getScore()));
                holder.mView.setOnClickListener(new MyOnClickListener(holder));
                Log.d("RecycleAdapter", mValues.get(position).toString());
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public PlayerDTO mItem;

            public ViewHolder(final View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        private class MyOnClickListener implements View.OnClickListener {
            private final ViewHolder holder;

            public MyOnClickListener(final ViewHolder holder) {
                this.holder = holder;
            }

            @Override
            public void onClick(final View v) {

                if (mTwoPane) {
                    final PlayerDetailFragment playerDetailFragment = PlayerDetailFragment.newInstance(holder.mItem);
                    getSupportFragmentManager().beginTransaction().replace(R.id.player_detail_container, playerDetailFragment).commit();

//                    final Bundle arguments = new Bundle();
//                    arguments.putString(PlayerDetailFragment.ARG_ITEM_ID, holder.mItem.getEmail());
//                    final PlayerDetailFragment fragment = new PlayerDetailFragment();
//                    fragment.setArguments(arguments);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.player_detail_container, fragment).commit();
                } else {
                    final Context context = v.getContext();
                    final Intent intent = new Intent(context, PlayerDetailActivity.class);

                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(PlayerDetailFragment.ARG_ITEM_ID, holder.mItem);
                    intent.putExtra(PlayerDetailFragment.ARG_ITEM_ID, bundle);
                    context.startActivity(intent);
                }
            }
        }
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            WindowLayout.setImmersiveMode(getWindow());
        }
    }
}
