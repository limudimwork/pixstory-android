package com.jullae.ui.home.profile.draftTab;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.jullae.data.AppDataManager;
import com.jullae.data.db.model.DraftModel;
import com.jullae.ui.base.BasePresentor;
import com.jullae.ui.base.BaseResponseModel;
import com.jullae.utils.NetworkUtils;

public class DraftTabPresentor extends BasePresentor<DraftTabView> {
    private static final String TAG = DraftTabPresentor.class.getName();
    private boolean isLoadingMore;
    private int count = 2;

    public DraftTabPresentor() {
    }

    public void loadDrafts() {
        checkViewAttached();
        AppDataManager.getInstance().getmApiHelper().getDraftList(AppDataManager.getInstance().getmSharedPrefsHelper().getKeyPenname(), 1).getAsObject(DraftModel.class, new ParsedRequestListener<DraftModel>() {
            @Override
            public void onResponse(DraftModel draftModel) {
                NetworkUtils.parseResponse(TAG, draftModel);
                if (isViewAttached()) {
                    getMvpView().onDraftsFetchSuccess(draftModel.getList());
                }
            }

            @Override
            public void onError(ANError anError) {
                NetworkUtils.parseError(TAG, anError);
                if (isViewAttached()) {
                    getMvpView().onDraftsFetchFail();
                }
            }
        });
    }

    public void loadMoreDrafts() {
        checkViewAttached();
        if (!isLoadingMore) {
            isLoadingMore = true;
            getMvpView().showLoadMoreProgess();
            AppDataManager.getInstance().getmApiHelper().getDraftList(AppDataManager.getInstance().getmSharedPrefsHelper().getKeyPenname(), count).getAsObject(DraftModel.class, new ParsedRequestListener<DraftModel>() {
                @Override
                public void onResponse(DraftModel draftModel) {
                    NetworkUtils.parseResponse(TAG, draftModel);
                    count++;
                    isLoadingMore = false;
                    if (isViewAttached()) {
                        getMvpView().hideLoadMoreProgess();

                        getMvpView().onMoreDraftsFetch(draftModel.getList());
                    }
                }

                @Override
                public void onError(ANError anError) {
                    NetworkUtils.parseError(TAG, anError);

                    isLoadingMore = false;
                    if (isViewAttached()) {
                        getMvpView().hideLoadMoreProgess();


                    }
                }

            });
        }
    }

    public void sendDeleteDraftReq(String story_id, final DraftTabAdapter.DeleteListener deleteListener) {
        checkViewAttached();
        AppDataManager.getInstance().getmApiHelper().makeDraftDeleteReq(story_id).getAsObject(BaseResponseModel.class, new ParsedRequestListener<BaseResponseModel>() {


            @Override
            public void onResponse(BaseResponseModel response) {
                NetworkUtils.parseResponse(TAG, response);
                if (isViewAttached()) {
                    deleteListener.onSuccess();

                }
            }

            @Override
            public void onError(ANError anError) {
                NetworkUtils.parseError(TAG, anError);
                if (isViewAttached()) {
                    deleteListener.onFail();
                }
            }
        });
    }
}
