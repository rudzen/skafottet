package com.undyingideas.thor.skafottet.game_ui.hichscorecontent;

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
import android.widget.TextView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Players. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link PlayerDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class PlayerListActivity extends AppCompatActivity implements Runnable {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
//    private Firebase myFirebaseRef;
//    private MultiplayerController mpc;
    private View recyclerView;

    private final ArrayList<PlayerDTO> players = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_list);

//        Firebase.setAndroidContext(this);
//        myFirebaseRef = new Firebase(Constant.HANGMANDTU_FIREBASEIO);

        GameUtility.mpc.setRunnable(this);
//        mpc = new MultiplayerController(myFirebaseRef, this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new FloaterClickHandler());

        recyclerView = findViewById(R.id.player_list);
        assert recyclerView != null;
        //Uncomment here if needed.
        //setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.player_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    private void setupRecyclerView(@NonNull final RecyclerView recyclerView, final List<HighScoreContent.HighScoreItem> items) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(items));
    }
    //This method is called on update
    @Override
    public void run() {

        setupRecyclerView((RecyclerView) recyclerView, GameUtility.mpc.getHighScoreItems());


    }

    private static class FloaterClickHandler implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            WindowLayout.showSnack("Replace with your own action", view, false);
        }
    }


    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<HighScoreContent.HighScoreItem> mValues;

        public SimpleItemRecyclerViewAdapter(final List<HighScoreContent.HighScoreItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.player_list_content, parent, false);
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
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.player_detail_container, fragment)
                            .commit();
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
