package com.jullae.data;


import com.jullae.data.network.ApiHelper;
import com.jullae.data.prefs.AppPrefsHelper;

/**
 * Created by master on 7/4/18.
 */

public class AppDataManager {

    private final AppPrefsHelper mAppPrefsHelper;
    private final ApiHelper mApiHelper;

    public AppDataManager(AppPrefsHelper appPrefsHelper) {
        mAppPrefsHelper = appPrefsHelper;
        mApiHelper = new ApiHelper(mAppPrefsHelper.getKeyToken());
    }

    public AppPrefsHelper getmAppPrefsHelper() {
        return mAppPrefsHelper;
    }

    public ApiHelper getmApiHelper() {
        return mApiHelper;
    }


}