package com.example.apple.test_app;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.apple.test_app.data.jsondata.login.LoginRequest;
import com.example.apple.test_app.manager.datamanager.PropertyManager;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.example.apple.test_app.service.fcm.QuickstartPreferences;
import com.example.apple.test_app.service.fcm.RegistrationIntentService;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    /**
     * FCM관련 변수
     **/
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "LoginActivity";
    //커스텀 로그인을 위한 버튼 생성//
    ImageButton facebook_login_button; //로그인 버튼 커스텀//
    /**
     * Facebook 관련 변수
     **/
    LoginManager mLoginManager;
    NetworkManager networkManager;
    String access_token;
    String register_id;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private CallbackManager callbackManager; //세션연결 콜백관리자.//
    private ProgressDialog pDialog;
    private Callback requestlogincallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (this != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                        alertDialog.setTitle("People Search")
                                .setMessage("요청에러 (네트워크 상태를 점검해주세요.)")
                                .setCancelable(false)
                                .setPositiveButton("확인",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                mLoginManager.logOut(); //로그아웃(처음 로그인 액티비티로 왔을 시 로그인을 해야되는 상황이므로 로그아웃을 확실히 해준다.).//
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

            //성공, 실패 유무를 판단//
            Gson gson = new Gson();

            final LoginRequest loginRequest = gson.fromJson(responseData, LoginRequest.class);

            String is_success = loginRequest.getIs_success();

            if (is_success.equals("true/insert")) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            Log.d("json control", "insert user info");

                            //정보저장 전 화면으로 진입을 하면 공유저장소 초기화//
                            PropertyManager.getInstance().set_user_id("");
                            PropertyManager.getInstance().set_user_email("");
                            PropertyManager.getInstance().set_user_facebookid("");
                            PropertyManager.getInstance().set_user_fcmtoken("");
                            PropertyManager.getInstance().set_user_gender("");
                            PropertyManager.getInstance().set_user_profileimageurl("");
                            PropertyManager.getInstance().set_user_name("");

                            //해당 파싱된 정보를 공유 저장소에 저장//
                            PropertyManager.getInstance().set_user_id(loginRequest.getResult().getUser_id());
                            PropertyManager.getInstance().set_user_email(loginRequest.getResult().getUser_email());
                            PropertyManager.getInstance().set_user_facebookid(access_token);
                            PropertyManager.getInstance().set_user_fcmtoken(register_id);
                            PropertyManager.getInstance().set_user_gender(loginRequest.getResult().getUser_gender());
                            PropertyManager.getInstance().set_user_profileimageurl(loginRequest.getResult().getUser_profileimageurl());
                            PropertyManager.getInstance().set_user_name(loginRequest.getResult().getUser_name());

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            startActivity(intent);

                            finish();
                        }
                    });
                }
            } else if (is_success.equals("true/update")) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            Log.d("json control", "update user info");

                            //(이미 정보 존재 시 수정만 해준다.)공유저장소에 등록될 수정될 내용은 토큰값과 fcm값만 바꾸어 준다.//
                            PropertyManager.getInstance().set_user_facebookid(access_token);
                            PropertyManager.getInstance().set_user_fcmtoken(register_id);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                            startActivity(intent);

                            finish();
                        }
                    });
                }
            } else if (is_success.equals("false")) {
                if (this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                            alertDialog.setTitle("People Search")
                                    .setMessage("로그인 실패 (다시 접속해주세요)")
                                    .setCancelable(false)
                                    .setPositiveButton("확인",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    mLoginManager.logOut(); //로그아웃(처음 로그인 액티비티로 왔을 시 로그인을 해야되는 상황이므로 로그아웃을 확실히 해준다.).//
                                                }
                                            });

                            AlertDialog alert = alertDialog.create();
                            alert.show();
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //페이스북 SDK정보 초기화//
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        registBroadcastReceiver(); //FCM토큰값을 얻는다.//

        setContentView(R.layout.activity_login);

        facebook_login_button = (ImageButton) findViewById(R.id.facebook_login_button);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        callbackManager = CallbackManager.Factory.create(); //onActivityResult설정.//
        mLoginManager = LoginManager.getInstance(); //로그인 매니저 등록//

        mLoginManager.logOut(); //로그아웃(처음 로그인 액티비티로 왔을 시 로그인을 해야되는 상황이므로 로그아웃을 확실히 해준다.).//

        facebook_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLogin())  //로그인이 되어있으면 다시 로그인을 할 수 없다.//
                {
                    Toast.makeText(LoginActivity.this, "이미 로그인 되어있습니다. 로그아웃 해주세요", Toast.LENGTH_SHORT).show();
                } else {

                    showpDialog();

                    getInstanceIdToken(); //FCM ID값 획득//
                }
            }
        });

        //HashKey 출력//
        printKeyHash();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_READY));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_GENERATING));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    /**
     * 앱이 화면에서 사라지면 등록된 LocalBoardcast를 모두 삭제한다.
     */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * Instance ID를 이용하여 디바이스 토큰을 가져오는 RegistrationIntentService를 실행한다.
     */
    public void getInstanceIdToken() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void registBroadcastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();


                if (action.equals(QuickstartPreferences.REGISTRATION_READY)) {
                    // 액션이 READY일 경우

                } else if (action.equals(QuickstartPreferences.REGISTRATION_GENERATING)) {
                    // 액션이 GENERATING일 경우

                } else if (action.equals(QuickstartPreferences.REGISTRATION_COMPLETE)) {
                    // 액션이 COMPLETE일 경우
                    String token = intent.getStringExtra("token");
                    register_id = token;

                    Log.d("json token fcm id : ", register_id);

                    //토큰을 받은 이 후 로그인을 진행한다.//
                    //토큰을 받지 못하면 로그인 과정을 진행하지 않는다.//
                    loginFacebook();
                }
            }
        };
    }

    /**
     * Google Play Service를 사용할 수 있는 환경이지를 체크한다.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void loginFacebook() {
        mLoginManager.setDefaultAudience(DefaultAudience.FRIENDS);
        mLoginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);

        //콜백등록.//
        mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Access Token값을 가져온다.//
                AccessToken accessToken = AccessToken.getCurrentAccessToken();

                access_token = accessToken.getToken();

                Log.d("json control(new token) : ", access_token);

                //해당 토큰값을 서버로 전송한다.//
                Facebook_LoginServer(); //페이스북 로그인//

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //기존 제공해주는 로그인 버튼으로도 가능.//
        mLoginManager.logInWithReadPermissions(LoginActivity.this, Arrays.asList("email")); //이메일 획득 권한//
    }

    private boolean isLogin() {
        AccessToken token = AccessToken.getCurrentAccessToken();

        Log.d("json control(old token): ", "" + token);

        return token != null; //로그인 구분.//
    }

    public void Facebook_LoginServer() {
        //네트워크 설정//
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
        builder.addPathSegment("login.jsp");

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("accessToken", access_token.toString())
                .add("registrationToken", register_id.toString());

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(LoginActivity.this)
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestlogincallback);
    }

    //인증에 대한 결과를 받는다.//
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** Facebook 로그인 관련 **/
        //등록이 되어있어야지 정상적으로 onSuccess에서 정보를 받아온다.//
        callbackManager.onActivityResult(requestCode, resultCode, data); //

        Log.d("myLog", "requestCode  " + requestCode);
        Log.d("myLog", "resultCode" + resultCode);
        Log.d("myLog", "data  " + data.toString());
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName().toString(), PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                //Log.d("json KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
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
