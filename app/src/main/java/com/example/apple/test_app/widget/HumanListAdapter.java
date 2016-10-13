package com.example.apple.test_app.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apple.test_app.HumanInfoUpdate;
import com.example.apple.test_app.R;
import com.example.apple.test_app.data.viewdata.HumanData;
import com.example.apple.test_app.view.HumanListViewHolder;

/**
 * Created by apple on 2016. 9. 24..
 */
public class HumanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String KEY_HUMANNAME = "KEY_HUMANNAME";
    private static final String KEY_HUMANID = "KEY_HUMANID";
    private static final String KEY_HUMANIMAGEURL = "KEY_HUMANIMAGEURL";
    private static final String KEY_HUMANDEPARTMENT = "KEY_HUMANDEPARTMENT";
    private static final String KEY_HUMANADDRESS = "KEY_HUMANADDRESS";
    private static final String KEY_HUMANTEL = "KEY_HUMANTEL";

    private static final int RC_UPDATE = 200;

    //데이터 클래스 정의//
    HumanData humanData;

    //자원 클래스 정의//
    Context context;

    //생성자를 이용하여서 자원과 데이터 클래스의 초기화//
    public HumanListAdapter(Context context) {
        this.context = context;

        humanData = new HumanData();
    }

    public void set_HumanData(HumanData humanData) {
        if (this.humanData != humanData) {
            this.humanData = humanData;

            notifyDataSetChanged(); //UI데이터 갱신//
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //리스트에 나타낼 뷰를 생성//
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.humanlist_view, parent, false);

        HumanListViewHolder holder = new HumanListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (humanData.getHumanDataList().size() > 0) {
            if (position < humanData.getHumanDataList().size()) {
                //뷰홀더의 자원을 초기화//
                final HumanListViewHolder humanListViewHolder = (HumanListViewHolder) holder;

                //데이터 클래스, 자원을 할당//
                humanListViewHolder.set_Human_info(humanData.getHumanDataList().get(position), context);

                //위젯 이벤트 처리//
                final int finalPosition = position;
                humanListViewHolder.human_info_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String select_human_name = humanListViewHolder.human_name_text.getText().toString();
                        String select_human_id = "" + humanData.getHumanDataList().get(finalPosition).getHuman_id();
                        String select_human_departmentname = humanData.getHumanDataList().get(finalPosition).getHuman_department();
                        String select_human_tel = humanData.getHumanDataList().get(finalPosition).getHuman_tel();
                        String select_human_address = humanData.getHumanDataList().get(finalPosition).getHuman_address();
                        String select_human_imageurl = humanData.getHumanDataList().get(finalPosition).getHuman_imageurl();

                        Toast.makeText(context, select_human_name + "의 정보수정", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(context, HumanInfoUpdate.class);

                        intent.putExtra(KEY_HUMANID, select_human_id);
                        intent.putExtra(KEY_HUMANADDRESS, select_human_address);
                        intent.putExtra(KEY_HUMANDEPARTMENT, select_human_departmentname);
                        intent.putExtra(KEY_HUMANIMAGEURL, select_human_imageurl);
                        intent.putExtra(KEY_HUMANNAME, select_human_name);
                        intent.putExtra(KEY_HUMANTEL, select_human_tel);

                        ((Activity) context).startActivityForResult(intent, RC_UPDATE);
                    }
                });

                return;
            }

            position -= humanData.getHumanDataList().size();
        }

        throw new IllegalArgumentException("invalid position");
    }

    @Override
    public int getItemCount() {
        if (humanData == null) {
            return 0;
        }

        //현재 리스트에 등록된 개수만큼 반환//
        return humanData.getHumanDataList().size();
    }

    //Adapter에서는 onActivityResult를 사용할 수 없으므로 Activity에서 콜백형식으로 이벤트를 받는다.//
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MyAdapter", "onActivityResult");

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_UPDATE) {
                Log.d("json data", "인사 정보 수정 완료");

                notifyDataSetChanged();
            }
        }
    }
}
