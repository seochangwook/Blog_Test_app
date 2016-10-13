package com.example.apple.test_app.data.jsondata;

public class UserSearchRequest {
    private UserSearchRequestResult result;
    private String is_success;

    public UserSearchRequestResult getResult() {
        return this.result;
    }

    public void setResult(UserSearchRequestResult result) {
        this.result = result;
    }

    public String getIs_success() {
        return this.is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }
}
