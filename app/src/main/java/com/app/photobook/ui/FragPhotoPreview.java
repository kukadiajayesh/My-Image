package com.app.photobook.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.app.photobook.R;

import java.io.File;

public class FragPhotoPreview extends Fragment {

    private GestureImageView ivContent;
    private String path = "";
    private ViewPager viewPager;

    public static FragPhotoPreview getInstant(Bundle bundle) {
        FragPhotoPreview fragPhotoPreview = new FragPhotoPreview();
        fragPhotoPreview.setArguments(bundle);
        return fragPhotoPreview;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        BigImageViewer.initialize(GlideImageLoader.with(getActivity().getApplicationContext()));

        viewPager = ((PhotoPreviewActivity) getActivity()).viewPager;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_photopreview, container, false);
        ivContent = view.findViewById(R.id.iv_content_vpp);
        ivContent.getController().getSettings()
                .setDoubleTapZoom(10f)
                .setMaxZoom(6f);
        ivContent.getController().enableScrollInViewPager(viewPager);

        if (getArguments() != null) {
            path = getArguments().getString("path");
            loadImage(path);
        }

        return view;
    }

    private void loadImage(final String path) {

        //SubsamplingScaleImageView gets = ivContent.getSSIV();

        //ivContent.showImage(Uri.fromFile(new File(path)));
        ivContent.setImageURI(Uri.fromFile(new File(path)));

        /*Glide.with(getActivity())
                .load(path)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(ivContent);*/


    }

}
