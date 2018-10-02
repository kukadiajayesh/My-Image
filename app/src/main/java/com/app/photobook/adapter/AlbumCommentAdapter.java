package com.app.photobook.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.app.photobook.ActivityCommentAlbums;
import com.app.photobook.R;
import com.app.photobook.dialog.CommentAdapter;
import com.app.photobook.model.AlbumImage;
import com.app.photobook.model.Comment;
import com.app.photobook.model.User;
import com.app.photobook.tools.DividerItemDecoration;
import com.app.photobook.tools.MyPrefManager;
import com.app.photobook.tools.Utils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jayesh on 9/26/2017.
 */

public class AlbumCommentAdapter extends RecyclerView.Adapter {

    ArrayList<AlbumImage> albums = new ArrayList<>();
    ActivityCommentAlbums context;
    User user;

    public AlbumCommentAdapter(@NonNull ActivityCommentAlbums context, @NonNull ArrayList<AlbumImage> objects) {
        this.context = context;
        albums = objects;
        MyPrefManager myPrefManager = new MyPrefManager(context);
        user = myPrefManager.getUserDetails();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_photo_album_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;

        AlbumImage album = albums.get(position);

        if (!TextUtils.isEmpty(album.url)) {
            Glide.with(context)
                    .load(album.url)
                    .into(viewHolder.ivImage);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        viewHolder.recyclerView.setLayoutManager(linearLayoutManager);

        CommentAdapter commentAdapter = new CommentAdapter(context, album.comments);
        viewHolder.recyclerView.setAdapter(commentAdapter);

        viewHolder.recyclerView.addItemDecoration(
                new DividerItemDecoration(context, R.drawable.recyclerview_divider));

        CustomListener customListener = new CustomListener(position, viewHolder);
        viewHolder.llItem.setOnClickListener(customListener);
        viewHolder.btnSubmit.setOnClickListener(customListener);
    }

    class CustomListener implements View.OnClickListener {

        int pos;
        ViewHolder viewHolder;

        public CustomListener(int pos, ViewHolder viewHolder) {
            this.pos = pos;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View view) {

            if (view.getId() == R.id.btnSubmit) {

                String comment = viewHolder.edtComment.getText().toString();
                if (comment.trim().length() == 0) {
                    viewHolder.edtComment.setError("Enter Comment");
                    return;
                }

                if (!Utils.isOnline(context)) {
                    Utils.showNoInternetMessage(context, viewHolder.btnSubmit);
                    return;
                }

                enableSubmit(viewHolder, false);
                Utils.hidekeyboard(context, viewHolder.btnSubmit);

                //Add Comments
                final int albumId = albums.get(pos).albumId;
                int imageId = albums.get(pos).pageId;

                Call<ResponseBody> responseBodyCall = context.getRetroAPI().addComment(
                        context.getString(R.string.photographer_id),
                        albumId, imageId, user.id, comment);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                        try {
                            String res;
                            if (response.code() == HttpStatus.SC_OK) {
                                res = response.body().string();
                            } else {
                                res = response.errorBody().string();
                            }

                            JSONObject jsonObject = new JSONObject(res);
                            Gson gson = new Gson();

                            String message = jsonObject.getString("msg");
                            int error = jsonObject.getInt("error");

                            if (error == 0) {

                                String date = jsonObject.getString("date");

                                Comment comment = gson.fromJson(res, Comment.class);
                                comment.date = date;

                                albums.get(pos).comments.add(0, comment);
                                viewHolder.recyclerView.getAdapter().notifyItemInserted(0);
                                viewHolder.recyclerView.getLayoutManager().scrollToPosition(0);

                                viewHolder.edtComment.setText("");
                            } else {
                                Toast.makeText(context,
                                        message, Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        enableSubmit(viewHolder, true);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

                        enableSubmit(viewHolder, true);
                    }
                });
            }
        }
    }

    void enableSubmit(ViewHolder viewHolder, boolean value) {

        if (value) {
            viewHolder.edtComment.setEnabled(true);
            viewHolder.btnSubmit.setVisibility(View.VISIBLE);
            viewHolder.progress.setVisibility(View.GONE);
        } else {
            viewHolder.edtComment.setEnabled(false);
            viewHolder.btnSubmit.setVisibility(View.GONE);
            viewHolder.progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage)
        ImageView ivImage;
        @BindView(R.id.recyclerView)
        RecyclerView recyclerView;
        @BindView(R.id.edtComment)
        EditText edtComment;
        @BindView(R.id.btnSubmit)
        ImageView btnSubmit;
        @BindView(R.id.progress)
        ProgressBar progress;
        @BindView(R.id.llItem)
        CardView llItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
