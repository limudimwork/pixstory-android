package com.jullae.ui.home.homeFeed;

import android.os.Handler;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.jullae.data.AppDataManager;
import com.jullae.ui.adapters.LikeAdapter;
import com.jullae.ui.base.BasePresentor;
import com.jullae.ui.base.BaseResponseModel;
import com.jullae.ui.home.homeFeed.freshfeed.HomeFeedAdapter;
import com.jullae.ui.storydetails.StoryDetailPresentor;
import com.jullae.utils.Constants;
import com.jullae.utils.ErrorResponseModel;
import com.jullae.utils.NetworkUtils;

public class HomeFeedPresentor extends BasePresentor<HomeFeedView> {


    private static final String TAG = HomeFeedPresentor.class.getName();
    private boolean isLoadFeedReqRunning;
    private boolean isLoadingMore;
    private int count = 2;

    public HomeFeedPresentor() {
    }

    public void loadFeeds() {
        isLoadFeedReqRunning = true;
        checkViewAttached();
        getMvpView().showProgress();
        AppDataManager.getInstance().getmApiHelper().loadHomeFeeds(1).getAsObject(HomeFeedModel.class, new ParsedRequestListener<HomeFeedModel>() {
            @Override
            public void onResponse(HomeFeedModel homeFeedModel) {
                isLoadFeedReqRunning = false;
                NetworkUtils.parseResponse(TAG, homeFeedModel);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFetchFeedSuccess(homeFeedModel);
                }
            }

            @Override
            public void onError(ANError anError) {
                isLoadFeedReqRunning = false;
                ErrorResponseModel errorResponseModel = NetworkUtils.parseError(TAG, anError);
                if (errorResponseModel.getErrorcode() == 706) {
                    if (isViewAttached()) {
                        getMvpView().hideProgress();
                        getMvpView().onFetchFeedFail(errorResponseModel.getMessage());
                    }
                } else
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onFetchFeedFail("");
                }
            }
        });
    }


    public void setLike(String id, final HomeFeedAdapter.ReqListener reqListener, boolean isLiked) {
        checkViewAttached();
        AppDataManager.getInstance().getmApiHelper().setlikeReq(id, isLiked, Constants.LIKE_TYPE_PICTURE).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                if (isViewAttached())
                    reqListener.onSuccess();

            }

            @Override
            public void onError(ANError anError) {
                if (isViewAttached())
                    reqListener.onFail();
            }
        });
    }


    public void makeFollowUserReq(String user_id, final LikeAdapter.FollowReqListener followReqListener, Boolean is_followed) {
        checkViewAttached();
        AppDataManager.getInstance().getmApiHelper().makeFollowReq(user_id, is_followed).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                NetworkUtils.parseResponse(TAG, response);
                if (isViewAttached())
                    followReqListener.onSuccess();
            }

            @Override
            public void onError(ANError anError) {
                NetworkUtils.parseError(TAG, anError);
                if (isViewAttached())
                    followReqListener.onFail();
            }
        });

    }

    public void reportPicture(String report, String picture_id, final StoryDetailPresentor.StringReqListener stringReqListener) {
        checkViewAttached();
        AppDataManager.getInstance().getmApiHelper().report(report, picture_id, Constants.REPORT_TYPE_PICTURE).getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                NetworkUtils.parseResponse(TAG, response);
                if (isViewAttached())
                    stringReqListener.onSuccess();
            }

            @Override
            public void onError(ANError anError) {
                NetworkUtils.parseError(TAG, anError);
                if (isViewAttached())
                    stringReqListener.onFail();
            }
        });


    }

    public void sendPictureDeleteReq(String picture_id) {
        checkViewAttached();
        getMvpView().showProgress();
        AppDataManager.getInstance().getmApiHelper().makePictureDeleteReq(picture_id).getAsObject(BaseResponseModel.class, new ParsedRequestListener<BaseResponseModel>() {

            @Override
            public void onResponse(BaseResponseModel response) {
                NetworkUtils.parseResponse(TAG, response);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onPictureDeleteSuccess();

                }
            }

            @Override
            public void onError(ANError anError) {
                ErrorResponseModel errorResponseModel = NetworkUtils.parseError(TAG, anError);
                if (isViewAttached()) {
                    getMvpView().hideProgress();
                    getMvpView().onPictureDeleteFail(errorResponseModel.getMessage());
                }


            }
        });
    }

    public boolean isLoadFeedReqRunning() {
        return isLoadFeedReqRunning;
    }

    public void loadMoreFeeds() {
        checkViewAttached();
        getMvpView().showLoadMoreFeedProgress();
        if (!isLoadingMore) {
            isLoadingMore = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppDataManager.getInstance().getmApiHelper().loadHomeFeeds(count).getAsObject(HomeFeedModel.class, new ParsedRequestListener<HomeFeedModel>() {
                        @Override
                        public void onResponse(HomeFeedModel homeFeedModel) {
                            isLoadingMore = false;
                            NetworkUtils.parseResponse(TAG, homeFeedModel);
                            if (isViewAttached()) {
                                count++;
                                getMvpView().hideLoadMoreFeedProgress();

                                getMvpView().onFetchMoreFeedSuccess(homeFeedModel);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            isLoadingMore = false;
                            NetworkUtils.parseError(TAG, anError);
                            if (isViewAttached()) {
                                getMvpView().hideLoadMoreFeedProgress();
                                getMvpView().onFetchMoreFeedFail();
                            }
                        }
                    });

                }
            }, 1000);
        }
    }
}
