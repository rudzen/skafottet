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
import com.undyingideas.thor.skafottet.interfaces.ProgressBarInterface;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.DTO.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * This Fragment is responsible for showing the current Multiplayer players that are online.<br>
 * It presents a list of them using a custom adapter with data retrieved from firebase.<br>
 * @author rudz
 */
public class LobbySelectorFragment extends Fragment {
    private static final String KEY_IS_ONLINE = "o";
    private static final String KEY_LAST_PLAYER_LIST = "lpl";
    private boolean isOffline = true;
    private ListView listView;

    private final ArrayList<LobbyDTO> lobbys = new ArrayList<>();
    private MultiplayerLobbyAdapter lobbyAdapter;
    private Runnable updater;

    @Nullable
    private OnMultiPlayerPlayerFragmentInteractionListener mListener;
    @Nullable
    private ProgressBarInterface setProgressListener;

    /**
     * Use this method to create a new instance of * this fragment using the provided parameters.
     * @param isOnline Defines if the application has access to the internet.
     * @return A new instance of fragment LobbySelectorFragment.
     */
    public static LobbySelectorFragment newInstance(final boolean isOnline) {
        final LobbySelectorFragment fragment = new LobbySelectorFragment();
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
            WindowLayout.showSnack("Kunne ikke få fat i en spilliste. Slå internet forbindelsen til og prøv igen.", listView, false);
        } else {
            // TODO MAYBE
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
        void startNewMultiplayerGame(final String lobbyKey, final String theWord);
    }

    private void onButtonPressed(final int position) {
        if (mListener != null) mListener.onPlayerClicked(lobbys.get(position).getPlayerList().get(0).getName());
    }

    private void configureAdapter() {
        lobbys.clear();

        removeInactive(GameUtility.mpc.name);
        lobbyAdapter = new MultiplayerLobbyAdapter(GameUtility.mpc.name, getContext(), R.layout.multiplayer_player_list_row, lobbys);
        listView.setAdapter(lobbyAdapter);
        listView.setOnItemClickListener(new OnLobbyClick(this));
        lobbyAdapter.notifyDataSetChanged();
    }

    private void updateList() {
        if (updater == null)
            updater = new UpdateList();

        configureAdapter();

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

    private static class OnLobbyClick implements AdapterView.OnItemClickListener {

        private final WeakReference<LobbySelectorFragment> lobbySelectorFragmentWeakReference;

        public OnLobbyClick(final LobbySelectorFragment lobbySelectorFragment) {
            lobbySelectorFragmentWeakReference = new WeakReference<>(lobbySelectorFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final LobbySelectorFragment lobbySelectorFragment =  lobbySelectorFragmentWeakReference.get();
            if (lobbySelectorFragment != null) {
                Log.d("NG", String.valueOf(id));
                // do stuff!!!
                lobbySelectorFragment.onButtonPressed(position);
                if(GameUtility.mpc.name == null)
                    Log.e("lobbyselectorfrag", "error : name == null");
                else {
                    for (final LobbyPlayerStatus lobbyPlayerStatus : lobbySelectorFragment.lobbys.get(position).getPlayerList())
                        if (lobbyPlayerStatus.getName().equals(GameUtility.mpc.name)){

                                if (lobbyPlayerStatus.getScore() == -1) {
                                    Log.d("firebaseopengame", lobbySelectorFragment.lobbys.get(position).getWord());
                                    //noinspection ConstantConditions
                                    String k = "";
                                    LobbyDTO dto = lobbySelectorFragment.lobbys.get(position);
                                    Set<String> keys = GameUtility.mpc.lc.lobbyList.keySet();
                                    for (String key : keys)
                                        if (dto.equals(GameUtility.mpc.lc.lobbyList.get(key)))
                                            { k = key; break; }

                                    GameUtility.mpc.setRunnable(null);
                                    lobbySelectorFragment.mListener.startNewMultiplayerGame(k, dto.getWord());
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
                // TODO not logged in
            } else {
                lobbys.clear();

                removeInactive(GameUtility.mpc.name);
                lobbyAdapter = new MultiplayerLobbyAdapter(GameUtility.mpc.name, getContext(), R.layout.multiplayer_player_list_row, lobbys);
                listView.setAdapter(lobbyAdapter);
                lobbyAdapter.notifyDataSetChanged();
            }
            setProgressListener.setProgressBar(false);
        }
    }

    private void removeInactive(String name) {
        ArrayList<LobbyDTO> dtoList = new ArrayList<>();
        dtoList.addAll(GameUtility.mpc.lc.lobbyList.values());
        boolean b;
        for(LobbyDTO dto : dtoList) {
            b = false;
            for(LobbyPlayerStatus status : dto.getPlayerList()){
                if (status.getScore() == -1 && status.getName().equals(name))
                {b = true; break; }
            }
            if(b) lobbys.add(dto);
        }

    }

}
