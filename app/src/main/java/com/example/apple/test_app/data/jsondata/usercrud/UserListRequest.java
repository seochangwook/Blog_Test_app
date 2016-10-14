package com.example.apple.test_app.data.jsondata.usercrud;

public class UserListRequest {
    private String count;
    private UserListRequestResults results;

    public String getCount() {
        return this.count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public UserListRequestResults getResults() {
        return this.results;
    }

    public void setResults(UserListRequestResults results) {
        this.results = results;
    }
}
