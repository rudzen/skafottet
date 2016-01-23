///*
// * Copyright 2016 Rudy Alex Kohn
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.undyingideas.thor.skafottet.fragments;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.widget.Toolbar;
//import android.widget.AdapterView;
//
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.undyingideas.thor.skafottet.adapters.WordListAdapter;
//import com.undyingideas.thor.skafottet.adapters.WordTitleLocalAdapter;
//import com.undyingideas.thor.skafottet.adapters.WordTitleRemoteAdapter;
//import com.undyingideas.thor.skafottet.broadcastrecievers.InternetReciever;
//import com.undyingideas.thor.skafottet.broadcastrecievers.InternetRecieverData;
//import com.undyingideas.thor.skafottet.support.utility.WindowLayout;
//
//import net.steamcrafted.loadtoast.LoadToast;
//
//import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
//
///**
// * Created on 23-01-2016, 09:44.<br>
// * Project : skafottet<br>
// * Wordlist Fragment.
// * @author rudz
// */
//public class WordListFragment extends Fragment implements
//        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener,
//        StickyListHeadersListView.OnStickyHeaderOffsetChangedListener,
//        StickyListHeadersListView.OnStickyHeaderChangedListener,
//        InternetRecieverData.InternetRecieverInterface
//{
//
//    private static final String TAG = "WordListFragment";
//
//    private WordListAdapter mAdapter;
//    private DrawerLayout mDrawerLayout;
//    private ActionBarDrawerToggle mDrawerToggle;
//
//    private StickyListHeadersListView stickyList;
//    private SwipeRefreshLayout refreshLayout;
//
//    private Toolbar toolbar;
////    private ProgressBar progressBar;
//
//    public MaterialDialog md; // for add list
//
//    private WordTitleLocalAdapter adapterLocal;
//    private WordTitleRemoteAdapter adapterRemote;
//
//    private Handler handler;
//    private Runnable refreshStopper;
//
//    private InternetRecieverData internetRecieverData;
//
//
//    @Override
//    public void onCreate(final Bundle savedInstanceState) {
//        /* set up non-view related fields */
//        internetRecieverData = new InternetRecieverData(this);
//        InternetReciever.addObserver(internetRecieverData);
//
//
//        super.onCreate(savedInstanceState);
//    }
//}
