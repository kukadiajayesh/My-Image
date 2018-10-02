package com.app.photobook.dialog;


import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.photobook.R;
import com.app.photobook.model.Comment;
import com.app.photobook.model.User;
import com.app.photobook.retro.RetroApi;
import com.app.photobook.tools.DividerItemDecoration;
import com.app.photobook.tools.MyPrefManager;
import com.app.photobook.tools.Utils;
import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jayesh on 12/8/2017.
 */

public class CommentDialog {

    public static final int LEFT_POSITION = 0;
    public static final int RIGHT_POSITION = 1;

    public static final int TYPE_GET = 1;
    public static final int TYPE_ADD = 2;
    private static final String TAG = CommentDialog.class.getSimpleName();

    Activity context;

    LinearLayout llDialogMain;
    RecyclerView recyclerView;
    EditText edtComment;
    View btnSubmit;

    ImageView ivWifi;
    TextView tvEmptyMsg;
    View btnRetry;
    LinearLayout ll_empty_view;

    View rlProgress;

    RetroApi retroApi;
    int albumId, pageId;

    ProgressDialog progressDialog;
    CommentAdapter commentAdapter;
    ArrayList<Comment> commentArrayList = new ArrayList<>();
    MyPrefManager myPrefManager;
    User user;

    LinearLayout dialogLayout;

    public CommentDialog(Activity context, RetroApi retroApi, LinearLayout dialogLayout) {
        this.context = context;
        this.retroApi = retroApi;
        this.dialogLayout = dialogLayout;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");
        myPrefManager = new MyPrefManager(context);
        user = myPrefManager.getUserDetails();

        View view = dialogLayout;
        findViews(view);
    }

    public void show(int albumId, int pageId) {
        this.albumId = albumId;
        this.pageId = pageId;

        initView();
        dialogLayout.setVisibility(View.VISIBLE);

        //Get All Comments
        getComment(albumId);
    }


    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.y;
    }

    private void initView() {

        if (!Utils.isOnline(context)) {
            Utils.showNoInternetMessage(context, dialogLayout);
            ShowEmptyMessage(true, true, context.getString(R.string.message_no_internet),
                    ContextCompat.getDrawable(context, R.drawable.ic_svg_wifi));
            return;
        }

        ll_empty_view.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentAdapter = new CommentAdapter(context, commentArrayList);
        recyclerView.setAdapter(commentAdapter);


        //add ItemDecoration
        //or
        //recyclerView.addItemDecoration(new DividerItemDecoration(context));
        //or
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, R.drawable.recyclerview_divider));
        edtComment.setText("");

    }

    private void findViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setTitle("Comments");
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLayout.setVisibility(View.GONE);
            }
        });


        ivWifi = view.findViewById(R.id.ivWifi);
        tvEmptyMsg = view.findViewById(R.id.tvEmptyMsg);
        btnRetry = view.findViewById(R.id.btnRetry);
        ll_empty_view = view.findViewById(R.id.frmEmpty);

        rlProgress = view.findViewById(R.id.rlProgress);

        //llDialogMain = view.findViewById(R.id.llDialogMain);
        recyclerView = view.findViewById(R.id.recyclerView);
        edtComment = view.findViewById(R.id.edtComment);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        btnRetry.setOnClickListener(onClickListener);
        btnSubmit.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.btnSubmit) {

                String comment = edtComment.getText().toString();
                if (comment.trim().length() == 0) {
                    edtComment.setError("Enter Comment");
                    return;
                }

                if (!Utils.isOnline(context)) {
                    Utils.showNoInternetMessage(context, dialogLayout);
                    ShowEmptyMessage(true, true, context.getString(R.string.message_no_internet),
                            ContextCompat.getDrawable(context, R.drawable.ic_svg_wifi));
                    return;
                }

                progressDialog.show();

                //Add Comments
                Call<ResponseBody> responseBodyCall = retroApi.addComment(context.getString(R.string.photographer_id)
                        , albumId, pageId, user.id, comment);
                responseBodyCall.enqueue(new ResponseClass(TYPE_ADD));

            } else if (v.getId() == R.id.btnRetry) {

            }

        }
    };

    void ShowEmptyMessage(boolean value, boolean showRetry, String msg, Drawable img) {

        if (value) {
            rlProgress.setVisibility(View.GONE);
            ll_empty_view.setVisibility(View.VISIBLE);
            tvEmptyMsg.setText(msg);
            recyclerView.setVisibility(View.GONE);
            ivWifi.setImageDrawable(img);
            if (showRetry)
                btnRetry.setVisibility(View.VISIBLE);
            else
                btnRetry.setVisibility(View.GONE);

        } else {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty_view.setVisibility(View.GONE);
        }
    }

    private void getComment(int albumId) {

        rlProgress.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Call<ResponseBody> responseBodyCall = retroApi.getComments(context.getString(R.string.photographer_id),
                albumId, pageId);
        responseBodyCall.enqueue(new ResponseClass(TYPE_GET));
    }

    class ResponseClass implements Callback<ResponseBody> {

        int type;

        public ResponseClass(int type) {
            this.type = type;
        }

        @Override
        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            try {
                //Log.e(TAG, "onResponse:1");
                String res = "";
                if (response.code() == HttpStatus.SC_OK) {
                    res = response.body().string();
                } else {
                    res = response.errorBody().string();
                }

                Log.e(TAG, res + "1");

                JSONObject jsonObject = new JSONObject(res);
                Gson gson = new Gson();

                if (type == TYPE_GET) {

                    //String message = jsonObject.getString("message");
                    int error = jsonObject.getInt("error");
                    commentArrayList.clear();

                    if (error == 0) {
                        getComments(jsonObject);
                    } else {
                        progressDialog.dismiss();
                        ShowEmptyMessage(true, false,
                                context.getString(R.string.message_empty_comment),
                                ContextCompat.getDrawable(context, R.drawable.ic_svg_data_no));
                    }

                } else if (type == TYPE_ADD) {

                    progressDialog.dismiss();

                    String message = jsonObject.getString("msg");
                    int error = jsonObject.getInt("error");

                    if (error == 0) {

                        String date = jsonObject.getString("date");

                        Comment comment = gson.fromJson(res, Comment.class);
                        comment.date = date;

                        commentArrayList.add(0, comment);
                        commentAdapter.notifyItemInserted(0);
                        edtComment.setText("");
                        recyclerView.getLayoutManager().scrollToPosition(0);

                        recyclerView.setVisibility(View.VISIBLE);
                        ShowEmptyMessage(false, false, context.getString(R.string.message_empty_comment),
                                ContextCompat.getDrawable(context, R.drawable.ic_svg_wifi));

                    } else {
                        Toast.makeText(context,
                                message, Toast.LENGTH_SHORT).show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                ShowEmptyMessage(true, false,
                        context.getString(R.string.message_empty_comment),
                        ContextCompat.getDrawable(context, R.drawable.ic_svg_data_no));
            }
        }

        @Override
        public void onFailure(Call<ResponseBody> call, Throwable t) {
            if (type == TYPE_ADD) {
                progressDialog.dismiss();
            } else if (type == TYPE_GET) {
                ShowEmptyMessage(true, false,
                        context.getString(R.string.message_empty_comment),
                        ContextCompat.getDrawable(context, R.drawable.ic_comment_black_24dp));
            }
        }
    }

    void getComments(JSONObject jsonObject) {

        Gson gson = new Gson();
        commentArrayList.clear();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                Comment comment = gson.fromJson(jsonObject.toString(), Comment.class);
                commentArrayList.add(comment);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (commentArrayList.size() > 0) {

            commentAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
            rlProgress.setVisibility(View.GONE);
            recyclerView.getLayoutManager().scrollToPosition(0);

            ShowEmptyMessage(false, false, context.getString(R.string.message_empty_comment),
                    ContextCompat.getDrawable(context, R.drawable.ic_svg_wifi));
        } else {
            ShowEmptyMessage(true, false, context.getString(R.string.message_empty_comment),
                    ContextCompat.getDrawable(context, R.drawable.ic_comment_black_24dp));
        }
    }

}
