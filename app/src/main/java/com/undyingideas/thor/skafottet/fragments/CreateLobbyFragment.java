//package com.undyingideas.thor.skafottet.fragments;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.undyingideas.thor.skafottet.R;
//import com.undyingideas.thor.skafottet.adapters.MultiplayerPlayersAdapter;
//import com.undyingideas.thor.skafottet.adapters.WordTitleLocalAdapter;
//import com.undyingideas.thor.skafottet.interfaces.IFragmentFlipper;
//import com.undyingideas.thor.skafottet.support.abstractions.WeakReferenceHolder;
//import com.undyingideas.thor.skafottet.support.firebase.controller.WordListController;
//import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyDTO;
//import com.undyingideas.thor.skafottet.support.firebase.dto.LobbyPlayerStatus;
//import com.undyingideas.thor.skafottet.support.firebase.dto.PlayerDTO;
//import com.undyingideas.thor.skafottet.support.utility.Constant;
//import com.undyingideas.thor.skafottet.support.utility.GameUtility;
//import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
//import com.undyingideas.thor.skafottet.support.wordlist.WordItem;
//
//import java.util.ArrayList;
//
///**
// * Created on 12-01-2016, 06:39.
// * Project : skafottet
// * @author Theis'
// */
//public class CreateLobbyFragment extends Fragment {
//    private static final String KEY_IS_ONLINE = "o";
//    private static final String KEY_LAST_PLAYER_LIST = "lpl";
//    @Nullable
//    private static String opponentName;
//
//    private boolean isOffline = true;
//
//    private ListView listView;
//    private final ArrayList<PlayerDTO> players = new ArrayList<>();
//    private final ArrayList<WordItem> wordList = new ArrayList<>();
//    private MultiplayerPlayersAdapter playerAdapter;
//    private WordTitleLocalAdapter wordTitleAdapter;
//    private Runnable updater;
//
//    @Nullable
//    private OnCreateLobbyFragmentInteractionListener onCreateLobbyFragmentInteractionListener;
//    @Nullable
//    private IFragmentFlipper iFragmentFlipper;
//
//    @Override
//    public void onAttach(final Context context) {
//        super.onAttach(context);
//        if (context instanceof OnCreateLobbyFragmentInteractionListener && context instanceof IFragmentFlipper) {
//            onCreateLobbyFragmentInteractionListener = (OnCreateLobbyFragmentInteractionListener) context;
//            iFragmentFlipper = (IFragmentFlipper) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnCreateLobbyFragmentInteractionListener & IFragmentFlipper.");
//        }
//    }
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            isOffline = !getArguments().getBoolean(KEY_IS_ONLINE);
//        }
//        if (isOffline) {
//            // read the last list used...
//            try {
//                final ArrayList<PlayerDTO> ply = (ArrayList<PlayerDTO>) GameUtility.prefs.getObject(KEY_LAST_PLAYER_LIST, ArrayList.class);
//                players.clear();
//                players.addAll(ply);
//            } catch (final Exception e) {
//                WindowLayout.showSnack("Kunne ikke få fat i en spillerliste. Slå internet forbindelsen til og prøv igen.", listView, false);
//            }
//        }
//    }
//
//    @Override
//    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
//        final View root = inflater.inflate(R.layout.fragment_multi_player_player, container, false);
//
//        final FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
//        fab.setOnClickListener(new FloatListener());
//
//        listView = (ListView) root.findViewById(R.id.multiplayer_player_list);
//
//        updateList();
//
//        configureAdapter();
//
//        return root;
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        onCreateLobbyFragmentInteractionListener = null;
//        iFragmentFlipper = null;
//        opponentName = null;
//        GameUtility.mpc.setRunnable(null);
//    }
//
//    public static CreateLobbyFragment newInstance(final boolean isOnline) {
//        final CreateLobbyFragment fragment = new CreateLobbyFragment();
//        final Bundle args = new Bundle();
//        args.putBoolean(KEY_IS_ONLINE, isOnline);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnCreateLobbyFragmentInteractionListener {
//        void onPlayerClicked(final String playerName);
//        void startNewMultiplayerGame(final String opponentName, final String theWord);
//    }
//
//    private void onButtonPressed(final int position) {
//        if (onCreateLobbyFragmentInteractionListener != null)
//            if (opponentName == null ) onCreateLobbyFragmentInteractionListener.onPlayerClicked(players.get(position).getmName());
//            else onCreateLobbyFragmentInteractionListener.onPlayerClicked(wordList.get(position).getTitle());
//    }
//
//    private void configureAdapter() {
//        playerAdapter = new MultiplayerPlayersAdapter(getContext(), R.layout.multiplayer_player_list_row, players);
//        listView.setAdapter(playerAdapter);
//        listView.setOnItemClickListener(new OnCreateLobbyClick(this));
//    }
//
//    private void updateList() {
//        players.clear();
//        if (updater == null) updater = new UpdateList();
//        configureAdapter();
//
////        Firebase.setAndroidContext(getActivity());
////        multiplayerController = new MultiplayerController(new Firebase("https://hangmandtu.firebaseio.com"), updater);
//
////        setProgressListener.setProgressBar(true);
//        GameUtility.mpc.setRunnable(updater);
//    }
//
//    public void setOnlineStatus(final boolean newStatus) {
//        if (isOffline && newStatus) {
//            // we got connection!
//            isOffline = false;
//            updateList();
//        } else {
//            isOffline = !newStatus;
//        }
//    }
//
//    private static class OnCreateLobbyClick extends WeakReferenceHolder<CreateLobbyFragment> implements AdapterView.OnItemClickListener {
//
//        public OnCreateLobbyClick(final CreateLobbyFragment createLobbyFragment) {
//            super(createLobbyFragment);
//        }
//
//        @Override
//        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
//            final CreateLobbyFragment createLobbyFragment =  mWeakReference.get();
//            if (createLobbyFragment != null) {
//                Log.d("NG", String.valueOf(id));
//                // do stuff!!!
//                createLobbyFragment.onButtonPressed(position);
//                if(GameUtility.mpc.name == null) {
//                    //GameUtility.mpc.login(multiPlayerPlayerFragment.players.get(position).getmName());
//                } else if (opponentName != null) {
//                    final WordItem item = createLobbyFragment.wordList.get(position);
//                    Log.d("createlobbyfragment", opponentName + " wordlist size = " + item.getWords().size() + " random ord " + item.getWords().get(getRandom(item.getWords().size())));
//                    final String w = item.getWords().get(getRandom(item.getWords().size()));
//                    final LobbyPlayerStatus lps1 = new LobbyPlayerStatus(opponentName, -1);
//                    final LobbyPlayerStatus lps2 = new LobbyPlayerStatus(GameUtility.mpc.name, -1);
//                    final LobbyDTO dto = new LobbyDTO();
//                    dto.add(lps1); dto.add(lps2); dto.setWord(w);
//                    GameUtility.mpc.createLobby(dto);
//                    GameUtility.mpc.setRunnable(null);
//                    createLobbyFragment.iFragmentFlipper.flipFragment(Constant.MODE_MENU);
////                    createLobbyFragment.getActivity().onBackPressed();// TODO design return
//                    //opponentName = null ;GameUtility.mpc.update();
//                } else {
//                    opponentName = createLobbyFragment.players.get(position).getmName();
//                    GameUtility.mpc.update();
//                }
//            }
//        }
//
//        private static int getRandom(final int size) {
//            return (int) ( 4 + Math.random()*4*size ) % size;
//        }
//    }
//
//
//
//    private static class FloatListener implements View.OnClickListener {
//        @Override
//        public void onClick(final View view) {
//            WindowLayout.showSnack("Her kan der ske noget interesant! (manuel aktivering af liste opdatering?)", view, true);
//        }
//    }
//
//    private class UpdateList implements Runnable {
//        @Override
//        public void run() {
//            wordList.clear();
//            wordList.addAll(WordListController.wordList.values());
//            Log.d("UpdateList", wordList.toString() + "  " +wordList.size());
//            players.clear();
//            players.addAll(GameUtility.mpc.pc.playerList.values());
//            if (GameUtility.mpc.name == null) {
//                // ups not logged in
//                Log.e("CreateLobbyFragment", "not logged in error");
//            } else if (opponentName != null) {
//                wordTitleAdapter = new WordTitleLocalAdapter(getContext(), R.layout.word_list_nav_drawer_list, wordList);
//                listView.setAdapter(wordTitleAdapter);
//                wordTitleAdapter.notifyDataSetChanged();
//            } else {
//                playerAdapter = new MultiplayerPlayersAdapter(getContext(), R.layout.multiplayer_player_list_row, players);
//                listView.setAdapter(playerAdapter);
//
//                players.remove(GameUtility.mpc.pc.playerList.get(GameUtility.mpc.name)); // remove logged in player TODO rewrite
//                playerAdapter.notifyDataSetChanged();
//                GameUtility.prefs.putObject(KEY_LAST_PLAYER_LIST, players);
//            }
//        }
//    }
//}
