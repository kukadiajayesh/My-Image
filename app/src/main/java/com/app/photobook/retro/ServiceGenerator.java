package com.app.photobook.retro;


import android.text.TextUtils;
import android.util.Log;

import com.app.photobook.CustomApp;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by shree on 09-01-2017.
 */

public class ServiceGenerator {

    static String baseUrl = CustomApp.getConstants().getBaseUrl();

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static <S> S createService(Class<S> serviceClass) {
        Log.e("", "createService: " + baseUrl);


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create());
        Retrofit retrofit = builder.build();

        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass,
                                      String username, String password) {
        Log.e("", "createService: " + baseUrl);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create());
        Retrofit retrofit = builder.build();

        if (!TextUtils.isEmpty(username)
                && !TextUtils.isEmpty(password)) {
            String authToken = Credentials.basic(username, password);
            return createService(serviceClass, authToken);
        }
        return retrofit.create(serviceClass);
    }

    public static <S> S createService(Class<S> serviceClass,
                                      String authToken) {
        Log.e("", "createService: " + baseUrl);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(ScalarsConverterFactory.create());
        Retrofit retrofit = builder.build();

        if (!TextUtils.isEmpty(authToken)) {
            AuthenticationInterceptor authenticationInterceptor = new
                    AuthenticationInterceptor(authToken);

            if (!httpClient.interceptors().contains(authenticationInterceptor)) {
                httpClient.addInterceptor(authenticationInterceptor);
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
}
