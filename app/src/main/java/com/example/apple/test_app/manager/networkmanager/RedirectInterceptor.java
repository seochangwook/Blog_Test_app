package com.example.apple.test_app.manager.networkmanager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by apple on 2016. 10. 5..
 */
public class RedirectInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (response.code() == 307) {
            String url = response.header("Location");
            Request nRequest = request.newBuilder()
                    .url(url)
                    .build();
            response = chain.proceed(nRequest);
        }
        return response;
    }
}
