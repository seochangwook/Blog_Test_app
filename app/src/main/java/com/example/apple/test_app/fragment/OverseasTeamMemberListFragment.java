package com.example.apple.test_app.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apple.test_app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverseasTeamMemberListFragment extends Fragment {
    String search_value = ""; //기본 빈 문자열//

    public OverseasTeamMemberListFragment() {
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
        View view = inflater.inflate(R.layout.fragment_overseas_team_member_list, container, false);

        return view;
    }

    //메뉴관련 콜백 메소드 정의//
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        getActivity().getMenuInflater().inflate(R.menu.overseas_saleteam_info_menu, menu);

        MenuItem item = menu.findItem(R.id.searchview_item); //SearchView의 자원을 얻어온다.//

        SearchView searchview = (SearchView) MenuItemCompat.getActionView(item);

        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_value = query;

                Toast.makeText(getActivity(), "멤버 검색어: " + search_value, Toast.LENGTH_SHORT).show();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }
}
