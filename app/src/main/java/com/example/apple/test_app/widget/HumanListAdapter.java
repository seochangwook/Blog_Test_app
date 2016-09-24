package com.example.apple.test_app.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.apple.test_app.R;
import com.example.apple.test_app.data.HumanData;
import com.example.apple.test_app.view.HumanListViewHolder;

/**
 * Created by apple on 2016. 9. 24..
 */
public class HumanListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
                humanListViewHolder.human_info_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String select_human_name = humanListViewHolder.human_name_text.getText().toString();

                        Toast.makeText(context, select_human_name + "의 추가정보 보기", Toast.LENGTH_SHORT).show();
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
}
