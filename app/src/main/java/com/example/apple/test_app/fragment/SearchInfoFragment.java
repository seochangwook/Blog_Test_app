package com.example.apple.test_app.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.test_app.R;
import com.example.apple.test_app.data.jsondata.UserInfoRequest;
import com.example.apple.test_app.data.jsondata.UserInfoRequestResult;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

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
public class SearchInfoFragment extends Fragment {
    private static final String KEY_VALUE = "KEY_VALUE";

    TextView search_textview;
    String search_value = "";

    TextView name_textview;
    TextView department_name_textview;
    TextView introduction_textview;
    TextView etc_textview;
    ImageView people_imageview;

    NetworkManager networkManager;

    private ProgressDialog pDialog;
    private Callback requestuserinfocallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //네트워크 자체에서의 에러상황.//
            Log.d("ERROR Message : ", e.getMessage());

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hidepDialog();

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle("People Search")
                                .setMessage("사용자 정보를 찾을 수 없습니다. 다시 입력해주세요 (네트워크 상태를 점검해주세요.)")
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

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "사용자 검색 완료", Toast.LENGTH_SHORT).show();

                        //GSON으로 자동 파싱적용//
                        Gson gson = new Gson();

                        UserInfoRequest userInfoRequest = gson.fromJson(responseData, UserInfoRequest.class);

                        setUserInfo(userInfoRequest.getResult());

                        hidepDialog();
                    }
                });
            }
        }
    };

    public SearchInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        //값을 전달받는다.//
        Bundle b = getArguments();
        search_value = b.getString(KEY_VALUE);

        Log.d("data trans: ", search_value);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_info, container, false);

        search_textview = (TextView) view.findViewById(R.id.search_value_textview);
        name_textview = (TextView) view.findViewById(R.id.search_name_textview);
        department_name_textview = (TextView) view.findViewById(R.id.department_name_textview);
        introduction_textview = (TextView) view.findViewById(R.id.introduction_textview);
        etc_textview = (TextView) view.findViewById(R.id.etc_textview);
        people_imageview = (ImageView) view.findViewById(R.id.search_people_imageview);

        search_textview.setText(search_value.toString());

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        //네트워크로 정보를 불러온다.//
        showpDialog();

        getUserInfo();

        return view;
    }

    public void getUserInfo() {
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
        builder.addPathSegment("userinfo.jsp");

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("name", search_value);

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(getActivity())
                .build();

        Log.d("json data: ", "" + request.url());
        Log.d("json data: ", "" + request.method());
        Log.d("json data: ", "" + formBuilder.build().value(0));

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestuserinfocallback);
    }

    public void setUserInfo(UserInfoRequestResult userInfoRequestResult) {
        name_textview.setText(userInfoRequestResult.getName());
        department_name_textview.setText(userInfoRequestResult.getDepartment());
        introduction_textview.setText(userInfoRequestResult.getIntroduction());
        etc_textview.setText(userInfoRequestResult.getEtc());

        String imageUrl = userInfoRequestResult.getImage();

        //이미지 로드//
        /** Picasso 방법 **/
        Picasso picasso = networkManager.getPicasso(); //피카소의 자원을 불러온다.//

        picasso.load(imageUrl)
                .placeholder(R.mipmap.ic_folder_x) //로딩 중 이미지//
                .error(R.mipmap.ic_folder_x) //이미지 다운 에러 시 이미지//
                .fit() //ImageView의 사이즈를 맞추어준다.//
                .into(people_imageview);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        if (item_id == R.id.help_menuitem) {
            Toast.makeText(getActivity(), "도움말 버튼", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
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
