package com.jullae.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.jullae.data.db.model.ProfileModel;
import com.jullae.ui.loginscreen.LoginResponseModel;
import com.jullae.utils.AppUtils;

import static com.jullae.ApplicationClass.PACKAGE_NAME;

/**
 * Created by master on 1/4/18.
 */

public class AppPrefsHelper {

    public static final String KEY_LOCALE = "locale";
    private static final String PREFS = PACKAGE_NAME + ".PREFS";
    private static final String IS_LOGGED_IN = "IS_LOGGED_IN";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_NAME = "name";
    private static final String KEY_PENNAME = "penname";
    private static final String KEY_BIO = "bio";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_DP_URL = "dp_url";

    private final SharedPreferences sharedPreferences;
    private final Context mContext;

    public AppPrefsHelper(Context context) {
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }


    public boolean getLoggedInMode() {
        return sharedPreferences.getBoolean(IS_LOGGED_IN, false);
    }

    private void setLoggedInMode(boolean loggedIn) {
        sharedPreferences.edit().putBoolean("IS_LOGGED_IN", loggedIn).apply();
    }



    public void saveUserDetails(LoginResponseModel loginResponseModel) {
        sharedPreferences.edit().putString(KEY_EMAIL, loginResponseModel.getEmail()).commit();
        sharedPreferences.edit().putString(KEY_NAME, loginResponseModel.getName()).commit();
        sharedPreferences.edit().putString(KEY_PENNAME, loginResponseModel.getPenname()).commit();
        sharedPreferences.edit().putString(KEY_BIO, loginResponseModel.getBio()).commit();
        sharedPreferences.edit().putString(KEY_USER_ID, loginResponseModel.getUser_id()).commit();
        sharedPreferences.edit().putString(KEY_TOKEN, loginResponseModel.getToken()).commit();
        sharedPreferences.edit().putString(KEY_DP_URL, loginResponseModel.getAvatar()).commit();
        setLoggedInMode(true);
    }


    public String getKeyName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    public void setKeyName(String name) {
        sharedPreferences.edit().putString(KEY_NAME, name).commit();
    }

    public String getKeyPenname() {
        return sharedPreferences.getString(KEY_PENNAME, "");
    }

    public String getKeyToken() {
        return sharedPreferences.getString(KEY_TOKEN, "");
    }

    public String getKeyBio() {
        return sharedPreferences.getString(KEY_BIO, "");
    }

    public void setKeyBio(String bio) {
        sharedPreferences.edit().putString(KEY_BIO, bio).commit();
    }

    public String getKeyUserId() {
        return sharedPreferences.getString(KEY_USER_ID, "");
    }

    public String getKeyEmail() {
        return KEY_EMAIL;
    }

    public String getKeyDpUrl() {
        return sharedPreferences.getString(KEY_DP_URL, "");
    }

    public void setKeyDpUrl(String profile_dp_url) {
        sharedPreferences.edit().putString(KEY_DP_URL, profile_dp_url).commit();
    }


    public void putinsharedprefBoolean(String key, boolean value) {

        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public Boolean getvaluefromsharedprefBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putinsharedprefString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public String getvaluefromsharedprefString(String key) {
        return sharedPreferences.getString(key, null);
    }

    public String getDeviceId() {
        return AppUtils.getDeviceId(mContext);
    }

    public void updateLocale(String localeCode) {
        putinsharedprefString(KEY_LOCALE, localeCode);

    }


    public ProfileModel getPrefsUserData() {
        return new ProfileModel(getKeyName(), getKeyPenname(), getKeyBio(), getKeyDpUrl());
    }
}
