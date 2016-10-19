package com.example.apple.test_app.data.jsondata.login;

public class LoginRequest {
    private LoginRequestResult result;
    private String is_success;

    public LoginRequestResult getResult() {
        return this.result;
    }

    public void setResult(LoginRequestResult result) {
        this.result = result;
    }

    public String getIs_success() {
        return this.is_success;
    }

    public void setIs_success(String is_success) {
        this.is_success = is_success;
    }
}
