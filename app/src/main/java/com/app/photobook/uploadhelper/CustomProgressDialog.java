package com.app.photobook.uploadhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyboardShortcutGroup;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by shree on 26-01-2017.
 */

public class CustomProgressDialog extends ProgressDialog {


    public CustomProgressDialog(Context context) {
        super(context);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Method method = TextView.class.getMethod("setVisibility",
                    Integer.TYPE);

            Field[] fields = this.getClass().getSuperclass()
                    .getDeclaredFields();

            for (Field field : fields) {
                if (field.getName().equalsIgnoreCase("mProgressNumber")) {
                    field.setAccessible(true);
                    TextView textView = (TextView) field.get(this);
                    method.invoke(textView, View.GONE);
                }
            }
        } catch (Exception e) {
            Log.e(TAG,
                    "Failed to invoke the progressDialog method 'setVisibility' and set 'mProgressNumber' to GONE.",
                    e);
        }
    }

    @Override
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {

    }
}
