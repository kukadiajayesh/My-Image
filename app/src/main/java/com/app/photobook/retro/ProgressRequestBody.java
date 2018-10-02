package com.app.photobook.retro;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by shree on 26-01-2017.
 */

public class ProgressRequestBody extends RequestBody {

    private File mFile;
    private boolean isCanceled = false;
    private UploadCallbacks mListener;
    private String media_type;

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public interface UploadCallbacks {

        void onInit();

        void onProgressUpdate(int percentage);

        void onError(String error);

        void onCanceled();

        void onFinish();
    }

    public ProgressRequestBody(final File file, final UploadCallbacks listener,String media_type) {
        this.media_type = media_type;
        super.create(MediaType.parse(media_type),file); //"application/pdf"
        mFile = file;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse(media_type); //"application/pdf"
    }


    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        long fileLength = mFile.length();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        if(mFile.exists()){
            Log.e("canRead",mFile.canRead()+"");
            Log.e("canWrite",mFile.canWrite()+"");
        }

        mListener.onInit();
        FileInputStream in = new FileInputStream(mFile);
        long uploaded = 0;

        try {
            int read;
            Handler handler = new Handler(Looper.getMainLooper());
            while ((read = in.read(buffer)) != -1) {

                uploaded += read;
                sink.write(buffer, 0, read);

                // update progress on UI thread
                handler.post(new ProgressUpdater(uploaded, fileLength));

                if (isCanceled) {
                    in.close();
                    mListener.onCanceled();
                    break;
                }
            }

        } catch (Exception ex) {
            mListener.onError(ex.getMessage());
        } finally {
            in.close();
            mListener.onFinish();
        }
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    private class ProgressUpdater implements Runnable {

        private long mUploaded;
        private long mTotal;

        public ProgressUpdater(long uploaded, long total) {
            mUploaded = uploaded;
            mTotal = total;
        }

        @Override
        public void run() {
            mListener.onProgressUpdate((int) (100 * mUploaded / mTotal));
        }
    }

}
