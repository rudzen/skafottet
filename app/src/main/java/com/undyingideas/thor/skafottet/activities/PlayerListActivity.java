package com.undyingideas.thor.skafottet.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.fragments.PlayerDetailFragment;
import com.undyingideas.thor.skafottet.interfaces.ProgressBarInterface;
import com.undyingideas.thor.skafottet.support.highscore.online.HighScoreContent;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.util.List;

/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * @author adam
 * @author rudz
 */
public class PlayerListActivity extends AppCompatActivity implements Runnable, ProgressBarInterface {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private View recyclerView;
    private ProgressBar topProgressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

        GameUtility.mpc.setRunnable(this);
        GameUtility.mpc.pc.createPlayer("Thor", "Kuss3");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        if (getSupportActionBar() != null) getSupportActionBar().setHomeButtonEnabled(true);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloaterClickHandler());

        topProgressBar = (ProgressBar) findViewById(R.id.topProgressBar);
        topProgressBar.setVisibility(View.INVISIBLE);

        recyclerView = findViewById(R.id.player_list);
        assert recyclerView != null;
        //Uncomment here if needed.
        //setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.player_detail_container) != null) {
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView, final List<HighScoreContent.HighScoreItem> items) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
    }

    //This method is called on update
    @Override
    public void run() {
        setProgressBar(true);
        setupRecyclerView((RecyclerView) recyclerView, GameUtility.mpc.getHighScoreItems());
        setProgressBar(false);
    }

    @Override
    public void setProgressBar(final boolean visible) {
        topProgressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    private static class FloaterClickHandler implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            WindowLayout.showSnack("Replace with your own action", view, false);
        }
    }

    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<HighScoreContent.HighScoreItem> mValues;

        public SimpleItemRecyclerViewAdapter(final List<HighScoreContent.HighScoreItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_list_content, parent, false);
            //noinspection ReturnOfInnerClass
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            holder.mView.setOnClickListener(new MyOnClickListener(holder));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public HighScoreContent.HighScoreItem mItem;

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
                    final Bundle arguments = new Bundle();
                    arguments.putString(PlayerDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    final PlayerDetailFragment fragment = new PlayerDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction().replace(R.id.player_detail_container, fragment).commit();
                } else {
                    final Context context = v.getContext();
                    final Intent intent = new Intent(context, PlayerDetailActivity.class);
                    intent.putExtra(PlayerDetailFragment.ARG_ITEM_ID, holder.mItem.id);
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
