package com.jullae.data.network;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jullae.utils.Constants;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static com.jullae.BuildConfig.BASE_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_ADD_MESSAGE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_ADD_PROFILE_DETAILS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_ADD_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_ARCHIVED_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_COMMENT;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_DELETE_BOOKMARK;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_DELETE_COMMENT;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_DELETE_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_EMAIL_LOGIN;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_EMAIL_SIGNUP;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FACEBOOK_LOGIN;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FOLLOW;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FOLLOWERS_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FOLLOWINGS_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FORGOT_PASSWORD;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_FRESH_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_GOOGLE_LOGIN;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_HOME_FEEDS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_LIKE_PICTURE_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_LIKE_STORY_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_NOTIFICATION_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_NOTIFICATION_READ_STATUS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PASSWORD_CHANGE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PEOPLE_SUGGESTIONS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PICTURE_DELETE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_PICTURE_DETAIL;
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
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_STORY_DELETE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_STORY_DETAILS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_STORY_LIKES_LIST;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_TAG_SUGGESTIONS;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UNFOLLOW;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UNLIKE_PICTURE_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UNLIKE_STORY_URL;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UPDATE_EDIT_STORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UPDATE_PROFILE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_UPLOAD_PICTURE;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_VIEW_ALL_STORIES;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_WRITESTORY_CATEGORY;
import static com.jullae.data.network.ApiEndPoint.ENDPOINT_WRITE_COMMENTS;

/**
 * Created by master on 1/4/18.
 */

public class ApiHelper {

    private static final String TAG = ApiHelper.class.getName();
    private static final String DEVICE_TYPE = "device_type";
    private static final String ANDROID = "android";
    private static final String PER = "10";
    private final HashMap<String, String> headers;

    public ApiHelper(String keyToken) {
        headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + keyToken);
    }

    public void updateToken(String token) {
        headers.put("Authorization", "Bearer " + token);
    }

    public ANRequest loadFreshFeeds(int position, int count) {
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
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", PER)
                .setPriority(Priority.HIGH)
                .logReponseBody()

                .build();
    }

    public ANRequest loadsearchfeeds(String searchTag) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_SEARCH_TAG)
                .addHeaders(headers)
                .addQueryParameter("term", searchTag)
                .setPriority(Priority.HIGH)
                .logReponseBody()

                .build();
    }

    public ANRequest loadHomeFeeds(int count) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_HOME_FEEDS)
                .addHeaders(headers)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", "10")
                .setPriority(Priority.HIGH)
                .logReponseBody()
                .build();
    }

    public ANRequest setlikeReq(String id, boolean isLiked, int likeType) {
        String url;
        if (!isLiked) {
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
                .logReponseBody()
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
                .logReponseBody()
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

    public ANRequest loadComments(String story_id, int count) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_COMMENT)
                .addHeaders(headers)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", "20")
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

    public ANRequest report(String report, String id, int reportTypeStory) {
        Log.d(TAG, "report: wejfdbwekhfvbkefbbenfbse: " + id + "   " + reportTypeStory);
        String url = ENDPOINT_REPORT_STORY;
        String reportType = null;
        if (reportTypeStory == Constants.REPORT_TYPE_STORY) {

            reportType = "Story";

        } else if (reportTypeStory == Constants.REPORT_TYPE_PICTURE) {

            reportType = "Picture";
        }

        return AndroidNetworking.post(BASE_URL + url)
                .addHeaders(headers)
                .addBodyParameter("reportable_type", reportType)
                .addBodyParameter("reportable_id", id)
                .addBodyParameter("reason", report)
                .setPriority(Priority.HIGH)
                .logReponseBody()
                .build();
    }

    public ANRequest reportuser(String report, String id) {
        String url = ENDPOINT_REPORT_STORY;
        String reportType = "User";


        return AndroidNetworking.post(BASE_URL + url)
                .addHeaders(headers)
                .addBodyParameter("reportable_type", reportType)
                .addBodyParameter("reportable_id", id)
                .addBodyParameter("reason", report)
                .setPriority(Priority.HIGH)
                .logReponseBody()
                .build();
    }

    public ANRequest emailLoginReq(String email, String password) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_EMAIL_LOGIN)
                .addHeaders(headers)
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .addBodyParameter("fcm_token", FirebaseInstanceId.getInstance().getToken())
                .addBodyParameter(DEVICE_TYPE, ANDROID)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest signUpReq(String email, String password, String name, String penname, String bio, File imageFile) {
        if (bio == null)
            bio = "";
        if (imageFile != null) return AndroidNetworking.upload(BASE_URL + ENDPOINT_EMAIL_SIGNUP)
                .addHeaders(headers)
                .addMultipartFile("avatar", imageFile)
                .addMultipartParameter("email", email)
                .addMultipartParameter("password", password)
                .addMultipartParameter("name", name)
                .addMultipartParameter("penname", penname)
                .addMultipartParameter("bio", bio)
                .addMultipartParameter("fcm_token", FirebaseInstanceId.getInstance().getToken())
                .addMultipartParameter(DEVICE_TYPE, ANDROID)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
        else
            return AndroidNetworking.post(BASE_URL + ENDPOINT_EMAIL_SIGNUP)
                    .addHeaders(headers)
                    .addBodyParameter("email", email)
                    .addBodyParameter("password", password)
                    .addBodyParameter("name", name)
                    .addBodyParameter("penname", penname)
                    .addBodyParameter("bio", bio)
                    .addBodyParameter("fcm_token", FirebaseInstanceId.getInstance().getToken())
                    .addBodyParameter(DEVICE_TYPE, ANDROID)
                    .setPriority(Priority.MEDIUM)
                    .logReponseBody()
                    .build();
    }

    public ANRequest loadStoryTabFeeds(String penname, int count) {

        return AndroidNetworking.get(BASE_URL + ENDPOINT_PROFILE_TAB_STORIES)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", PER)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest loadBookmarkTabFeeds(String penname, int count) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_PROFILE_TAB_BOOKMARKS)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", PER)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest loadPictureTabFeeds(String penname, int count) {

        return AndroidNetworking.get(BASE_URL + ENDPOINT_PROFILE_TAB_PICTURES)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", PER)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest makeDpChangeReq(String userid, File file) {
        return AndroidNetworking.upload(BASE_URL + ENDPOINT_PROFILE_PIC_UPDATE)
                .addMultipartFile("avatar", file)
                .addHeaders(headers)
                .addPathParameter("id", userid)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest getDraftList(String penname, int count) {
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_DRAFTS)
                .addHeaders(headers)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", PER)
                .addPathParameter("penname", penname)
                .setPriority(Priority.MEDIUM)
                .build();
    }


    public ANRequest fetchVisitorProfileData(String penname) {
        Log.d(TAG, "fetchVisitorProfileData: " + penname);
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_PROFILE_VISITOR_INFO)
                .addHeaders(headers)
                .addPathParameter("penname", penname)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();

    }

    public ANRequest getConversationList() {
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_CONVERSATION_LIST)
                .addHeaders(headers)
                .setPriority(Priority.MEDIUM)
                .build();

    }

    /**
     * Load message list an request.
     *
     * @param user_id the user id
     * @return the ANRequest
     */
    public ANRequest loadMessageList(String user_id) {
        return AndroidNetworking.get(BASE_URL + ApiEndPoint.ENDPOINT_MESSAGE_LIST)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest sendMessageReq(String message, String user_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_ADD_MESSAGE)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .addBodyParameter("body", message)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest getSearchList(String text) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_TAG_SUGGESTIONS)
                .addHeaders(headers)
                .addQueryParameter("term", text)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest getSearchPeopleList(String text) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_PEOPLE_SUGGESTIONS)
                .addHeaders(headers)
                .addQueryParameter("term", text)
                .setPriority(Priority.MEDIUM)
                .build();
    }


    /*POST - /api/v1/story/publish
Request Params: {"title": "Title", "content": "Text here", "picture_id": picture_id}*/
    public ANRequest publishStory(String title, String content, String picture_id, String categoryId) {

        return AndroidNetworking.post(BASE_URL + ENDPOINT_PUBLISH_STORY)
                .addHeaders(headers)
                .addBodyParameter("title", title)
                .addBodyParameter("content", content)
                .addBodyParameter("picture_id", picture_id)
                .addBodyParameter("category_id", categoryId)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest draftStory(String title, String content, String picture_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_PUBLISH_DRAFT)
                .addHeaders(headers)
                .addBodyParameter("title", title)
                .addBodyParameter("content", content)
                .addBodyParameter("picture_id", picture_id)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest makeDraftDeleteReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_DELETE_STORY)
                .addHeaders(headers)
                .addPathParameter("id", story_id)

                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest makeBookmarkDeleteReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_DELETE_BOOKMARK)
                .addHeaders(headers)
                .addPathParameter("id", story_id)

                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest updateProfileReq(String name, String bio, String user_id, File imagefile) {
        if (imagefile != null)

            return AndroidNetworking.upload(BASE_URL + ENDPOINT_UPDATE_PROFILE)
                    .addHeaders(headers)
                    .addMultipartFile("avatar", imagefile)
                    .addPathParameter("id", user_id)
                    .addMultipartParameter("bio", bio)
                    .addMultipartParameter("name", name)
                    .setPriority(Priority.MEDIUM)
                    .logReponseBody()
                    .build();
        else
            return AndroidNetworking.post(BASE_URL + ENDPOINT_UPDATE_PROFILE)
                    .addHeaders(headers)
                    .addPathParameter("id", user_id)
                    .addBodyParameter("bio", bio)
                    .addBodyParameter("name", name)
                    .setPriority(Priority.MEDIUM)
                    .build();
    }

    public ANRequest sendStorySaveReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_SAVE_STORY)
                .addHeaders(headers)
                .addPathParameter("id", story_id)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest loadAllStories(String picture_id) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_VIEW_ALL_STORIES)
                .addHeaders(headers)
                .addQueryParameter("picture_id", picture_id)
                .addQueryParameter("story_id", "0")
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest makeUploadPictureReq(File file) {
        return AndroidNetworking.upload(BASE_URL + ENDPOINT_UPLOAD_PICTURE)
                .addHeaders(headers)
                .addMultipartFile("image", file)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest loadNotificationList(String user_id, int count) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_NOTIFICATION_LIST)
                .addHeaders(headers)
                .addPathParameter("id", user_id)
                .addQueryParameter("page", String.valueOf(count))
                .addQueryParameter("per", PER)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest sendNotiReadReq(String keyUserId) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_NOTIFICATION_READ_STATUS)
                .addHeaders(headers)
                .addPathParameter("id", keyUserId)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest googleSignInReq(String idToken) {
        Log.d(TAG, "googleSignInReq: " + idToken);
        Log.d(TAG, "googleSignInReq: " + FirebaseInstanceId.getInstance().getToken());
        return AndroidNetworking.post(BASE_URL + ENDPOINT_GOOGLE_LOGIN)
                .addHeaders(headers)
                .addBodyParameter("token", idToken)
                .addBodyParameter("fcm_token", FirebaseInstanceId.getInstance().getToken())
                .addBodyParameter(DEVICE_TYPE, ANDROID)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()

                .build();
    }

    public ANRequest addProfileDetailReq(String user_id, String token, String penname, String email) {
        if (!email.isEmpty()) {
            return AndroidNetworking.post(BASE_URL + ENDPOINT_ADD_PROFILE_DETAILS)
                    .addHeaders("Authorization", "Bearer " + token)
                    .addBodyParameter("user_id", user_id)
                    .addBodyParameter("penname", penname)
                    .addBodyParameter("email", email)
                    .setPriority(Priority.MEDIUM)
                    .logReponseBody()
                    .build();
        } else {
            return AndroidNetworking.post(BASE_URL + ENDPOINT_ADD_PROFILE_DETAILS)
                    .addHeaders("Authorization", "Bearer " + token)
                    .addBodyParameter("user_id", user_id)
                    .addBodyParameter("penname", penname)
                    .setPriority(Priority.MEDIUM)
                    .build();
        }
    }

    public ANRequest makeFbLoginReq(String token) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_FACEBOOK_LOGIN)
                .addHeaders(headers)
                .addBodyParameter("token", token)
                .addBodyParameter("fcm_token", FirebaseInstanceId.getInstance().getToken())
                .addBodyParameter(DEVICE_TYPE, ANDROID)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()

                .build();
    }

    public ANRequest makeForgotPasswordReq(String email) {
        Log.d(TAG, "makeForgotPasswordReq: " + email);
        return AndroidNetworking.get(BASE_URL + ENDPOINT_FORGOT_PASSWORD)
                .addHeaders(headers)
                .addQueryParameter("email", email)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest loadPictureDetail(String picture_id) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_PICTURE_DETAIL)
                .addHeaders(headers)
                .addQueryParameter("picture_id", picture_id)
                /*.addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")*/
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest fetchStoryDetails(String story_id) {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_STORY_DETAILS)
                .addHeaders(headers)
                .addPathParameter("id", story_id)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest makePasswordChangeReq(String oldpassword, String newpassword, String keyUserId) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_PASSWORD_CHANGE)
                .addHeaders(headers)
                .addPathParameter("id", keyUserId)
                .addBodyParameter("old_password", oldpassword)
                .addBodyParameter("new_password", newpassword)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest makeStoryDeleteReq(String story_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_STORY_DELETE)
                .addHeaders(headers)
                .addPathParameter("id", story_id)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest makePictureDeleteReq(String picture_id) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_PICTURE_DELETE)
                .addHeaders(headers)
                .addPathParameter("id", picture_id)
                .setPriority(Priority.MEDIUM)
                .build();
    }

    public ANRequest updateStoryReq(String story_id, String title, String story) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_UPDATE_EDIT_STORY)
                .addHeaders(headers)
                .addPathParameter("id", story_id)
                .addBodyParameter("title", title)
                .addBodyParameter("content", story)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest loadPictures() {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_ADD_STORY)
                .addHeaders(headers)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest getFollowersList(String userId, int count, boolean isFollowerPage) {
        if (isFollowerPage)
            return AndroidNetworking.get(BASE_URL + ENDPOINT_FOLLOWERS_LIST)
                    .addHeaders(headers)
                    .addPathParameter("id", userId)
                    .addQueryParameter("page", String.valueOf(count))
                    .addQueryParameter("per", PER)
                    .setPriority(Priority.MEDIUM)
                    .logReponseBody()
                    .build();
        else
            return AndroidNetworking.get(BASE_URL + ENDPOINT_FOLLOWINGS_LIST)
                    .addHeaders(headers)
                    .addPathParameter("id", userId)
                    .addQueryParameter("page", String.valueOf(count))
                    .addQueryParameter("per", PER)
                    .setPriority(Priority.MEDIUM)
                    .logReponseBody()
                    .build();
    }


    public ANRequest makeCommentDeleteReq(String commentId) {
        return AndroidNetworking.post(BASE_URL + ENDPOINT_DELETE_COMMENT)
                .addHeaders(headers)
                .addPathParameter("id", commentId)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }

    public ANRequest fetchWriteStoryCategory() {
        return AndroidNetworking.get(BASE_URL + ENDPOINT_WRITESTORY_CATEGORY)
                .addHeaders(headers)
                .setPriority(Priority.MEDIUM)
                .logReponseBody()
                .build();
    }


    public interface ReqListener {


        void onSuccess(JSONObject response);

        void onFail();

    }


}
