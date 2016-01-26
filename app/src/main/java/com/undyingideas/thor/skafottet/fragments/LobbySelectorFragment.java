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
import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
import com.undyingideas.thor.skafottet.support.utility.GameUtility;
import com.undyingideas.thor.skafottet.support.utility.WindowLayout;

import java.util.ArrayList;
import java.util.Set;

//import com.undyingideas.thor.skafottet.support.abstractions.FragmentOnBackClickListener;

/**
 * This Fragment is responsible for showing the current Multiplayer players that are online.<br>
 * It presents a list of them using a custom adapter with data retrieved from firebase.<br>
 * TODO : Needs to facilitate a firebase observer interface *correctly* to monitor changes.
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
    private IFragmentFlipper iFragmentFlipper;

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnMultiPlayerPlayerFragmentInteractionListener && context instanceof IFragmentFlipper) {
            mListener = (OnMultiPlayerPlayerFragmentInteractionListener) context;
            iFragmentFlipper = (IFragmentFlipper) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnMultiPlayerPlayerFragmentInteractionListener & IFragmentFlipper.");
        }
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
        iFragmentFlipper = null;
    }

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

    /**
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
        if(lobbys.isEmpty()) WindowLayout.showSnack("Du har ingen udfordringer klar", listView, false);
    }

    private void updateList() {
        if (updater == null)
            updater = new UpdateList();

        configureAdapter();

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

    private static class OnLobbyClick extends WeakReferenceHolder<LobbySelectorFragment> implements AdapterView.OnItemClickListener {

        public OnLobbyClick(final LobbySelectorFragment lobbySelectorFragment) {
            super(lobbySelectorFragment);
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            final LobbySelectorFragment lobbySelectorFragment =  weakReference.get();
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
                                    final LobbyDTO dto = lobbySelectorFragment.lobbys.get(position);
                                    final Set<String> keys = GameUtility.mpc.lc.lobbyList.keySet();
                                    for (final String key : keys)
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
//            setProgressListener.setProgressBar(false);
        }
    }

    private void removeInactive(final String name) {
        final ArrayList<LobbyDTO> dtoList = new ArrayList<>();
        dtoList.addAll(GameUtility.mpc.lc.lobbyList.values());
        boolean b;
        for(final LobbyDTO dto : dtoList) {
            b = false;
            try {
                for (final LobbyPlayerStatus status : dto.getPlayerList()) {
                    if (status.getScore() == -1 && status.getName().equals(name)) {
                        b = true;
                        break;
                    }
                }
                if (b) lobbys.add(dto);
            } catch (final NullPointerException e) {
                Log.e("NullPointer", e.toString());
            }
        }

    }

}
