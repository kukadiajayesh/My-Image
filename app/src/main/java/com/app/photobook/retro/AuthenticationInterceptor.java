package com.app.photobook.retro;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Jayesh on 11/29/2017.
 */

public class AuthenticationInterceptor implements Interceptor {

    private static final String TAG = AuthenticationInterceptor.class.getSimpleName();
    private String authToken;

    public AuthenticationInterceptor(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .addHeader("Authorization", authToken);

        //Log.e(TAG, "authToken: " + authToken);

        Request request = builder.build();
        return chain.proceed(request);
    }
}
