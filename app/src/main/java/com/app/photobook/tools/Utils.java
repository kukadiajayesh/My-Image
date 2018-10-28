package com.app.photobook.tools;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.photobook.R;
import com.app.photobook.model.ResponseJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import id.zelory.compressor.Compressor;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Jayesh on 9/28/2016.
 */

public class Utils {

    public static void play(Context context, Uri uri) {

        MediaPlayer mediaPlayer = MediaPlayer.create(context, uri);
        mediaPlayer.setVolume(1.0f, 1.0f);
        mediaPlayer.start();
    }

    public static String getContentFromUrl(String url) throws MalformedURLException, IOException {

        URL yahoo = new URL(url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        yahoo.openStream()));

        String inputLine;
        StringBuilder data = new StringBuilder();
        while ((inputLine = in.readLine()) != null)
            data.append(inputLine);
        //System.out.println(inputLine);

        in.close();
        return data.toString();
    }

    public static String getLastPartFromUrl(String url) {

        int index = url.lastIndexOf("/");
        if (index != -1) {
            return url.substring(index + 1);
        }
        return null;

    }

    public static void HideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Bitmap compressFile(Context context, File file) throws NullPointerException {

        return new Compressor.Builder(context)
                .setMaxWidth(Constants.WIDTH)
                .setMaxHeight(Constants.HEIGHT)
                .setQuality(80)
                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                .build()
                .compressToBitmap(file);
        //.compressToFile(file);
        //return fileCamera;
    }


    public static String getRealPathFromURI(Context context, Uri selectedImage) {

        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = context.getContentResolver().query(selectedImage, filePath, null, null, null);
        if (c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(filePath[0]);
            return c.getString(columnIndex);
        } else {
            return null;
        }
    }

    public static int[] getBitmapWidthHeight(String file){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);
        int[] size = new int[2];
        size[0] = options.outWidth;
        size[1] = options.outHeight;
        return size;
    }

    public static Bitmap decodeSampledBitmapFromResource(String file, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file, options);
    }


    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getBitmap(File file, int size) {

        Bitmap bitmap = decodeSampledBitmapFromResource(file.getAbsolutePath(), size, size);

        try {
            ExifInterface ei = new ExifInterface(file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }

        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap img, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
/*
    public static int getAge(long time) {
        LocalDate birthdate = new LocalDate(time);
        LocalDate now = new LocalDate();
        Years age = Years.yearsBetween(birthdate, now);
        return age.getYears();
    }*/

    public static byte[] getBytes(File file) {

        byte[] bytes = null;
        int size = (int) file.length();
        bytes = new byte[size];

        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bytes;

    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Calendar convertTime(String time) {

        Calendar date = Calendar.getInstance();

        try {
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date.setTime(simpleDateFormat.parse(time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static Calendar convertTime(String time, String format) {

        Calendar date = Calendar.getInstance();

        try {
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat(format);
            date.setTime(simpleDateFormat.parse(time));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }


    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }


    public static void showNoInternetMessage(Context context, View view) {

        Snackbar snackbar = Snackbar.make(view,
                context.getString(R.string.message_no_internet),
                Snackbar.LENGTH_LONG);

        // snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        // snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView
                .findViewById(android.support.design.R.id.snackbar_text);
        textView.setGravity(Gravity.CENTER);
        snackbar.show();

    }


    public static void downloadPDF(Context context, View view, String file_url) {

        if (!Utils.isOnline(context)) {
            Utils.showNoInternetMessage(context, view);
            return;
        }

        try {
            Uri intentUri = Uri.parse(file_url);

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(intentUri);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Unable to download pdf, try again !!!", Toast.LENGTH_LONG).show();
        }
    }


    public static int getRandom() {
        Random random = new Random();
        return random.nextInt();
    }


   /* public static void setSelection(AppCompatSpinner appCompatSpinner, String reward_id) {

        int counter = appCompatSpinner.getAdapter().getCount();
        for (int i = 0; i < counter; i++) {
            ParentCategory subCategory = (ParentCategory) appCompatSpinner.getItemAtPosition(i);
            if ((subCategory.reward_id + "").equalsIgnoreCase(reward_id)) {
                appCompatSpinner.setSelection(i);
                break;
            }
        }
    }*/


    public static StateListDrawable makeShape(Context context, int color) {

        // prepare
        int strokeWidth = 5; // 3px not dp
        int roundRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics()); // 8px not dp
        //int strokeColor = Color.parseColor("#2E3135");
        int fillColor = color; // Color.parseColor("#DFDFE0");

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(roundRadius);

        GradientDrawable gdSelect = new GradientDrawable();
        gdSelect.setColor(darker(fillColor));
        //gdSelect.setColorFilter(new LightingColorFilter(0xFFFFFFFF, 0xFFAA0000));
        gdSelect.setCornerRadius(roundRadius);

        StateListDrawable res = new StateListDrawable();
        res.setExitFadeDuration(400);
        //res.setAlpha(45);
        res.addState(new int[]{android.R.attr.state_pressed}, gdSelect);
        res.addState(new int[]{}, gd);

        return res;
    }

    public static int darker(int color) {
        double factor = 0.7;
        return (color & 0xFF000000) |
                (crimp((int) (((color >> 16) & 0xFF) * factor)) << 16) |
                (crimp((int) (((color >> 8) & 0xFF) * factor)) << 8) |
                (crimp((int) (((color) & 0xFF) * factor)));
    }

    public static int crimp(int c) {
        return Math.min(Math.max(c, 0), 255);
    }


    public static String getTimeAgo(String strTime) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        Calendar date = Calendar.getInstance();

        try {
            java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("hh:mm aa dd/MM/yyyy"); //2016-03-05 09:45:54
            date.setTime(simpleDateFormat.parse(strTime));
        } catch (Exception e) {
            e.printStackTrace();
        }

        long time = date.getTimeInMillis();

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return strTime;
        }

        //today or comment_date

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a min ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hr ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return "today";//diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static void scrollTo(final View targetView, final ScrollView scrollView) {

        // Scroll the view so that the touched editText is near the top of the scroll view
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Make it feel like a two step process
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Determine where to set the scroll-to to by measuring the distance from the top of the scroll view
                // to the control to focus on by summing the "top" position of each view in the hierarchy.
                int yDistanceToControlsView = 0;
                View parentView = (View) targetView.getParent();
                while (true) {
                    if (parentView.equals(scrollView)) {
                        break;
                    }
                    yDistanceToControlsView += parentView.getTop();
                    parentView = (View) parentView.getParent();
                }

                // Compute the final position value for the top and bottom of the control in the scroll view.
                final int topInScrollView = yDistanceToControlsView + targetView.getTop();
                final int bottomInScrollView = yDistanceToControlsView + targetView.getBottom();

                // Post the scroll action to happen on the scrollView with the UI thread.
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        int height = targetView.getHeight();
                        scrollView.smoothScrollTo(0, ((topInScrollView + bottomInScrollView) / 2) - height);
                        targetView.requestFocus();
                    }
                });
            }
        }).start();
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static void hidekeyboard(Activity activity, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static boolean hasNavBar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }


    public static void showDialog(Context context, String title, String message,
                                  final DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickListener != null)
                            onClickListener.onClick(dialog, which);
                    }
                })
                .show();
    }

    public static void showDialog(Context context, String title,
                                  final DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (onClickListener != null)
                            onClickListener.onClick(dialog, which);
                    }
                })
                .show();
    }

    public static void swipeRefresh(Context context, SwipeRefreshLayout swipeRefresh) {

        swipeRefresh.setColorSchemeColors(context.getResources().getColor(android.R.color.holo_blue_dark),
                context.getResources().getColor(android.R.color.holo_red_dark),
                context.getResources().getColor(android.R.color.holo_green_light),
                context.getResources().getColor(android.R.color.holo_orange_dark));
    }

    public static void showNoInternetMessage(Context context, View view,
                                             View.OnClickListener onClickListener) {

        Snackbar snackbar = Snackbar.make(view,
                context.getString(R.string.message_no_internet),
                Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Retry", onClickListener);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();

    }

    public static void sendEmail(Context context, String toEmail) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", toEmail, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmail);

        if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else {
            Toast.makeText(context, "you don't have mail app", Toast.LENGTH_LONG).show();
        }

        /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","abc@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));*/
    }

    public static void openMap(Context context, String title, float lat, float lan) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + lat + ">,<" +
                lan + ">?q=<" + lat + ">,<" + lan + ">(" + title + ")"));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();

            String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)",
                    lat, lan, title);

            Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            context.startActivity(unrestrictedIntent);
        }
    }


    public static boolean getAllPermission(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            int makeCall = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);

            ArrayList<String> permiss = new ArrayList<>();

            if (makeCall != PackageManager.PERMISSION_GRANTED) {
                permiss.add(Manifest.permission.CALL_PHONE);
            }

            if (permiss.size() > 0) {
                String[] per = new String[permiss.size()];
                //requestPermissions(permiss.toArray(per), REQUEST_CODE_PERMISSION);
                ActivityCompat.requestPermissions((Activity) context, permiss.toArray(per), Constants.REQUEST_CODE_PERMISSION);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public static String convertToMD5(String str) {

        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static boolean transformColor(boolean play, View llItem, int fromColor, int toColor) {
        if (!play) {
            ObjectAnimator colorFade = ObjectAnimator.ofObject(llItem,
                    "backgroundColor" /*view attribute name*/, new ArgbEvaluator(),
                    fromColor
                    /*from color*/, toColor /*to color*/);
            colorFade.setDuration(3500);
            colorFade.start();
            return true;
        }
        return false;
    }

    public static int getActionBarSize(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return 0;
    }


    public static ResponseJson getResponseJson(Response<ResponseBody> response) throws Exception {

        String res;
        res = response.body().string();
        Log.e("res", res);
        return parseJson(res);
    }


    public static ResponseJson getResponseJson(String response) throws Exception {
        return parseJson(response);
    }

    private static ResponseJson parseJson(String json) throws Exception {

        JSONObject jsonObject = new JSONObject(json);

        ResponseJson responseJson = new ResponseJson();
        responseJson.response_json = json;
        responseJson.jsonObject = jsonObject;
        if (jsonObject.has("message")) {
            responseJson.message = jsonObject.getString("message");
        }
        responseJson.status = jsonObject.getBoolean("status");

        if (responseJson.status) {
            try {

                if (jsonObject.has("details")) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("details");
                        responseJson.details = jsonArray;
                    } catch (JSONException e) {
                        e.printStackTrace();

                        JSONObject jsonObject1;
                        try {
                            jsonObject1 = jsonObject.getJSONObject("details");
                            responseJson.jsonObjectDetails = jsonObject1;
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return responseJson;

    }
}
