package com.example.apple.test_app.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.test_app.LoginActivity;
import com.example.apple.test_app.R;
import com.example.apple.test_app.data.jsondata.logout.LogoutRequest;
import com.example.apple.test_app.manager.datamanager.PropertyManager;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class Option1Fragment extends Fragment {
    ImageButton logout_button;
    TextView name_textview;
    TextView email_textview;
    TextView gender_textview;
    Button email_link_button;
    Button kakaotalk_link_button;

    /**
     * Facebook 관련 변수
     **/
    LoginManager mLoginManager;
    NetworkManager networkManager;
    private ProgressDialog pDialog;
    private Callback requestlogoutcallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (this != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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

            Gson gson = new Gson();

            LogoutRequest logoutRequest = gson.fromJson(responseData, LogoutRequest.class);

            boolean is_success = logoutRequest.getResult().getIs_success();

            if (is_success == true) {
                if (this != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            Toast.makeText(getActivity(), "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();

                            mLoginManager.logOut();

                            //성공, 실패 유무를 판단//
                            Intent intnet = new Intent(getActivity(), LoginActivity.class);

                            startActivity(intnet);

                            getActivity().finish();
                        }
                    });
                }
            } else if (is_success == false) {
                if (this != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("People Search")
                                    .setMessage("로그아웃 에러")
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

    public Option1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //로그인 매니저가 연동될려면 SDK초기화가 필요//
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_option1, container, false);

        mLoginManager = LoginManager.getInstance(); //로그인 매니저 등록(현재 로그인/로그아웃 상태를 얻어온다.)//

        logout_button = (ImageButton) view.findViewById(R.id.logout_button);
        name_textview = (TextView) view.findViewById(R.id.name_textview);
        email_textview = (TextView) view.findViewById(R.id.email_textview);
        gender_textview = (TextView) view.findViewById(R.id.gender_textview);
        email_link_button = (Button) view.findViewById(R.id.email_link_button);
        kakaotalk_link_button = (Button) view.findViewById(R.id.kakaotalk_link_button);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        //공유저장소로부터 사용자의 정보를 불러온다.//
        name_textview.setText(PropertyManager.getInstance().get_user_name());
        email_textview.setText(PropertyManager.getInstance().get_user_email());

        if (PropertyManager.getInstance().get_user_gender().equals("male")) {
            gender_textview.setText(PropertyManager.getInstance().get_user_gender() + " (남성)");
        } else if (PropertyManager.getInstance().get_user_gender().equals("female")) {
            gender_textview.setText(PropertyManager.getInstance().get_user_gender() + " (여성)");
        }

        email_link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //지메일의 패키지 정보를 불러온다.//
                PackageManager pm = getActivity().getPackageManager();
                String packageName = "com.google.android.gm";

                ApplicationInfo ai = null;

                try {
                    ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String labelName = pm.getApplicationLabel(ai).toString();

                Intent intent = pm.getLaunchIntentForPackage(packageName);

                startActivity(intent);
            }
        });

        kakaotalk_link_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //카카오톡의 패키지 정보를 불러온다.//
                PackageManager pm = getActivity().getPackageManager();
                String packageName = "com.kakao.talk";

                ApplicationInfo ai = null;

                try {
                    ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                String labelName = pm.getApplicationLabel(ai).toString();

                Intent intent = pm.getLaunchIntentForPackage(packageName);

                startActivity(intent);
            }
        });

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃 과정을 수행//
                //네트워크로 로그아웃 수행//
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Human Management")
                        .setMessage("정말로 로그아웃 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("로그아웃",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //yes
                                        //네트워크로 데이터를 보낸다.//
                                        showpDialog();

                                        Logout();
                                    }
                                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //no
                    }
                });

                AlertDialog alert = alertDialog.create();
                alert.show();
            }
        });

        return view;
    }

    public void Logout() {
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
        builder.addPathSegment("logout.jsp");

        //유저의 id를 가져온다.//
        String user_id = PropertyManager.getInstance().get_user_id();

        Log.d("json data: ", user_id.toString());

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("user_id", user_id.toString());

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(getActivity())
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestlogoutcallback);
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
