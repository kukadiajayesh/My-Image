package com.app.photobook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.photobook.adapter.NotificationAdapter;
import com.app.photobook.model.NotifyItem;
import com.app.photobook.tools.Constants;
import com.app.photobook.tools.ThemeManager;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityNotifications extends AppCompatActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.ivWifi)
    ImageView ivWifi;
    @BindView(R.id.tvEmptyMsg)
    TextView tvEmptyMsg;
    @BindView(R.id.frmEmpty)
    LinearLayout ll_empty_view;
    @BindView(R.id.llData)
    View llData;

    ArrayList<NotifyItem> notifyItemArrayList = new ArrayList<>();
    NotificationAdapter notificationAdapter;

    ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        themeManager = new ThemeManager(this);
        setTheme(themeManager.getTheme());

        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);
        setBroadcast();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Notifications");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(this,
                notifyItemArrayList);
        recyclerView.setAdapter(notificationAdapter);

        loadNotifications();
    }

    void ShowEmpty() {
        if (notifyItemArrayList.size() == 0) {
            ShowEmptyMessage(true, getString(R.string.message_empty_notifications),
                    ContextCompat.getDrawable(this, R.drawable.ic_svg_board));
        } else {
            ShowEmptyMessage(false, getString(R.string.message_empty_activity),
                    ContextCompat.getDrawable(this, R.drawable.ic_svg_data_no));
        }
    }

    void ShowEmptyMessage(boolean value, String msg, Drawable img) {

        if (value) {
            ll_empty_view.setVisibility(View.VISIBLE);
            tvEmptyMsg.setText(msg);
            llData.setVisibility(View.GONE);
            ivWifi.setImageDrawable(img);
        } else {
            llData.setVisibility(View.VISIBLE);
            ll_empty_view.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    void setBroadcast() {
        IntentFilter intentFilter = new IntentFilter(Constants.ACTION_UPDATE_NOTIFICATION);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTION_UPDATE_NOTIFICATION)) {
                loadNotifications();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(broadcastReceiver);
    }


    void loadNotifications() {

        notifyItemArrayList.clear();
        Database_class db = new Database_class(this);
        db.open();

        int unreadCounter = 0;

        try {
            Cursor cur = db.getNotifications();
            if (cur.getCount() > 0) {

                while (cur.moveToNext()) {
                    NotifyItem itemAlert = new NotifyItem();

                    itemAlert.id = cur.getInt(0);
                    itemAlert.json = cur.getString(1);
                    itemAlert.status = cur.getInt(2);
                    itemAlert.entry_time = DateFormat.format("hh:mm aa dd/MM/yyyy", cur.getLong(3)).toString();

                    if (itemAlert.status == Constants.STATUS_NOTIFICATION_UNREAD) {
                        unreadCounter++;
                    }

                    notifyItemArrayList.add(itemAlert);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();

        if (unreadCounter > 0) {
            setTitle("Notifications (" + unreadCounter + ")");
        }

        notificationAdapter.updateArray(notifyItemArrayList);
        ShowEmpty();
    }
}
