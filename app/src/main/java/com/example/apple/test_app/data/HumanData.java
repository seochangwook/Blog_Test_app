package com.example.apple.test_app.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 2016. 9. 24..
 */
public class HumanData {
    public List<HumanData> humanDataList = new ArrayList<>(); //여러 사람의 정보를 담기위한 배열선언//
    int human_id;
    int human_imageresource_id; //Drawable은 int값이다//
    int human_age;
    String human_name;
    String human_tel;
    String human_job;
    String human_address;

    //getter/setter설정//
    public int getHuman_id() {
        return human_id;
    }

    public void setHuman_id(int human_id) {
        this.human_id = human_id;
    }

    public int getHuman_imageresource_id() {
        return human_imageresource_id;
    }

    public void setHuman_imageresource_id(int human_imageresource_id) {
        this.human_imageresource_id = human_imageresource_id;
    }

    public String getHuman_name() {
        return human_name;
    }

    public void setHuman_name(String human_name) {
        this.human_name = human_name;
    }

    public int getHuman_age() {
        return human_age;
    }

    public void setHuman_age(int human_age) {
        this.human_age = human_age;
    }

    public String getHuman_tel() {
        return human_tel;
    }

    public void setHuman_tel(String human_tel) {
        this.human_tel = human_tel;
    }

    public String getHuman_job() {
        return human_job;
    }

    public void setHuman_job(String human_job) {
        this.human_job = human_job;
    }

    public String getHuman_address() {
        return human_address;
    }

    public void setHuman_address(String human_address) {
        this.human_address = human_address;
    }

    public List<HumanData> getHumanDataList() {
        return humanDataList;
    }

    public void setHumanDataList(List<HumanData> humanDataList) {
        this.humanDataList = humanDataList;
    }


}
