package com.example.apple.test_app;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.apple.test_app.data.jsondata.profile.ProfileRequest;
import com.example.apple.test_app.manager.datamanager.PropertyManager;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {
    Handler mHandler;

    NetworkManager networkManager;
    //id값//
    String get_user_id;
    private ProgressDialog pDialog;
    private Callback requestprofilecallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (this != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this);
                        alertDialog.setTitle("Login")
                                .setMessage("요청에러 (네트워크 상태를 점검해주세요.)")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }
                });
            }
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            final String responseData = response.body().string();

            Log.d("json data", responseData);

            //현재 보안을 위해서 우선은 공유저장소에 들어있는 값(현재 스마트폰의 저장 정보)과 서버에 저장되어 있는 값을 비교//
            //만약 다를 시 해커가 우회에서 들어올 수 있으므로 로그인 화면으로 이동하여 다시 정상적으로 토큰등을 발급받게 함.//
            Gson gson = new Gson();

            final ProfileRequest profileRequest = gson.fromJson(responseData, ProfileRequest.class);

            String s_user_id = profileRequest.getResult().getUser_id();

            if (s_user_id.equals(get_user_id)) //동일하다면 현재 사용자가 로그인상태인지 로그아웃인지 비교해서 메인, 로그인 화면으로 이동//
            {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            String login_auth = profileRequest.getResult().getAuth_login();

                            if (login_auth.equals("1")) //로그인한 경우//
                            {
                                //메인화면으로 이동하기 전 공유 프래퍼런스의 내용을 재설정한다.(최신의 정보 유지)//
                                PropertyManager.getInstance().set_user_name(profileRequest.getResult().getUser_name());
                                PropertyManager.getInstance().set_user_profileimageurl(profileRequest.getResult().getUser_profileimageurl());
                                PropertyManager.getInstance().set_user_gender(profileRequest.getResult().getUser_gender());
                                PropertyManager.getInstance().set_user_email(profileRequest.getResult().getUser_email());
                                PropertyManager.getInstance().set_user_id(profileRequest.getResult().getUser_id());

                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);

                                startActivity(intent);

                                finish();
                            } else if (login_auth.equals("0")) //로그아웃 상태.//
                            {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                                startActivity(intent);

                                finish();
                            }
                        }
                    });
                }
            } else {
                hidepDialog();

                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

                startActivity(intent);

                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler = new Handler(Looper.getMainLooper());

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        //배지 카운터 설정(초기화)(기기별 호환문제)//
        Intent i = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        i.putExtra("badge_count", 0); //다시 배지카운터를 0으로 초기화.//
        i.putExtra("badge_count_package_name", getApplicationContext().getPackageName());
        i.putExtra("badge_count_class_name", SplashActivity.class.getName());

        //변경된 값으로 다시 공유 저장소 값 초기화.//
        PropertyManager.getInstance().setBadge_number(0);

        sendBroadcast(i); //브로드캐스트를 이용.//

        Auto_Login(); //자동로그인//
    }

    public void Auto_Login() {
        /** 자동로그인 처리 매커니즘 **/
        runOnUiThread(new Runnable() {
            public void run() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //최초 앱을 실행 시 로그인이 되어있는지 검사//
                        showpDialog();

                        is_login_check();
                    }
                }, 1500);
            }
        });
    }

    public void is_login_check() {
        //첫번째 비교로 현재 공유저장소에 값이 있는지 유무를 검사//
        get_user_id = PropertyManager.getInstance().get_user_id();

        if (get_user_id.equals("")) //존재하지 않는 경우//
        {
            Log.d("json control", "not user id");

            //로그인 페이지로 바로 이동//
            hidepDialog();

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);

            startActivity(intent);

            finish();
        } else {
            Log.d("json control", "user id:" + get_user_id);

            //서버에 저장되어있는 프로필 정보를 불러온다. 불러온 정보와 현재 공유저장소에 정보가 일치한지와 로그인 유무를 검사한다.//
            get_Profile(get_user_id);
        }
    }

    public void get_Profile(String get_user_id) {
        /** Networok 설정 **/
        networkManager = NetworkManager.getInstance();

        OkHttpClient client = networkManager.getClient();

        /** POST방식의 프로토콜 요청 설정 **/
        /** URL 설정 **/
        HttpUrl.Builder builder = new HttpUrl.Builder();

        builder.scheme("http"); //스킴정의(Http / Https)//
        builder.host(getResources().getString(R.string.server_domain)); //host정의.//
        builder.port(8080);
        builder.addPathSegment("DummyServer_Blog");
        builder.addPathSegment("LoginInfo");
        builder.addPathSegment("profile.jsp");

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("user_id", get_user_id.toString());

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(SplashActivity.this)
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestprofilecallback);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
