package com.example.apple.test_app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.apple.test_app.R;
import com.example.apple.test_app.manager.datamanager.PropertyManager;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * A simple {@link Fragment} subclass.
 */
public class DrawerFragment extends Fragment {
    ImageView people_imageview;
    Button option_1_button;
    Button option_2_button;

    /**
     * 로그인 관련 클래스
     **/
    NetworkManager networkManager;

    String profile_imageurl;

    public DrawerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);

        people_imageview = (ImageView) view.findViewById(R.id.people_imageview);
        option_1_button = (Button) view.findViewById(R.id.option_button_1);
        option_2_button = (Button) view.findViewById(R.id.option_button_2);

        //공유 저장소에서 현재 프로필 이미지를 받아온다.//
        profile_imageurl = PropertyManager.getInstance().get_user_profileimageurl();
        networkManager = NetworkManager.getInstance();

        /** Picasso 방법 **/
        Picasso picasso = networkManager.getPicasso(); //피카소의 자원을 불러온다.//

        picasso.load(profile_imageurl)
                .placeholder(R.mipmap.ic_folder_x) //로딩 중 이미지//
                .error(R.mipmap.ic_folder_x) //이미지 다운 에러 시 이미지//
                .transform(new CropCircleTransformation()) //이미지를 변형(원형자르기)//
                .fit() //ImageView의 사이즈를 맞추어준다.//
                .into(people_imageview);

        //처음 첫번째 옵션 화면으로 셋팅//
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment, new Option1Fragment()
        ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

        option_1_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "화면 1 전환", Toast.LENGTH_SHORT).show();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment, new Option1Fragment()
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        option_2_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "화면 2 전환", Toast.LENGTH_SHORT).show();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_fragment, new Option2Fragment()
                ).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        return view;
    }
}
