package com.example.apple.test_app.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.apple.test_app.R;
import com.example.apple.test_app.data.HumanData;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by apple on 2016. 9. 24..
 */
public class HumanListViewHolder extends RecyclerView.ViewHolder {
    public TextView human_name_text, human_age_text, human_job_text, human_tel_text, human_address_text;
    public ImageView human_image;
    public ImageButton human_info_button;

    //데이터 클래스 정의//
    public HumanData humanData;

    public HumanListViewHolder(View itemView) {
        super(itemView);

        human_name_text = (TextView) itemView.findViewById(R.id.human_name_text);
        human_age_text = (TextView) itemView.findViewById(R.id.human_age_text);
        human_address_text = (TextView) itemView.findViewById(R.id.human_address_text);
        human_job_text = (TextView) itemView.findViewById(R.id.human_job_text);
        human_tel_text = (TextView) itemView.findViewById(R.id.human_tel_text);
        human_image = (ImageView) itemView.findViewById(R.id.human_image);
        human_info_button = (ImageButton) itemView.findViewById(R.id.human_info_button);
    }

    public void set_Human_info(HumanData human_data, Context context) {
        this.humanData = human_data; //데이터 정보 등록//

        this.humanData.setHuman_id(human_data.getHuman_id());

        this.human_name_text.setText(human_data.getHuman_name());
        this.human_age_text.setText("" + human_data.getHuman_age());
        this.human_tel_text.setText(human_data.getHuman_tel());
        this.human_address_text.setText(human_data.getHuman_address());
        this.human_job_text.setText(human_data.getHuman_job());

        //이미지 설정(Drawable방식)//
        //this.human_image.setImageResource(human_data.getHuman_imageresource_id());

        //이미지 처리작업 방법 2가지 Picasso / Glide//
        String imageUrl = human_data.getHuman_imageurl(); //로드할 이미지를 불러온다.//
        Log.d("data: ", imageUrl);

        /** Glide방법 **/
        /*Glide.with(context)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_folder_x)
                .error(R.mipmap.ic_folder_x)
                .into(this.human_image); //into로 보낼 위젯 선택.//*/

        /** Picasso 방법 **/
        Picasso.with(context)
                .load(imageUrl)
                .placeholder(R.mipmap.ic_folder_x) //로딩 중 이미지//
                .error(R.mipmap.ic_folder_x) //이미지 다운 에러 시 이미지//
                .transform(new CropCircleTransformation()) //이미지를 변형(원형자르기)//
                .fit() //ImageView의 사이즈를 맞추어준다.//
                .into(this.human_image); //into로 보낼 이미지를 나타낼 위젯정의.//
    }
}
