package com.example.apple.test_app.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apple.test_app.HumanAddActivity;
import com.example.apple.test_app.R;
import com.example.apple.test_app.data.jsondata.usercrud.UserListRequest;
import com.example.apple.test_app.data.jsondata.usercrud.UserListRequestResultsHumanList;
import com.example.apple.test_app.data.viewdata.HumanData;
import com.example.apple.test_app.manager.networkmanager.NetworkManager;
import com.example.apple.test_app.view.LoadMoreView;
import com.example.apple.test_app.widget.HumanListAdapter;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;
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
public class HumanResourceFragment extends Fragment {
    private final static int LOAD_MORE_TAG = 1;
    private final static String KEY_DEPARTMENTNAME = "KEY_DEPARTMENTNAME";
    View view;
    Button developeroneteamlist_button;
    Button managementteamlist_button;
    TextView what_select_list_info_text;
    FloatingActionButton topup_button;
    /**
     * 어댑터, 데이터
     **/
    HumanListAdapter humanListAdapter;
    HumanData humanData;
    /**
     * List Flag
     **/
    int list_flag = 0; //기본 0//
    int flag_department = 0; //0이면 개발팀 / 1이면 경영팀//
    /**
     * Scroll처리 관련 변수
     **/
    float startYPosition = 0; //기본적으로 스크롤은 Y축을 기준으로 계산.//
    float endYPosition = 0;
    boolean firstDragFlag = true;
    boolean motionFlag = true;
    boolean dragFlag = false; //현재 터치가 드래그인지 먼저 확인//
    NetworkManager networkManager;
    ImageButton humanadd_button;
    /**
     * Popup관련 변수
     **/
    PopupWindow helper_popup; //팝업//
    View helper_popupview;
    ImageButton helper_option_1;
    ImageButton helper_option_2;
    ImageButton helper_option_3;
    private FamiliarRefreshRecyclerView human_list;
    private FamiliarRecyclerView recyclerview;
    private ProgressDialog pDialog;
    private Callback requestuserlistcallback = new Callback() {
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
                                .setMessage("요청에러 (네트워크 상태를 점검해주세요.)")
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

            Gson gson = new Gson();

            UserListRequest userlistRequest = gson.fromJson(responseData, UserListRequest.class);

            int list_count = Integer.parseInt(userlistRequest.getCount()); //리스트의 개수 반환//

            if (list_count == 0) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hidepDialog();

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("People Search")
                                    .setMessage("인사기록 정보가 없습니다. 사원을 등록해주세요")
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
            } else if (list_count > 0) {
                set_Humandata(userlistRequest.getResults().getHumanList(), userlistRequest.getResults().getHumanList().length);
            }
        }
    };

    public HumanResourceFragment() {
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
        view = inflater.inflate(R.layout.fragment_human_resource, container, false);

        human_list = (FamiliarRefreshRecyclerView) view.findViewById(R.id.human_info_list);
        topup_button = (FloatingActionButton) view.findViewById(R.id.scroll_up_fabbutton);

        topup_button.setVisibility(View.GONE); //처음에 위로가기 버튼을 보이지 않는다.//

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

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

        /** RecyclerView의 각종 뷰 정의(HeaderView, EmptyView, FooterView) **/
        View humanlist_headerview = LayoutInflater.from(getActivity()).inflate(R.layout.humanlist_headerview, null);
        View humanlist_emptyview = LayoutInflater.from(getActivity()).inflate(R.layout.humanlist_emptyview, null);

        //해당 위젯들 정의.//
        developeroneteamlist_button = (Button) humanlist_headerview.findViewById(R.id.developeroneteamlist_button);
        managementteamlist_button = (Button) humanlist_headerview.findViewById(R.id.developertwoteamlist_button);
        what_select_list_info_text = (TextView) humanlist_headerview.findViewById(R.id.select_list_info_text);
        humanadd_button = (ImageButton) humanlist_headerview.findViewById(R.id.humanadd_button);

        humanadd_button.setVisibility(View.GONE);

        what_select_list_info_text.setVisibility(View.GONE);

        //리사이클뷰에 연결//
        recyclerview.addHeaderView(humanlist_headerview, true);
        recyclerview.setEmptyView(humanlist_emptyview, true);

        /** RecyclerView에 Adapter, 데이터 설정 **/
        humanData = new HumanData();
        humanListAdapter = new HumanListAdapter(getActivity());

        recyclerview.setAdapter(humanListAdapter);

        /** 사람 추가 버튼 **/
        humanadd_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //개발팀인지 경영팀인지 구분//
                String departmentname = "";

                if (flag_department == 0) {
                    departmentname = "Developement part";
                } else if (flag_department == 1) {
                    departmentname = "Management part";
                }

                Intent intent = new Intent(getActivity(), HumanAddActivity.class);

                intent.putExtra(KEY_DEPARTMENTNAME, departmentname);

                startActivity(intent); //따로 반환값을 등록하지 않아도 되는 이유는 프래그먼트의 생애주기인 onResume()을 사용//
            }
        });

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
                        } else if (list_flag == 1) //개발팀 리스트 초기화//
                        {
                            if (humanData.getHumanDataList().size() > 0) {
                                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                            }

                            get_HumanData(list_flag);
                        } else if (list_flag == 2) //경영팀 리스트 초기화//
                        {
                            if (humanData.getHumanDataList().size() > 0) {
                                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                            }

                            get_HumanData(list_flag);
                        }

                    }
                }, 1000);
            }
        });

        /** RecyclerView의 Scroll Event처리 **/
        recyclerview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE: {
                        dragFlag = true;

                        //사용자는 보통 한 번 터치 후 내리거나 올리는 작업을 하기에 반복작업을 피하기 위해서 true/false로 한번만 되도록 구현.//
                        if (firstDragFlag) //첫번째 움직임을 가지고 판단하기 위해서//
                        {
                            startYPosition = motionEvent.getY();
                            firstDragFlag = false;
                        }

                        break;
                    }

                    case MotionEvent.ACTION_UP: {
                        endYPosition = motionEvent.getY();
                        firstDragFlag = true;

                        if (dragFlag) {
                            // 시작Y가 끝 Y보다 크다면 터치가 아래서 위로 이루어졌다는 것이고, 스크롤은 아래로내려갔다는 뜻이다.
                            if ((startYPosition > endYPosition) && (startYPosition - endYPosition) > 10) {
                                Log.d("data : ", "scroll down");

                                topup_button.setVisibility(View.VISIBLE);
                            }

                            //시작 Y가 끝 보다 작다면 터치가 위에서 아래로 이러우졌다는 것이고, 스크롤이 올라갔다는 뜻이다.
                            else if ((startYPosition < endYPosition) && (endYPosition - startYPosition) > 10) {
                                Log.d("data : ", "scroll up");

                                topup_button.setVisibility(View.GONE);
                            }
                        }

                        //다시 Y축에 대한 위치를 초기화.//
                        startYPosition = 0.0f;
                        endYPosition = 0.0f;
                        motionFlag = false;

                        break;
                    }
                }

                return false;
            }
        });

        /** 스크롤 관련 이벤트 처리 **/
        topup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerview.smoothScrollToPosition(0); //리스트의 맨 처음으로 이동//

                topup_button.setVisibility(View.GONE);
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
                Toast.makeText(getActivity(), "개발팀 리스트 정보출력", Toast.LENGTH_SHORT).show();

                humanadd_button.setVisibility(View.VISIBLE);
                flag_department = 0;

                what_select_list_info_text.setText("개발팀 리스트정보");
                what_select_list_info_text.setVisibility(View.VISIBLE);
                list_flag = 1;

                //set_HumanDummyData(list_flag);
                showpDialog();
                //네트워크로 데이터를 불러온다.//

                if (humanData.getHumanDataList().size() > 0) {
                    humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                    humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                }

                get_HumanData(list_flag);
            }
        });

        managementteamlist_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "경영팀 리스트 정보출력", Toast.LENGTH_SHORT).show();

                humanadd_button.setVisibility(View.VISIBLE);
                flag_department = 1;

                what_select_list_info_text.setText("경영팀 리스트정보");
                what_select_list_info_text.setVisibility(View.VISIBLE);
                list_flag = 2;

                //set_HumanDummyData(list_flag);

                //네트워크로 데이터를 불러온다.//

                if (humanData.getHumanDataList().size() > 0) {
                    humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                    humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
                }

                get_HumanData(list_flag);
            }
        });

        return view;
    }

    public void get_HumanData(int list_flag) {
        String condition_str = "";

        //부서는 고정이니 조건으로 설정//
        if (list_flag == 1)
        {
            condition_str = "Developement part";
        } else if (list_flag == 2) {
            condition_str = "Management part";
        }

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
        builder.addPathSegment("userlist.jsp");

        //Body설정//
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("departmentname", condition_str);

        /** RequestBody 설정(파일 전송 시 Multipart로 설정) **/
        RequestBody body = formBuilder.build();

        /** Request 설정 **/
        Request request = new Request.Builder()
                .url(builder.build())
                .post(body) //POST방식 적용.//
                .tag(getActivity())
                .build();

        /** 비동기 방식(enqueue)으로 Callback 구현 **/
        client.newCall(request).enqueue(requestuserlistcallback);
    }

    public void set_Humandata(final UserListRequestResultsHumanList[] humanList, final int length) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hidepDialog();

                    List<UserListRequestResultsHumanList> userlist = new ArrayList<>();

                    userlist.addAll(Arrays.asList(humanList));

                    for (int i = 0; i < length; i++) {
                        HumanData new_human = new HumanData();

                        new_human.setHuman_imageurl(userlist.get(i).getHuman_imageurl());
                        new_human.setHuman_job(userlist.get(i).getHuman_job());
                        new_human.setHuman_name(userlist.get(i).getHuman_name());
                        new_human.setHuman_address(userlist.get(i).getHuman_address());
                        new_human.setHuman_tel(userlist.get(i).getHuman_tel());
                        new_human.setHuman_age(Integer.parseInt(userlist.get(i).getHuman_age()));
                        new_human.setHuman_id(Integer.parseInt(userlist.get(i).getHuman_id()));
                        new_human.setHuman_department(userlist.get(i).getHuman_department());
                        new_human.setHuman_emailaddress(userlist.get(i).getHuman_emailaddress());
                        new_human.setHuman_etcinfo(userlist.get(i).getHuman_etcinfo());
                        new_human.setHuman_introduction(userlist.get(i).getHuman_introduction());

                        humanData.humanDataList.add(new_human);
                    }

                    humanListAdapter.set_HumanData(humanData);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Toast.makeText(getActivity(), "이미지가 보이지 않을 시 새로고침(당기기) 해주세요", Toast.LENGTH_SHORT).show();

        Log.d("json data :", "onresume");

        //수정이나 삭제가 되었을 시 다시 프래그먼트로 돌아오면 초기화//
        if (list_flag == 1) //개발팀 리스트 초기화//
        {
            if (humanData.getHumanDataList().size() > 0) {
                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
            }

            get_HumanData(list_flag); //데이터 Reload//
        } else if (list_flag == 2) //경영팀 리스트 초기화//
        {
            if (humanData.getHumanDataList().size() > 0) {
                humanData.getHumanDataList().clear(); //리스트 정보 초기화//

                humanListAdapter.set_HumanData(humanData); //초기화된 정보를 갱신//
            }

            get_HumanData(list_flag);
        }
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

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
