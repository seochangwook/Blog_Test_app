package com.example.apple.test_app.manager.datamanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.apple.test_app.manager.MyApplication;

/**
 * Created by apple on 2016. 10. 19..
 */
public class PropertyManager {
    //일반 사용자 프로필 정보//
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_PROFILEIMAGEURL = "user_profileimageurl";
    private static final String KEY_USEREMAIL = "user_email";
    private static final String KEY_USERID = "user_id";
    private static final String KEY_USERGENDER = "user_gender";

    //SNS, FCM등의 토큰//
    private static final String KEY_FACEBOOK_ID = "facebookid";
    private static final String KEY_FCM_REG_ID = "fcmtoken";

    private static PropertyManager instance;

    //공유 프래퍼런스 생성//
    SharedPreferences mPrefs;
    SharedPreferences.Editor mEdittor;

    private PropertyManager() {
        Context context = MyApplication.getContext(); //현재 앱의 자원을 얻어온다.//

        //프래퍼런스를 사용하도록 설정//
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mEdittor = mPrefs.edit();
    }

    public static PropertyManager getInstance() {
        if (instance == null) {
            instance = new PropertyManager();
        }

        return instance;
    }

    //프래퍼런스에 저장된 값들을 불러온다.//
    public String get_user_name() {
        return mPrefs.getString(KEY_USERNAME, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_name(String user_name) {
        mEdittor.putString(KEY_USERNAME, user_name);
        mEdittor.commit(); //저장 후 완료한다.//
    }

    public String get_user_id() {
        return mPrefs.getString(KEY_USERID, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_id(String user_id) {
        mEdittor.putString(KEY_USERID, user_id);
        mEdittor.commit(); //저장 후 완료한다.//
    }

    public String get_user_email() {
        return mPrefs.getString(KEY_USEREMAIL, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_email(String user_email) {
        mEdittor.putString(KEY_USEREMAIL, user_email);
        mEdittor.commit(); //저장 후 완료한다.//
    }

    public String get_user_profileimageurl() {
        return mPrefs.getString(KEY_PROFILEIMAGEURL, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_profileimageurl(String user_profileimageurl) {
        mEdittor.putString(KEY_PROFILEIMAGEURL, user_profileimageurl);
        mEdittor.commit(); //저장 후 완료한다.//
    }

    public String get_user_gender() {
        return mPrefs.getString(KEY_USERGENDER, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_gender(String user_gender) {
        mEdittor.putString(KEY_USERGENDER, user_gender);
        mEdittor.commit(); //저장 후 완료한다.//
    }

    public String get_user_facebookid() {
        return mPrefs.getString(KEY_FACEBOOK_ID, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_facebookid(String user_facebookid) {
        mEdittor.putString(KEY_FACEBOOK_ID, user_facebookid);
        mEdittor.commit(); //저장 후 완료한다.//
    }

    public String get_user_fcmtoken() {
        return mPrefs.getString(KEY_FCM_REG_ID, ""); //만약에 프래퍼런스가 없을 경우 ""로 나온다.//
    }

    public void set_user_fcmtoken(String user_fcmtoken) {
        mEdittor.putString(KEY_FCM_REG_ID, user_fcmtoken);
        mEdittor.commit(); //저장 후 완료한다.//
    }
}
