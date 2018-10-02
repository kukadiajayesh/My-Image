package com.app.photobook.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.photobook.R;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.indicator.progresspie.ProgressPieIndicator;
import com.github.piasy.biv.view.BigImageView;

import java.io.File;

public class FragPhotoPreview_bck extends Fragment {

    private BigImageView ivContent;
    private String path = "";

    public static FragPhotoPreview_bck getInstant(Bundle bundle) {
        FragPhotoPreview_bck fragPhotoPreview = new FragPhotoPreview_bck();
        fragPhotoPreview.setArguments(bundle);
        return fragPhotoPreview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BigImageViewer.initialize(GlideImageLoader.with(getActivity().getApplicationContext()));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_photopreview, container, false);
        ivContent = view.findViewById(R.id.iv_content_vpp);

        ivContent.setProgressIndicator(new ProgressPieIndicator());
        if (getArguments() != null) {
            path = getArguments().getString("path");
            loadImage(path);
        }

        return view;
    }

    private void loadImage(final String path) {

        SubsamplingScaleImageView gets = ivContent.getSSIV();
        gets.setMinScale(0.05f);

        ivContent.showImage(Uri.fromFile(new File(path)));

        /*Glide.with(getActivity())
                .load(path)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(ivContent);*/


    }

}
