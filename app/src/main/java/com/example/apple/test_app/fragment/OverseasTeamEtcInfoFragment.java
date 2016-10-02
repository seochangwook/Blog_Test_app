package com.example.apple.test_app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apple.test_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverseasTeamEtcInfoFragment extends Fragment {
    String search_value = ""; //기본 빈 문자열//

    public OverseasTeamEtcInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_overseas_team_etc_info, container, false);

        //메뉴변경 옵션허용//
        setHasOptionsMenu(true);

        return view;
    }
}
