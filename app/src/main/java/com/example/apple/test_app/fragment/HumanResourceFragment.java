package com.example.apple.test_app.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.test_app.R;
import com.example.apple.test_app.data.HumanData;
import com.example.apple.test_app.view.LoadMoreView;
import com.example.apple.test_app.widget.HumanListAdapter;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HumanResourceFragment extends Fragment {
    private final static int LOAD_MORE_TAG = 1;
    Button developeroneteamlist_button;
    Button developertwoteamlist_button;
    TextView what_select_list_info_text;
    /**
     * 어댑터, 데이터
     **/
    HumanListAdapter humanListAdapter;
    HumanData humanData;
    /**
     * List Flag
     **/
    int list_flag = 0; //기본 0//
    private FamiliarRefreshRecyclerView human_list;
    private FamiliarRecyclerView recyclerview;

    public HumanResourceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_human_resource, container, false);

        human_list = (FamiliarRefreshRecyclerView) view.findViewById(R.id.human_info_list);

        /** RecyclerView 정의 **/
        //RefreshRecyclerView 기능설정.//
        human_list.setId(android.R.id.list);
        human_list.setLoadMoreView(new LoadMoreView(getActivity(), LOAD_MORE_TAG));
        human_list.setColorSchemeColors(0xFFFF5000, Color.RED, Color.YELLOW, Color.GREEN);
        human_list.setLoadMoreEnabled(true);

        //RecyclerView의 특징을 RefreshRecyclerView에 연결//
        recyclerview = human_list.getFamiliarRecyclerView();
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        /** RecyclerView의 각종 뷰 정의(HeaderView, EmptyView, FooterView) **/
        View humanlist_headerview = LayoutInflater.from(getActivity()).inflate(R.layout.humanlist_headerview, null);
        View humanlist_emptyview = LayoutInflater.from(getActivity()).inflate(R.layout.humanlist_emptyview, null);

        //해당 위젯들 정의.//
        developeroneteamlist_button = (Button) humanlist_headerview.findViewById(R.id.developeroneteamlist_button);
        developertwoteamlist_button = (Button) humanlist_headerview.findViewById(R.id.developertwoteamlist_button);
        what_select_list_info_text = (TextView) humanlist_headerview.findViewById(R.id.select_list_info_text);

        what_select_list_info_text.setVisibility(View.GONE);

        //리사이클뷰에 연결//
        recyclerview.addHeaderView(humanlist_headerview, true);
        recyclerview.setEmptyView(humanlist_emptyview, true);

        /** RecyclerView에 Adapter, 데이터 설정 **/
        humanData = new HumanData();
        humanListAdapter = new HumanListAdapter(getActivity());

        recyclerview.setAdapter(humanListAdapter);

        /** RecyclerView Item Click Listener **/
        human_list.setOnItemClickListener(new FamiliarRecyclerView.OnItemClickListener() {
            @Override
            public void onItemClick(FamiliarRecyclerView familiarRecyclerView, View view, int position) {
                String select_name = humanData.getHumanDataList().get(position).getHuman_name();

                Toast.makeText(getActivity(), "[" + select_name + "](" + position + ") 선택", Toast.LENGTH_SHORT).show();
            }
        });

        /** RecyclerView Refresh이벤트 처리(일반적으로 위에서 당기기기는 현 정보에서 갱신, 아래에서 로딩은 '더보기'기능) **/
        human_list.setOnPullRefreshListener(new FamiliarRefreshRecyclerView.OnPullRefreshListener() {
            @Override
            public void onPullRefresh() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("EVENT :", "새로고침 완료");

                        human_list.pullRefreshComplete();

                        //당겨서 새로고침할때의 작업 정의//
                        if (list_flag == 0) {
                            if (humanData.getHumanDataList().size() > 0) {
                                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                            }
                        } else if (list_flag == 1) //개발1팀 리스트 초기화//
                        {
                            if (humanData.getHumanDataList().size() > 0) {
                                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                            }

                            set_HumanDummyData(list_flag);
                        } else if (list_flag == 2) //개발2팀 리스트 초기화//
                        {
                            if (humanData.getHumanDataList().size() > 0) {
                                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                            }

                            set_HumanDummyData(list_flag);
                        }

                    }
                }, 1000);
            }
        });

        human_list.setOnLoadMoreListener(new FamiliarRefreshRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("EVENT :", "새로고침 완료");

                        human_list.loadMoreComplete();

                        //맨 아래일 시 '더보기' 작업(더보기 작업 생략 - 더미데이터로는 구현하지 않음)//
                        Toast.makeText(getActivity(), "더 이상 정보가 없습니다", Toast.LENGTH_SHORT).show();
                    }
                }, 1000);

            }
        });

        developeroneteamlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "개발1팀 리스트 정보출력", Toast.LENGTH_SHORT).show();

                what_select_list_info_text.setText("개발1팀 리스트정보");
                what_select_list_info_text.setVisibility(View.VISIBLE);
                list_flag = 1;

                set_HumanDummyData(list_flag);
            }
        });

        developertwoteamlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "개발2팀 리스트 정보출력", Toast.LENGTH_SHORT).show();

                what_select_list_info_text.setText("개발2팀 리스트정보");
                what_select_list_info_text.setVisibility(View.VISIBLE);
                list_flag = 2;

                set_HumanDummyData(list_flag);
            }
        });

        return view;
    }

    public void init_HumanListData() {
        if (humanData.getHumanDataList().size() > 0) {
            humanData.getHumanDataList().clear(); //리스트 정보 초기화//

            humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
        }
    }

    public void set_HumanDummyData(int list_flag) {
        init_HumanListData(); //정보를 우선 초기화//

        //각 10개의 더미 데이터 설정.//
        if (list_flag == 1) //개발1팀 정보//
        {
            for (int i = 0; i < 10; i++) {
                HumanData new_humandata = new HumanData();

                new_humandata.setHuman_id(1);
                new_humandata.setHuman_address("경기도 수원시 장안구");
                new_humandata.setHuman_age(25);
                new_humandata.setHuman_imageresource_id(R.mipmap.ic_launcher);
                new_humandata.setHuman_name("서창욱");
                new_humandata.setHuman_job("대학생");
                new_humandata.setHuman_tel("010-xxxx-xxxx");

                //데이터 클래스에 생성된 데이터 정보를 저장//
                humanData.getHumanDataList().add(new_humandata);
            }

            //최종적으로 완성된 데이터클래스를 어댑터로 할당//
            humanListAdapter.set_HumanData(humanData);
        } else if (list_flag == 2) {
            for (int i = 0; i < 10; i++) {
                HumanData new_humandata = new HumanData();

                new_humandata.setHuman_id(2);
                new_humandata.setHuman_address("경기도 수원시 팔달구");
                new_humandata.setHuman_age(25);
                new_humandata.setHuman_imageresource_id(R.mipmap.ic_launcher);
                new_humandata.setHuman_name("홍길동");
                new_humandata.setHuman_job("직장인");
                new_humandata.setHuman_tel("010-xxxx-xxxx");

                //데이터 클래스에 생성된 데이터 정보를 저장//
                humanData.getHumanDataList().add(new_humandata);
            }

            //최종적으로 완성된 데이터클래스를 어댑터로 할당//
            humanListAdapter.set_HumanData(humanData);
        }
    }
}
