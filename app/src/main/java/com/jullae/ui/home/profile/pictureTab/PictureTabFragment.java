package com.jullae.ui.home.profile.pictureTab;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jullae.R;
import com.jullae.app.AppController;
import com.jullae.data.db.model.PictureModel;
import com.jullae.ui.adapters.PicturesAdapter;
import com.jullae.ui.base.BaseFragment;

import java.util.List;

public class PictureTabFragment extends BaseFragment implements PictureTabView {

    private View view;
    private RecyclerView recyclerView;
    private PicturesAdapter picturesAdapter;
    private PictureTabPresentor mPresentor;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_story_tab_profile, container, false);

        mPresentor = new PictureTabPresentor(((AppController) getmContext().getApplication()).getmAppDataManager());

        setuprecyclerView();
        return view;
    }

    private void setuprecyclerView() {
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getmContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        picturesAdapter = new PicturesAdapter(getmContext());
        recyclerView.setAdapter(picturesAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresentor.attachView(this);
        mPresentor.loadFeeds();
    }

    @Override
    public void onDestroyView() {
        mPresentor.detachView();
        super.onDestroyView();

    }

    @Override
    public void onPicturesFetchSuccess(List<PictureModel> pictureModelList) {
        picturesAdapter.add(pictureModelList);
    }

    @Override
    public void onPicturesFetchFail() {

    }
}