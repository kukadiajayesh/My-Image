package com.app.photobook.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import com.app.photobook.R;

/**
 * Created by Jayesh on 12/16/2017.
 */

public class SharingUtils {

    public static final String PACKAGE_FB = "com.facebook.katana";
    public static final String PACKAGE_GPLUS = "com.google.android.apps.plus";
    public static final String PACKAGE_TWITTER = "com.twitter.android";
    public static final String PACKAGE_WHATSAPP = "com.whatsapp";

/*
    public static void shareAlbum(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }
*/

    public static void shareAlbum(Context context, String text, Uri uri) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        //sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        //context.startActivity(sendIntent);

        startIntentChooser(context, sendIntent);
    }

    private static void startIntentChooser(Context context, Intent sendIntent) {
        // Always use string resources for UI text.
        // This says something like "Share this photo with"
        String title = context.getResources().getString(R.string.app_name);
        // Create intent to show chooser
        Intent chooser = Intent.createChooser(sendIntent, title);

        // Verify the intent will resolve to at least one activity
        if (sendIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }

    public static void sharingToSocialMedia(Context context, String application, String text, Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        //intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (checkAppInstall(context, application)) {
            intent.setPackage(application);
            context.startActivity(intent);
            return;
        }
        Toast.makeText(context, "Application is not install", Toast.LENGTH_LONG).show();
    }

    public static boolean checkAppInstall(Context context, String uri) {
        try {
            context.getPackageManager().getPackageInfo(uri, 1);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sharingToMail(Context context, String text, Uri uri) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        //emailIntent.setType("plain/text");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        emailIntent.setType("application/image");
        //context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));

        startIntentChooser(context, emailIntent);
    }

}
