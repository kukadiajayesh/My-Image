package com.app.photobook.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.photobook.R;
import com.app.photobook.model.Comment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jayesh on 9/26/2017.
 */

public class CommentAdapter extends RecyclerView.Adapter {

    ArrayList<Comment> commentArrayList = new ArrayList<>();
    Context context;

    public CommentAdapter(@NonNull Context context, @NonNull ArrayList<Comment> objects) {
        this.context = context;
        commentArrayList = objects;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;

        Comment comment = commentArrayList.get(position);

        /*char[] dtArray = new char[comment.comment_date.length() + 7];
        Arrays.fill(dtArray, ' ');

        char[] nmArray = new char[comment.pb_username.length() + 7];
        Arrays.fill(nmArray, ' ');

        viewHolder.tvName.setText(comment.pb_username);

        Rect bounds = new Rect();
        viewHolder.tvName.getPaint().getTextBounds(comment.pb_username, 0, comment.pb_username.length(), bounds);


        viewHolder.tvComment.setText(String.valueOf(nmArray) + ": " + comment.comment + String.valueOf(dtArray));
        viewHolder.tvDate.setText(comment.comment_date);*/

        viewHolder.tvComment.setText(comment.comment);
        viewHolder.tvName.setText(comment.user_name);
        viewHolder.tvDate.setText(comment.date);

        CustomListener customListener = new CustomListener(position, viewHolder);
        viewHolder.llItem.setOnClickListener(customListener);
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

            if (view.getId() == R.id.llItem) {

              /*  Intent intent = new Intent(context, ActivityAlbumView_.class);
                intent.putExtra("images", commentArrayList.get(pos).images);
                context.startActivity(intent);*/
            }
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvComment)
        TextView tvComment;
        @BindView(R.id.tvDate)
        TextView tvDate;
        @BindView(R.id.llItem)
        View llItem;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
