package com.undyingideas.thor.skafottet.fragments;

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
import com.undyingideas.thor.skafottet.adapters.MultiplayerLobbyAdapter;
import com.undyingideas.thor.skafottet.adapters.MultiplayerPlayersAdapter;
import com.undyingideas.thor.skafottet.adapters.WordListAdapter;
import com.undyingideas.thor.skafottet.adapters.WordTitleListAdapter;
import com.undyingideas.thor.skafottet.interfaces.ProgressBarInterface;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.PlayerDTO;
import com.undyingideas.thor.skafottet.support.firebase.WordList.WordListDTO;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
import com.undyingideas.thor.skafottet.support.wordlist.WordItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Theis' on 12-01-2016.
 */
public class CreateLobbyFragment extends Fragment {
    private static final String KEY_IS_ONLINE = "o";
    private static final String KEY_LAST_PLAYER_LIST = "lpl";
    private static String opponentName;

    private boolean isOffline = true;

    private ListView listView;
    private final ArrayList<PlayerDTO> players = new ArrayList<>();
    private final ArrayList<WordItem> wordList = new ArrayList<>();
    private MultiplayerPlayersAdapter playerAdapter;
    private WordTitleListAdapter wordTitleAdapter;
    private Runnable updater;

    @Nullable
    private OnCreateLobbyFragmentInteractionListener mListener;
    @Nullable
    private ProgressBarInterface setProgressListener;

    public static CreateLobbyFragment newInstance(final boolean isOnline) {
        final CreateLobbyFragment fragment = new CreateLobbyFragment();
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
                players.clear();
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
        if (context instanceof OnCreateLobbyFragmentInteractionListener) {
            mListener = (OnCreateLobbyFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnCreateLobbyFragmentInteractionListener");
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
    public interface OnCreateLobbyFragmentInteractionListener {
        void onPlayerClicked(final String playerName);
        void startNewMultiplayerGame(final String opponentName, final String theWord);
    }

    private void onButtonPressed(final int position) {
        if (mListener != null) mListener.onPlayerClicked(players.get(position).getName());
    }

    private void configureAdapter() {
        playerAdapter = new MultiplayerPlayersAdapter(getContext(), R.layout.multiplayer_player_list_row, players);
        listView.setAdapter(playerAdapter);
        listView.setOnItemClickListener(new OnCreateLobbyClick(this));
    }

    private void updateList() {
        players.clear();
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

    private static class OnCreateLobbyClick implements AdapterView.OnItemClickListener {

        private final WeakReference<CreateLobbyFragment> createLobbyFragmentWeakReference;

        public OnCreateLobbyClick(final CreateLobbyFragment createLobbyFragment) {
            createLobbyFragmentWeakReference = new WeakReference<>(createLobbyFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final CreateLobbyFragment createLobbyFragment =  createLobbyFragmentWeakReference.get();
            if (createLobbyFragment != null) {
                Log.d("NG", String.valueOf(id));
                // do stuff!!!
                createLobbyFragment.onButtonPressed(position);
                if(GameUtility.mpc.name == null) {
                    //GameUtility.mpc.login(multiPlayerPlayerFragment.players.get(position).getName());
                } else if (opponentName != null) {
                    Log.d("createlobbyfragment", "wordlist size = " + createLobbyFragment.wordList.get(position).getWords().size());
                } else {
                    opponentName = createLobbyFragment.players.get(position).getName();
                    GameUtility.mpc.update();
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
            wordList.clear();
            wordList.addAll(GameUtility.mpc.wlc.wordList.values());
            Log.d("UpdateList", wordList.toString() + "  " +wordList.size());
            players.clear();
            players.addAll(GameUtility.mpc.pc.playerList.values());
            if (GameUtility.mpc.name == null) {
                // ups not logged in
                Log.e("CreateLobbyFragment", "not logged in error");
            } else if (opponentName != null) {
                wordTitleAdapter = new WordTitleListAdapter(getContext(), R.layout.word_list_nav_drawer_list, wordList);
                listView.setAdapter(wordTitleAdapter);
                wordTitleAdapter.notifyDataSetChanged();
            } else {
                playerAdapter = new MultiplayerPlayersAdapter(getContext(), R.layout.multiplayer_player_list_row, players);
                listView.setAdapter(playerAdapter);

                players.remove(GameUtility.mpc.pc.playerList.get(GameUtility.mpc.name)); // remove logged in player TODO rewrite
                playerAdapter.notifyDataSetChanged();
                GameUtility.s_prefereces.putObject(KEY_LAST_PLAYER_LIST, players);
            }
            setProgressListener.setProgressBar(false);
        }
    }
}
