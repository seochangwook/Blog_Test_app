package com.example.apple.test_app.fragment;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.apple.test_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverseasTeamEtcInfoFragment extends Fragment {
    String search_value = ""; //기본 빈 문자열//

    /**
     * Popup관련 변수
     **/
    PopupWindow helper_popup; //팝업//
    View helper_popupview;

    ImageButton helper_option_1;
    ImageButton helper_option_2;
    ImageButton helper_option_3;


    public OverseasTeamEtcInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overseas_team_etc_info, container, false);

        /** Popup설정 **/
        helper_popupview = getActivity().getLayoutInflater().inflate(R.layout.helper_popup_layout, null);

        //팝업 뷰에 있는 위젯참조//
        helper_option_1 = (ImageButton) helper_popupview.findViewById(R.id.helper_option_1_button);
        helper_option_2 = (ImageButton) helper_popupview.findViewById(R.id.helper_option_2_button);
        helper_option_3 = (ImageButton) helper_popupview.findViewById(R.id.helper_option_3_button);

        //팝업창 설정.//
        helper_popup = new PopupWindow(helper_popupview, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        helper_popup.setTouchable(true);
        helper_popup.setOutsideTouchable(true);
        helper_popup.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        helper_popup.setAnimationStyle(R.style.PopupAnimationTop);
        helper_popup.getContentView().setFocusableInTouchMode(true);
        helper_popup.getContentView().setFocusable(true);

        //팝어업 위젯 이벤트 처리//
        helper_option_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "기능소개", Toast.LENGTH_SHORT).show();
            }
        });

        helper_option_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "개발정보", Toast.LENGTH_SHORT).show();
            }
        });

        helper_option_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "문의사항", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
            //getView()를 이용하여 현재의 뷰를 가져온다.(프래그먼트에서 상위 액티비티는 getActivity()이고, 현재의 뷰는 getView())//
            helper_popup.showAtLocation(getView(), Gravity.NO_GRAVITY, 320, 320);
        }

        return super.onOptionsItemSelected(item);
    }
}
