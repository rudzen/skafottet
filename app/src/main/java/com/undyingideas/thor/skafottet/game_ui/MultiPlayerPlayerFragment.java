package com.undyingideas.thor.skafottet.game_ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.undyingideas.thor.skafottet.R;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.firebase.DTO.WordStatus;
import com.undyingideas.thor.skafottet.firebase.controller.MultiplayerController;
import com.undyingideas.thor.skafottet.multiplayer.MultiplayerLobbyAdapter;
import com.undyingideas.thor.skafottet.multiplayer.MultiplayerPlayersAdapter;
import com.undyingideas.thor.skafottet.utility.GameUtility;
import com.undyingideas.thor.skafottet.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * This Fragment is responsible for showing the current Multiplayer players that are online.<br>
 * It presents a list of them using a custom adapter with data retrieved from firebase.<br>
 * @author rudz
 */
public class MultiPlayerPlayerFragment extends Fragment {
    private static final String KEY_IS_ONLINE = "o";
    private static final String KEY_LAST_PLAYER_LIST = "lpl";

    private boolean isOffline = true;

    private ListView listView;
    private ArrayList<PlayerDTO> players;
    private final ArrayList<LobbyDTO> lobbys = new ArrayList<>();
//    private MultiplayerController multiplayerController;
    private MultiplayerPlayersAdapter playerAdapter;
    private MultiplayerLobbyAdapter lobbyAdapter;
    private Runnable updater;

    @Nullable
    private OnMultiPlayerPlayerFragmentInteractionListener mListener;
    private ProgressBarInterface setProgressListener;

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
            isOffline = !getArguments().getBoolean(KEY_IS_ONLINE);
        }
        if (isOffline) {
            // read the last list used...
            try {
                final ArrayList<PlayerDTO> ply = (ArrayList<PlayerDTO>) GameUtility.s_prefereces.getObject(KEY_LAST_PLAYER_LIST, ArrayList.class);
                players = new ArrayList<>();
                players.addAll(ply);
            } catch (final Exception e) {
                WindowLayout.showSnack("Kunne ikke få fat i en spillerliste. Slå internet forbindelsen til og prøv igen.", listView, false);
            }
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_multi_player_player, container, false);

        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setOnClickListener(new FloatListener());

        listView = (ListView) root.findViewById(R.id.multiplayer_player_list);

        updateList();

        configureAdapter();

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
        if (context instanceof ProgressBarInterface) {
            setProgressListener = (ProgressBarInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ProgressBarInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        setProgressListener = null;
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
        void startNewMultiplayerGame(final String opponentName, final String theWord);
    }

    private void onButtonPressed(final int position) {
        if (mListener != null) mListener.onPlayerClicked(players.get(position).getName());
    }

    private void configureAdapter() {
        playerAdapter = new MultiplayerPlayersAdapter(getContext(), R.layout.multiplayer_player_list_row, players);
        listView.setAdapter(playerAdapter);
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

//        Firebase.setAndroidContext(getActivity());
//        multiplayerController = new MultiplayerController(new Firebase("https://hangmandtu.firebaseio.com"), updater);

        setProgressListener.setProgressBar(true);
        GameUtility.mpc.setRunnable(updater);
    }

    public void setOnlineStatus(final boolean newStatus) {
        if (isOffline && newStatus) {
            // we got connection!
            isOffline = false;
            updateList();
        } else {
            isOffline = !newStatus;
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
                Log.d("firebaselogin", "login");
                if(GameUtility.mpc.name == null)
                    GameUtility.mpc.login(multiPlayerPlayerFragment.players.get(position).getName());
                else {
                    for (final LobbyPlayerStatus lobbyPlayerStatus : multiPlayerPlayerFragment.lobbys.get(position).getPlayerList())
                        if (lobbyPlayerStatus.getName().equals(GameUtility.mpc.name))
                            for(final WordStatus wordStatus : lobbyPlayerStatus.getWordList()) {
                                if (wordStatus.getScore() == -1) {
                                    Log.d("firebaseopengame", wordStatus.getWordID());
                                    //noinspection ConstantConditions
                                    multiPlayerPlayerFragment.mListener.startNewMultiplayerGame("ib", wordStatus.getWordID());
                                    //TODO
                                    return;
                                }
                            }
                }
            }
        }
    }

    private static class FloatListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            WindowLayout.showSnack("Her kan der ske noget interesant! (manuel aktivering af liste opdatering?)", view, true);
        }
    }

    private class UpdateList implements Runnable {
        @Override
        public void run() {
            if (GameUtility.mpc.name == null) {
                playerAdapter = new MultiplayerPlayersAdapter(getContext(), R.layout.multiplayer_player_list_row, players);
                listView.setAdapter(playerAdapter);
                players.clear();
                players.addAll(GameUtility.mpc.pc.playerList.values());
                playerAdapter.notifyDataSetChanged();
                if (players != null) {
                    GameUtility.s_prefereces.putObject(KEY_LAST_PLAYER_LIST, players);
                }
            } else {
                lobbys.clear();
                lobbys.addAll(GameUtility.mpc.lc.lobbyList.values());
                Log.d("firebase", lobbys.size() + "  " + GameUtility.mpc.lc.lobbyList.size());
                lobbyAdapter = new MultiplayerLobbyAdapter(GameUtility.mpc.name, getContext(), R.layout.multiplayer_player_list_row, lobbys);
                listView.setAdapter(lobbyAdapter);
                lobbyAdapter.notifyDataSetChanged();
            }
            setProgressListener.setProgressBar(false);
        }
    }

}
