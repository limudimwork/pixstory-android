package com.jullae.ui.home.homeFeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jullae.R;
import com.jullae.ui.base.BaseFragment;
import com.jullae.ui.custom.ItemOffTBsetDecoration;
import com.jullae.ui.home.HomeActivity;
import com.jullae.ui.home.homeFeed.freshfeed.HomeFeedAdapter;
import com.jullae.utils.AppUtils;
import com.jullae.utils.Constants;
import com.jullae.utils.NetworkUtils;

import java.util.ArrayList;

public class HomeFeedFragment extends BaseFragment implements HomeFeedView {

    private static final String TAG = HomeFeedFragment.class.getName();
    private View view;

    private HomeFeedPresentor mPresentor;
    private HomeFeedAdapter mAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int refreshMode = intent.getIntExtra(Constants.REFRESH_MODE, -1);
            Log.d("receiver", "Got message: " + refreshMode);
            switch (refreshMode) {
                case Constants.REFRESH_HOME_FEEDS:
                    loadFeeds();
                    break;
            }
        }
    };
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private boolean loading = true;
    private FloatingActionButton fab;


    private void loadFeeds() {
        view.findViewById(R.id.no_feeds).setVisibility(View.INVISIBLE);

        hideNetworkRetryContainer();
        recyclerView.scrollToPosition(0);
        mPresentor.loadFeeds();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        if (view != null) {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
            return view;
        }


        view = inflater.inflate(R.layout.fragment_home_feed, container, false);

        mPresentor = new HomeFeedPresentor();

        swipeRefresh = view.findViewById(R.id.swiperefresh);
        fab = view.findViewById(R.id.addButton);

        recyclerView = view.findViewById(R.id.recycler_view);
        setScrollListener();
        mAdapter = new HomeFeedAdapter(getmContext(), mPresentor);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getmContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ItemOffTBsetDecoration itemDecoration = new ItemOffTBsetDecoration(getmContext(), R.dimen.item_offset_2dp);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        view.findViewById(R.id.no_feeds).setVisibility(View.INVISIBLE);

        view.findViewById(R.id.discover).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getmContext()).showHomeFeedFragment(0);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresentor.loadFeeds();
            }
        });
        return view;
    }

    private void setScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                    totalItemCount = recyclerView.getLayoutManager().getItemCount();
                    pastVisiblesItems = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        Log.v("...", "Last Item Wow !");

                        //Do pagination.. i.e. fetch new data
                        mPresentor.loadMoreFeeds();

                    }
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresentor.attachView(this);
        loadFeeds();
        setupRefreshBroadcastListener();


    }


    private void setupRefreshBroadcastListener() {
        LocalBroadcastManager.getInstance(getmContext()).registerReceiver(mMessageReceiver,
                new IntentFilter(Constants.REFRESH_INTENT_FILTER));


    }


    @Override
    public void onDestroyView() {
        mPresentor.detachView();
        LocalBroadcastManager.getInstance(getmContext()).unregisterReceiver(mMessageReceiver);

        super.onDestroyView();
    }

    @Override
    public void onFetchFeedSuccess(HomeFeedModel homeFeedModel) {
        for (int i = 0; i < homeFeedModel.getFeedList().size(); i++) {
            for (int j = 0; j < homeFeedModel.getFeedList().get(i).getStories().size(); j++) {
                if (homeFeedModel.getFeedList().get(i).getStories().get(j).getStory_id().equals(homeFeedModel.getFeedList().get(i).getNav_story_id()))
                    homeFeedModel.getFeedList().get(i).setHighlightStoryIndex(j);
            }
        }

        if (homeFeedModel.getFeedList().size() > 0)
            mAdapter.add(homeFeedModel.getFeedList());
        else {
            mAdapter.add(new ArrayList<HomeFeedModel.Feed>());
            view.findViewById(R.id.no_feeds).setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onFetchFeedFail(String message) {
        if (!message.isEmpty()) {
            view.findViewById(R.id.no_feeds).setVisibility(View.VISIBLE);

        } else if (mAdapter.getItemCount() == 0) {
            showNetworkRetryContainer();
            NetworkUtils.registerNetworkChangeListener(getmContext(), new NetworkUtils.NetworkChangeListener() {
                @Override
                public void onNetworkAvailable() {
                    if (!mPresentor.isLoadFeedReqRunning())
                        loadFeeds();
                }
            });
        } else
            Toast.makeText(getmContext().getApplicationContext(), "No internet connectivity", Toast.LENGTH_SHORT).show();


    }

    private void showNetworkRetryContainer() {
        view.findViewById(R.id.network_container).setVisibility(View.VISIBLE);
        view.findViewById(R.id.network_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFeeds();
            }
        });
    }

    private void hideNetworkRetryContainer() {
        view.findViewById(R.id.network_container).setVisibility(View.GONE);

    }


    @Override
    public void showProgress() {
        view.findViewById(R.id.no_feeds).setVisibility(View.INVISIBLE);

        swipeRefresh.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        swipeRefresh.setRefreshing(false);

    }

    @Override
    public void onPictureDeleteSuccess() {
        Toast.makeText(getmContext().getApplicationContext(), "Picture deleted successfully!", Toast.LENGTH_SHORT).show();
        AppUtils.sendRefreshBroadcast(getmContext(), Constants.REFRESH_HOME_FEEDS);
    }

    @Override
    public void onPictureDeleteFail(String message) {
        Toast.makeText(getmContext().getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showLoadMoreFeedProgress() {
        view.findViewById(R.id.container_load_more).setVisibility(View.VISIBLE);

    }

    @Override
    public void hideLoadMoreFeedProgress() {
        view.findViewById(R.id.container_load_more).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFetchMoreFeedFail() {
        //  ToastUtils.showNoInternetToast(getmContext());
    }

    @Override
    public void onFetchMoreFeedSuccess(HomeFeedModel homeFeedModel) {
        recyclerView.stopScroll();

        mAdapter.addMore(homeFeedModel.getFeedList());
    }

    public void refreshFeeds() {
        mPresentor.loadFeeds();
    }
}
