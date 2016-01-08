package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.multiplayer.MultiplayerPlayersAdapter;
import com.undyingideas.thor.skafottet.utility.GameUtility;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This Fragment is responsible for showing the current Multiplayer players that are online.<br>
 * It presents a list of them using a custom adapter with data retrieved from the firebase db.<br>
 * @author rudz
 */
public class MultiPlayerPlayerFragment extends Fragment {
    private static final String KEY_IS_ONLINE = "o";
    private static final String KEY_LAST_PLAYER_LIST = "lpl";

    private boolean isOnline;

    private ListView listView;
    private ArrayList<PlayerDTO> players;
    private MultiplayerController multiplayerController;
    private MultiplayerPlayersAdapter adapter;
    private Runnable updater;

    @Nullable
    private OnMultiPlayerPlayerFragmentInteractionListener mListener;

    /**
     * Use this method to create a new instance of * this fragment using the provided parameters.
     * @param isOnline Defines if the application has access to the internet.
     * @return A new instance of fragment MultiPlayerPlayerFragment.
     */
    public static MultiPlayerPlayerFragment newInstance(final boolean isOnline) {
        final MultiPlayerPlayerFragment fragment = new MultiPlayerPlayerFragment();
        final Bundle args = new Bundle();
        args.putBoolean(KEY_IS_ONLINE, isOnline);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isOnline = getArguments().getBoolean(KEY_IS_ONLINE);
        }
        if (isOnline) {
            updateList();
        } else {
            // read the last list used...
            try {
                ArrayList<PlayerDTO> ply = (ArrayList<PlayerDTO>) GameUtility.s_prefereces.getObject(KEY_LAST_PLAYER_LIST, ArrayList.class);
                players = new ArrayList<>();
                players.addAll(ply);
            } catch (final Exception e) {
                onListFail();
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_multi_player_player, container, false);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new FloatListener());

        listView = (ListView) root.findViewById(R.id.multiplayer_player_list);

        if (!players.isEmpty()) configureAdapter();

        return root;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnMultiPlayerPlayerFragmentInteractionListener) {
            mListener = (OnMultiPlayerPlayerFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMultiPlayerPlayerFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMultiPlayerPlayerFragmentInteractionListener {
        void onPlayerClicked(final String playerName);
    }

    public void onButtonPressed(final int position) {
        if (mListener != null) mListener.onPlayerClicked(players.get(position).getName());
    }

    private void configureAdapter() {
        if (adapter == null) adapter = new MultiplayerPlayersAdapter(getActivity(), R.layout.multiplayer_player_list_row, players);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnMultiPlayerPlayerClick(this));
    }

    private void updateList() {
        if (players != null) {
            players.clear();
        } else {
            players = new ArrayList<>();
        }
        if (updater == null) updater = new UpdateList();

        configureAdapter();

        Firebase.setAndroidContext(getActivity());
        multiplayerController = new MultiplayerController(new Firebase("https://hangmandtu.firebaseio.com/Multiplayer"), updater);
    }

    private void onListFail() {
        Snackbar.make(listView, "Kunne ikke få fat i en spillerliste. Slå internet forbindelsen til og prøv igen.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void setOnlineStatus(final boolean newStatus) {
        if (!isOnline && newStatus) {
            // we got connection!
            isOnline = true;
            updateList();
        } else {
            isOnline = newStatus;
        }
    }

    private static class OnMultiPlayerPlayerClick implements AdapterView.OnItemClickListener {

        private final WeakReference<MultiPlayerPlayerFragment> multiPlayerPlayerFragmentWeakReference;

        public OnMultiPlayerPlayerClick(final MultiPlayerPlayerFragment multiPlayerPlayerFragment) {
            multiPlayerPlayerFragmentWeakReference = new WeakReference<>(multiPlayerPlayerFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final MultiPlayerPlayerFragment multiPlayerPlayerFragment =  multiPlayerPlayerFragmentWeakReference.get();
            if (multiPlayerPlayerFragment != null) {
                Log.d("NG", String.valueOf(id));
                // do stuff!!!
                multiPlayerPlayerFragment.onButtonPressed(position);
                Snackbar.make(view, multiPlayerPlayerFragment.players.get(position).getName(), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private static class FloatListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            Snackbar.make(view, "Her kan der ske noget interesant! (manuel aktivering af liste opdatering?)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    private class UpdateList implements Runnable {
        @Override
        public void run() {
            Log.d("firebase", "Updater in action");

            players.clear();
            players.addAll(multiplayerController.pc.playerList.values());

            Log.d("firebase", players.toString());

            adapter.notifyDataSetChanged();

            adapter = new MultiplayerPlayersAdapter(getActivity(), R.layout.multiplayer_player_list_row, players);
            listView.setAdapter(adapter);

            GameUtility.s_prefereces.putObject(KEY_LAST_PLAYER_LIST, players);

        }
    }

}
