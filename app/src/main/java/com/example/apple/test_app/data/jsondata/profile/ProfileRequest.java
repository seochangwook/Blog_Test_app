package com.example.apple.test_app.data.jsondata.profile;

public class ProfileRequest {
    private ProfileRequestResult result;
    private String is_success;

    public ProfileRequestResult getResult() {
        return this.result;
    }

    public void setResult(ProfileRequestResult result) {
        this.result = result;
    }

    public String getIs_success() {
        return this.is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }
}
