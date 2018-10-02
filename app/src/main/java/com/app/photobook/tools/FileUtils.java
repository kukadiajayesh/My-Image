package com.app.photobook.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.app.photobook.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;

/**
 * Created by Jayesh on 9/28/2016.
 */

public class FileUtils {


    public static String getDefaultFolder(Context context) {

        String path = Environment.getExternalStorageDirectory() +
                File.separator + "Android" + File.separator + "data" +
                File.separator + context.getPackageName() + File.separator;
        return path;
    }

    public static String getFolderPath(Context context, String folder) {

        String path = Environment.getExternalStorageDirectory() + File.separator +
                context.getString(R.string.app_name) + File.separator + folder;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }

    public static void createIfNotFolder(Context context, String folder) {
        File file = new File(getFolderPath(context, folder));
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void copy(File src, File dst) throws IOException {

        if (!dst.exists()) {
            dst.createNewFile();
        }

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static void galleryUpadte(Context context, File dst) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(dst);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
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


    public static String getSize(long file_size) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        String sizeD = "";

        float file_sizef = file_size / 1024f;

        if (file_size <= 1024) { //kb
            sizeD = numberFormat.format(file_sizef) + " KB";
        } else {
            file_sizef = file_size / 1024f / 1024f;
            sizeD = numberFormat.format(file_sizef) + " MB";
        }

        return sizeD;
    }

    public static File compressFilePhoto(Context context, File src) {

        String path = Environment.getExternalStorageDirectory() + File.separator +
                context.getString(R.string.app_name);

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        File temp = new File(path + File.separator
                + ".temp");

        try {
            if (!temp.exists()) {
                temp.createNewFile();
            }

            Bitmap bitmap = Utils.decodeSampledBitmapFromResource(src.getAbsolutePath(),
                    Constants.WIDTH, Constants.HEIGHT);
            writeBitmap(temp, bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return temp;
    }

    public static void writeBitmap(File src, Bitmap bitmap) {

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(src);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {

                long length = folderSize(dir);

                if (deleteDir(dir)) {
                    Toast.makeText(context, getSize(length) + " clear cache", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "cache clear error", Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public static boolean deleteDir(File dir) {

        if (dir != null && dir.isDirectory()) {

            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {

                File files = new File(dir, children[i]);
                boolean success = deleteDir(files);
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }



}
