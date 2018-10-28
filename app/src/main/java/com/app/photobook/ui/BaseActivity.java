package com.app.photobook.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.app.photobook.CustomApp;
import com.app.photobook.model.User;
import com.app.photobook.retro.RetroApi;
import com.app.photobook.tools.MyPrefManager;

public class BaseActivity extends AppCompatActivity {

    RetroApi retroApi;
    MyPrefManager myPrefManager;
    User user;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myPrefManager = new MyPrefManager(this);
        user = myPrefManager.getUserDetails();

        retroApi = ((CustomApp) getApplication()).getRetroApi();
        /*retroApi = ServiceGenerator.createService(RetroApi.class, getString(R.string.user),
                getString(R.string.pwd));*/
    }


}
