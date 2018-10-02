package com.app.photobook.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.app.photobook.tools.FileUtils;
import com.app.photobook.tools.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jayesh on 11/30/2017.
 */

public class ImageDownloadAndSave extends AsyncTask<String, Void, Boolean> {

    Context context;
    DownloadListener downloadListener;
    Handler handler = new Handler();

    public ImageDownloadAndSave(Context context, DownloadListener downloadListener) {
        this.context = context;
        this.downloadListener = downloadListener;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        String url = strings[0]; // url to download
        int pos = Integer.parseInt(strings[1]); // file name to be stored
        String albumId = strings[2]; // file name to be stored
        String fileId = strings[3]; // file name to be stored

        downloadImagesToSdCard(url, pos, albumId, fileId);
        return null;
    }

    @Override
    protected void onPostExecute(Boolean value) {
        super.onPostExecute(value);
    }

    private boolean downloadImagesToSdCard(String downloadUrl, final int pos, String albumId,
                                           String fileId) {

        try {

            URL url = new URL(downloadUrl);

            /* making a directory in sdcard */
            String sdCard = FileUtils.getDefaultFolder(context) + albumId;

            /*  if specified not exist create new */
            File myDir = new File(sdCard);
            if (!myDir.exists()) {
                myDir.mkdirs();
                Log.v("", "inside mkdir");
            }

            /* checks the file and if it already exist delete */
            String fname = System.currentTimeMillis() + "";//".jpg";
            final File file = new File(myDir, fileId);
            if (file.exists()) {
                file.delete();
            }

            /* Open a connection */
            URLConnection ucon = url.openConnection();
            InputStream inputStream = null;
            HttpURLConnection httpConn = (HttpURLConnection) ucon;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }

            FileOutputStream fos = new FileOutputStream(file);
            int totalSize = httpConn.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                //Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
            }

            fos.close();

            //Compress Image
            Bitmap bitmap = Utils.compressFile(context, file);
            FileUtils.writeBitmap(file, bitmap);

            handler.post(new Runnable() {
                @Override
                public void run() {
                    downloadListener.onComplete(pos, file.getAbsolutePath());
                }
            });


            Log.d("test", "Image Saved in sdcard..");
            return true;
        } catch (IOException io) {
            io.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        handler.post(new Runnable() {
            @Override
            public void run() {
                downloadListener.onError();
            }
        });

        return false;
    }


    public interface DownloadListener {
        void onComplete(int pos, String filename);

        void onError();
    }
}
