package com.app.photobook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.app.photobook.model.User;
import com.app.photobook.model.UserRes;
import com.app.photobook.retro.RetroApi;
import com.app.photobook.tools.Constants;
import com.app.photobook.tools.MyPrefManager;
import com.app.photobook.tools.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends BaseActivity {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    String mActivity = "";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edtPersonName)
    EditText edtPersonName;
    @BindView(R.id.edtMobile)
    EditText edtMobile;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.btnSubmit)
    View btnSubmit;

    RetroApi retroApi;
    ProgressDialog progressDialog;
    MyPrefManager myPrefManager;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        myPrefManager = new MyPrefManager(this);
        user = myPrefManager.getUserDetails();
        ButterKnife.bind(this);

        setActionbar();

        CustomApp app = (CustomApp) getApplication();
        retroApi = app.getRetroApi();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");

        if (getIntent() != null) {
            if (getIntent().hasExtra(Constants.ACTIVITY)) {
                mActivity = getIntent().getStringExtra(Constants.ACTIVITY);
            }
        }

        if (mActivity.equals(MainActivity.class.getSimpleName())) {
            setTitle("User Profile");
        } else {
            setTitle("Register User");
        }

        if (user != null) {
            fillDetails();
        }

    }

    private void setActionbar() {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void fillDetails() {

        edtPersonName.setText(user.name);
        edtMobile.setText(user.phone_no);
        edtEmail.setText(user.email);
    }

    void registerUser() {

        if (!Utils.isOnline(this)) {
            Utils.showNoInternetMessage(this, btnSubmit);
            return;
        }

        String name = edtPersonName.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();

        if (name.length() == 0) {
            edtPersonName.setError("Enter Name");
            return;
        } else if (mobile.length() == 0) {
            edtMobile.setError("Enter Name");
            return;
        } else if (email.length() == 0) {
            edtEmail.setError("Enter Email");
            return;
        } else if (email.length() > 0) {
            if (!Utils.isValidEmail(email)) {
                edtEmail.setError("Invalid Email Format");
                return;
            }
        }

        progressDialog.show();
        Call<UserRes> responseBodyCall = retroApi.registerUser(getString(R.string.photographer_id),
                name, email, mobile);
        responseBodyCall.enqueue(new Callback<UserRes>() {
            @Override
            public void onResponse(Call<UserRes> call, Response<UserRes> response) {

                progressDialog.dismiss();
                try {
                    UserRes userRes = response.body();

                    if (userRes.error == 0) {
                        loginSuccess(userRes.data);
                    } else {
                        Toast.makeText(UserProfileActivity.this, userRes.msg, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<UserRes> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(UserProfileActivity.this, t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginSuccess(User user) {
        myPrefManager.createOrUpdateUserDetails(user);
        goForward();
    }

    @OnClick({R.id.btnSubmit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                registerUser();
                break;
        }
    }

    void goForward() {
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
