package com.app.photobook.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.photobook.CustomApp;
import com.app.photobook.R;
import com.app.photobook.model.NotifyItem;
import com.app.photobook.model.ResponseActivityStatus;
import com.app.photobook.tools.Constants;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Jayesh on 9/26/2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter implements Filterable {

    ArrayList<NotifyItem> notifyItemArrayList = new ArrayList<>();
    ArrayList<NotifyItem> notifyItemArrayListFilter = new ArrayList<>();
    Activity context;
    Handler handler = new Handler();
    Typeface typeface, typefaceBold, typefaceItalic;
    Gson gson;

    public NotificationAdapter(@NonNull Activity context,
                               @NonNull ArrayList<NotifyItem> objects) {
        this.context = context;
        notifyItemArrayList = objects;
        notifyItemArrayListFilter.addAll(notifyItemArrayList);

        gson = new Gson();
        CustomApp app = (CustomApp) context.getApplicationContext();
        typeface = app.getFontNormal();
        typefaceBold = app.getFontBold();
        typefaceItalic = app.getFontItalic();

    }

    public void updateArray(@NonNull ArrayList<NotifyItem> objects) {
        notifyItemArrayList = objects;
        notifyItemArrayListFilter.clear();
        notifyItemArrayListFilter.addAll(notifyItemArrayList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.tvTitle.setTypeface(typefaceBold);
        viewHolder.tvText.setTypeface(typeface);
        viewHolder.tvEntryTime.setTypeface(typefaceItalic);

        NotifyItem notifyItem = notifyItemArrayListFilter.get(position);

        CharSequence text = "";
        String title = "", response_type;
        Intent intent = null;

        try {
            JSONObject jsonObject = new JSONObject(notifyItem.json);
            response_type = jsonObject.getString(Constants.RESPONSE_TYPE);

            if (response_type.equals(Constants.TYPE_ACTIVITY_STATUS_UPDATE)) {
                title = Constants.TITLE_ACTIVITY_STATUS_CHANGE;
            } else if (response_type.equals(Constants.TYPE_ACTIVITY_REVIEW_UPDATE)) {
                title = Constants.TITLE_ACTIVITY_STATUS_REVIEWED;
            } else if (response_type.equals(Constants.TYPE_REWARD_STATUS_UPDATE)) {
                title = Constants.TITLE_REWARD_STATUS_CHANGE;
            } else if (response_type.equals(Constants.TYPE_REWARD_REVIEW_UPDATE)) {
                title = Constants.TITLE_REWARD_STATUS_REVIEWED;
            }

            JSONObject jsonDetails = jsonObject.getJSONObject("details");


        } catch (JSONException e) {
            e.printStackTrace();
        }

        viewHolder.tvTitle.setText(title);
        viewHolder.tvText.setText(text);
        viewHolder.tvEntryTime.setText(notifyItem.entry_time);

        if (notifyItem.status == Constants.STATUS_NOTIFICATION_READ) {
            viewHolder.viewLine.setVisibility(View.INVISIBLE);
            viewHolder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.color_white));
        } else {
            viewHolder.viewLine.setVisibility(View.VISIBLE);
            viewHolder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.field_background));
        }


/*        int status = activity.reviewed_status;

        if (status == Constants.STATUS_REVIEW_PENDING) {
            viewHolder.tvStatusFrom.setText("Review Pending");
        } else if (status == Constants.STATUS_PENDING) {
            viewHolder.tvStatusFrom.setText("Pending By");
        } else if (status == Constants.STATUS_APPROVED) {
            viewHolder.tvStatusFrom.setText("Approved By");
        } else if (status == Constants.STATUS_REJECTED) {
            viewHolder.tvStatusFrom.setText("Rejected By");
        }*/


        CustomListener customListener = new CustomListener(position, viewHolder, intent);
        viewHolder.llItem.setOnClickListener(customListener);

    }

    @Override
    public Filter getFilter() {
        return new MyFilter();
    }

    class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            //notifyItemArrayListFilter.clear();
            /*for (NotifyItem dashboardActivity : notifyItemArrayList) {
                if (dashboardActivity.title.toLowerCase().contains(charSequence)) {
                    notifyItemArrayListFilter.add(dashboardActivity);
                }
            }*/
            FilterResults filterResults = new FilterResults();
            filterResults.values = notifyItemArrayListFilter;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            notifyItemArrayListFilter = (ArrayList<NotifyItem>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    class CustomListener implements View.OnClickListener {

        Intent intent;
        int pos;
        ViewHolder viewHolder;

        public CustomListener(int pos, ViewHolder viewHolder, Intent intent) {
            this.pos = pos;
            this.viewHolder = viewHolder;
            this.intent = intent;
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.llItem) {
                context.startActivity(intent);
                context.finish();
            }
        }
    }

    @Override
    public int getItemCount() {
        return notifyItemArrayListFilter.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitle)
        TextView tvTitle;
        @BindView(R.id.ivUserProfile)
        CircularImageView ivUserProfile;
        @BindView(R.id.tvText)
        TextView tvText;
        @BindView(R.id.tvEntryTime)
        TextView tvEntryTime;
        @BindView(R.id.llItem)
        LinearLayout llItem;
        @BindView(R.id.viewLine)
        View viewLine;


        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    ResponseActivityStatus parseResponseTypeStatus(String json) {
        ResponseActivityStatus responseActivityStatus = gson.fromJson(json, ResponseActivityStatus.class);
        return responseActivityStatus;
    }
}
