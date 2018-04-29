package com.jullae.data.network;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.jullae.utils.Constants;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static com.jullae.BuildConfig.BASE_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_ADD_MESSAGE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_ARCHIVED_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_DELETE_BOOKMARK;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_DELETE_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_EMAIL_LOGIN;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_EMAIL_SIGNUP;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FOLLOW;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FRESH_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_HOME_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_LIKE_PICTURE_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_LIKE_STORY_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_NOTIFICATION_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PICTURE_LIKES_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_POPULAR_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PROFILE_PIC_UPDATE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PROFILE_TAB_BOOKMARKS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PROFILE_TAB_PICTURES;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PROFILE_TAB_STORIES;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PUBLISH_DRAFT;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PUBLISH_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_REPORT_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_SAVE_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_SEARCH_TAG;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_STORY_DETAILS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_STORY_LIKES_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_TAG_SUGGESTIONS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UNFOLLOW;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UNLIKE_PICTURE_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UNLIKE_STORY_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UPDATE_PROFILE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UPLOAD_PICTURE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_VIEW_ALL_STORIES;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_WRITE_COMMENTS;

/**
 * Created by master on 1/4/18.
 */

public class ApiHelper {

    private static final String TAG = ApiHelper.class.getName();
    private final HashMap<String, String> headers;

    public ApiHelper(String keyToken) {
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + keyToken);
    }

    public void updateToken(String token) {
        headers.put("Authorization", "Bearer " + token);
    }

    public ANRequest loadFreshFeeds(int position) {
        String end_point;
        switch (position) {
            case 0:
                end_point = ENDPOINT_FRESH_FEEDS;
                break;
            case 1:
                end_point = ENDPOINT_POPULAR_FEEDS;
                break;
            case 2:
                end_point = ENDPOINT_ARCHIVED_FEEDS;
                break;
            case 3:
                end_point = ENDPOINT_FRESH_FEEDS;
                break;
            default:
                end_point = ENDPOINT_FRESH_FEEDS;
        }
        return AndroidNetworking.get(BASE_URL + end_point)
                .addHeaders(headers)
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")*/
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadsearchfeeds(String searchTag) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_SEARCH_TAG)
                .addHeaders(headers)
                .addQueryParameter("term", searchTag)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadHomeFeeds() {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_HOME_FEEDS)
                .addHeaders(headers)
                .addQueryParameter("term", "karakoram")
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")*/
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest setlikeReq(String id, String isLiked, int likeType) {
        String url;
        if (isLiked.equals("false")) {
            if (likeType == Constants.LIKE_TYPE_PICTURE)
                url = ENDPOINT_LIKE_PICTURE_URL;
            else url = ENDPOINT_LIKE_STORY_URL;
        } else {
            if (likeType == Constants.LIKE_TYPE_PICTURE)
                url = ENDPOINT_UNLIKE_PICTURE_URL;
            else url = ENDPOINT_UNLIKE_STORY_URL;
        }

        return AndroidNetworking.post(BASE_URL + url)
                .addHeaders(headers)
                .addPathParameter("id", id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest getLikesList(String id, int likesListType) {

        String endpoint = null;
        if (likesListType == Constants.LIKE_TYPE_STORY)
            endpoint = ENDPOINT_STORY_LIKES_LIST;
        else if (likesListType == Constants.LIKE_TYPE_PICTURE)
            endpoint = ENDPOINT_PICTURE_LIKES_LIST;


        return AndroidNetworking.get(BASE_URL + endpoint)
                .addHeaders(headers)
                .addPathParameter("id", id)
                .setPriority(Priority.HIGH)
                .build();


    }

    public ANRequest makeFollowReq(String user_id, Boolean is_followed) {
        String url = null;
        if (!is_followed)
            url = ENDPOINT_FOLLOW;
        else url = ENDPOINT_UNFOLLOW;
        return AndroidNetworking.post(BASE_URL + url)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .setPriority(Priority.HIGH)
                .build();


    }

    public ANRequest loadComments(String story_id) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_STORY_DETAILS)
                .addHeaders(headers)
                .addPathParameter("id", story_id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest sendCommentReq(String comment, String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_WRITE_COMMENTS)
                .addHeaders(headers)
                .addBodyParameter("comment", comment)
                .addBodyParameter("story_id", story_id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest reportStory(String report, String id, int reportTypeStory) {
        String url = null;
        String reportType = null;
        if (reportTypeStory == Constants.REPORT_TYPE_STORY) {
            url = ENDPOINT_REPORT_STORY;
            reportType = "StoryModel";

        }

        return AndroidNetworking.post(BASE_URL + url)
                .addHeaders(headers)
                .addBodyParameter("reportable_type", reportType)
                .addBodyParameter("reportable_id", id)
                .addBodyParameter("reason", report)
                .setPriority(Priority.HIGH)
                .build();


    }

    public ANRequest emailLoginReq(String email, String password) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_EMAIL_LOGIN)
                .addHeaders(headers)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest signUpReq(String email, String password, String name, String penname, String bio) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_EMAIL_SIGNUP)
                .addHeaders(headers)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("name", name)
                .addBodyParameter("penname", penname)
                .addBodyParameter("bio", bio)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadStoryTabFeeds(String penname) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_PROFILE_TAB_STORIES)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadBookmarkTabFeeds(String penname) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_PROFILE_TAB_BOOKMARKS)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadPictureTabFeeds(String penname) {

        return AndroidNetworking.get(BASE_URL + ENDPOINT_PROFILE_TAB_PICTURES)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest makeDpChangeReq(String userid, File file) {
        return AndroidNetworking.upload(BASE_URL + ENDPOINT_PROFILE_PIC_UPDATE)
                .addMultipartFile("avatar", file)
                .addHeaders(headers)
                .addPathParameter("id", userid)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest getDraftList(String penname) {
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_DRAFTS)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest fetchVisitorProfileData(String penname) {
        Log.d(TAG, "fetchVisitorProfileData: " + penname);
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_PROFILE_VISITOR_INFO)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .setPriority(Priority.HIGH)
                .build();

    }

    public ANRequest getConversationList() {
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_CONVERSATION_LIST)
                .addHeaders(headers)
                .setPriority(Priority.HIGH)
                .build();

    }

    public ANRequest loadMessageList(String user_id) {
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_MESSAGE_LIST)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest sendMessageReq(String message, String user_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_ADD_MESSAGE)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .addBodyParameter("body", message)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest getSearchList(String text) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_TAG_SUGGESTIONS)
                .addHeaders(headers)
                .addQueryParameter("term", text)
                .setPriority(Priority.HIGH)
                .build();
    }

    /*POST - /api/v1/story/publish
Request Params: {"title": "Title", "content": "Text here", "picture_id": picture_id}*/
    public ANRequest publishStory(String title, String content, String picture_id) {

        return AndroidNetworking.post(BASE_URL + ENDPOINT_PUBLISH_STORY)
                .addHeaders(headers)
                .addBodyParameter("title", title)
                .addBodyParameter("content", content)
                .addBodyParameter("picture_id", picture_id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest draftStory(String title, String content, String picture_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_PUBLISH_DRAFT)
                .addHeaders(headers)
                .addBodyParameter("title", title)
                .addBodyParameter("content", content)
                .addBodyParameter("picture_id", picture_id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest makeDraftDeleteReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_DELETE_STORY)
                .addHeaders(headers)
                .addPathParameter("id", story_id)

                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest makeBookmarkDeleteReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_DELETE_BOOKMARK)
                .addHeaders(headers)
                .addPathParameter("id", story_id)

                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest updateProfileReq(String name, String bio, String user_id) {

        return AndroidNetworking.post(BASE_URL + ENDPOINT_UPDATE_PROFILE)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .addBodyParameter("bio", bio)
                .addBodyParameter("name", name)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest sendStorySaveReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_SAVE_STORY)
                .addHeaders(headers)
                .addPathParameter("id", story_id)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadAllStories(String picture_id) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_VIEW_ALL_STORIES)
                .addHeaders(headers)
                .addQueryParameter("picture_id", picture_id)
                .addQueryParameter("story_id", "0")
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest makeUploadPictureReq(String title, File file) {

        /*    Request Params: {"picture_title": "title of the picture", "image": <multipart file to be uplaoded to server> }
         */
        return AndroidNetworking.upload(BASE_URL + ENDPOINT_UPLOAD_PICTURE)
                .addHeaders(headers)
                .addMultipartParameter("picture_title", title)
                .addMultipartFile("image", file)
                .setPriority(Priority.HIGH)
                .build();
    }

    public ANRequest loadNotificationList(String user_id) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_NOTIFICATION_LIST)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .setPriority(Priority.HIGH)
                .build();
    }


    public interface ReqListener {


        void onSuccess(JSONObject response);

        void onFail();

        interface StringReqListener {

            void onSuccess(String string);

            void onFail();
        }
    }


}
