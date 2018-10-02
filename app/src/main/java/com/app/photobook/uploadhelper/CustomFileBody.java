package com.app.photobook.uploadhelper;

import android.content.Context;
import android.net.Uri;

import com.app.photobook.tools.Utils;

import org.apache.http.entity.mime.content.FileBody;

import java.io.File;

public class CustomFileBody extends FileBody {

    String name, id;

    public CustomFileBody(Context context, File file, String name) {
        super(file, Utils.getMimeType(context, Uri.fromFile(file)));
        this.name = name;
    }

    @Override
    public String getFilename() {
        return name;
    }


}
