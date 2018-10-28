package com.app.photobook.ui;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.photobook.CustomApp;
import com.app.photobook.R;
import com.app.photobook.model.Photographer;
import com.app.photobook.model.User;
import com.app.photobook.retro.RetroApi;
import com.app.photobook.room.RoomDatabaseClass;
import com.app.photobook.tools.Constants;
import com.app.photobook.tools.MyPrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PhotographerProfile extends BaseActivity {

    private static final String TAG = PhotographerProfile.class.getSimpleName();

    RoomDatabaseClass roomDatabaseClass;

    RetroApi retroApi;
    ProgressDialog progressDialog;
    MyPrefManager myPrefManager;
    User user;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rlBackground)
    View rlBackground;
    @BindView(R.id.ivUserProfile)
    ImageView ivUserProfile;
    @BindView(R.id.container)
    CoordinatorLayout container;
    @BindView(R.id.tvPersonName)
    TextView tvPersonName;
    @BindView(R.id.tvMobile)
    TextView tvMobile;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvAbout)
    TextView tvAbout;

    String albumId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_view);

        myPrefManager = new MyPrefManager(this);
        user = myPrefManager.getUserDetails();
        ButterKnife.bind(this);

        setActionbar();

        roomDatabaseClass = Room.databaseBuilder(this,
                RoomDatabaseClass.class, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

        CustomApp app = (CustomApp) getApplication();
        retroApi = app.getRetroApi();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");

        if (getIntent() != null) {
            if (getIntent().hasExtra("album_id")) {
                albumId = getIntent().getStringExtra("album_id");
                //registerUser();
            }
        }
    }

    private void setActionbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Photographer");

        /*toolbar.setBackgroundColor(ContextCompat.getColor(this,
                R.color.color_trans_30));*/

        /*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(
                ColorUtils.setAlphaComponent(
                        ContextCompat.getColor(this, R.color.color_black),
                        100)));*/

        /*getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this,
                R.color.color_trans_30)));*/
    }

    /*void registerUser() {

        if (!Utils.isOnline(this)) {
            loadFromLocal();
            return;
        }

        progressDialog.show();
        Call<PhotographerRes> responseBodyCall = retroApi.getPhotgrapherDetail(albumId);
        responseBodyCall.enqueue(new Callback<PhotographerRes>() {
            @Override
            public void onResponse(Call<PhotographerRes> call, Response<PhotographerRes> response) {

                progressDialog.dismiss();
                try {
                    PhotographerRes res = response.body();
                    if (!res.error) {

                        //Insert Photographer
                        res.data.pb_id = albumId;
                        roomDatabaseClass.daoPhotographer().insert(res.data);
                        //==//

                        displayDetails(res.data);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    loadFromLocal();
                }

            }

            @Override
            public void onFailure(Call<PhotographerRes> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PhotographerProfile.this, t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                loadFromLocal();
            }
        });
    }*/

    private void loadFromLocal() {
        Photographer photographer = roomDatabaseClass.daoPhotographer().getClientInfo(albumId);
        displayDetails(photographer);
    }

    void displayDetails(Photographer photographer) {

        /*tvPersonName.setText(photographer.clName);

        if (!TextUtils.isEmpty(photographer.clMobile)) {
            tvMobile.setText(photographer.clMobile);
        } else {
            tvMobile.setText("-");
        }

        if (!TextUtils.isEmpty(photographer.clEmail)) {
            tvEmail.setText(photographer.clEmail);
        } else {
            tvEmail.setText("-");
        }
        if (!TextUtils.isEmpty(photographer.clAddress)) {
            tvAddress.setText(photographer.clAddress);
        } else {
            tvAddress.setText("-");
        }
        tvAbout.setText(photographer.clAbouts);

        if (!TextUtils.isEmpty(photographer.clLogo)) {
            Picasso.with(this)
                    .load(photographer.logoUrl)
                    .into(ivUserProfile, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            ivUserProfile.setImageResource(R.drawable.ic_svg_user_thumb_128dp);
                        }
                    });
        } else {
            ivUserProfile.setImageResource(R.drawable.ic_svg_user_thumb_128dp);
        }*/

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
