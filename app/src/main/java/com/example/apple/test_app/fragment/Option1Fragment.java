package com.example.apple.test_app.fragment;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.test_app.LoginActivity;
import com.example.apple.test_app.R;
import com.example.apple.test_app.manager.datamanager.PropertyManager;
import com.facebook.login.LoginManager;

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

    public Option1Fragment() {
        // Required empty public constructor
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

        //공유저장소로부터 사용자의 정보를 불러온다.//
        name_textview.setText(PropertyManager.getInstance().get_user_name());
        email_textview.setText(PropertyManager.getInstance().get_user_email());

        if (PropertyManager.getInstance().get_user_gender().equals("male")) {
            gender_textview.setText(PropertyManager.getInstance().get_user_gender() + " (남성)");
        } else if (PropertyManager.getInstance().get_user_gender().equals("female")) {
            gender_textview.setText(PropertyManager.getInstance().get_user_gender() + " (여성)");
        }

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

                mLoginManager.logOut();

                Toast.makeText(getActivity(), "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();

                Intent intnet = new Intent(getActivity(), LoginActivity.class);

                startActivity(intnet);

                getActivity().finish();
            }
        });

        return view;
    }
}
