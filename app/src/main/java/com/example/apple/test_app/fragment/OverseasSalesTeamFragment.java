package com.example.apple.test_app.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.apple.test_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverseasSalesTeamFragment extends Fragment {
    FragmentTabHost tabHost;

    public OverseasSalesTeamFragment() {
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
        View view = inflater.inflate(R.layout.fragment_overseas_sales_team, container, false);

        tabHost = (FragmentTabHost) view.findViewById(R.id.tabhost);
        //Fragment에서 탭을 사용 시 탭 내부에 있는 프래그먼트는 기존 부모프래그먼트의 자원이 아닌 부모 프래그먼트에 속해있는
        //자식 프래그먼트의 자원을 얻어야 하므로 getChildFragmentManger()를 사용한다//

        tabHost.setup(getContext(), getChildFragmentManager(), android.R.id.tabcontent);

        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("Member List"),
                OverseasTeamMemberListFragment.class, null);

        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("Location"),
                OverseasTeamLocationFragment.class, null);

        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("Etc"),
                OverseasTeamEtcInfoFragment.class, null);

        return view;
    }
}
