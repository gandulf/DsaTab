package com.dsatab.data.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dsatab.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ganymedes on 13.12.2016.
 */

public abstract class BaseImageAdapter<T> extends ListRecyclerAdapter<BaseImageAdapter.ImageViewHolder, T> {

    private ImageView.ScaleType scaleType;

    private int minHeight;
    private int minWidth;

    public BaseImageAdapter() {
        super(new ArrayList<T>());
    }

    public BaseImageAdapter(List<T> objects) {
        super(objects);
    }

    public ImageView.ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppCompatImageView iv = new AppCompatImageView(parent.getContext());
        iv.setScaleType(scaleType);
        iv.setAdjustViewBounds(true);
        //RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(getMinWidth(),getMinHeight());
        //iv.setLayoutParams(params);

        int padding = parent.getResources().getDimensionPixelSize(R.dimen.default_gap);
        iv.setPadding(padding,padding,padding,padding);
        return new ImageViewHolder(iv);
    }

    protected static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView = null;

        public ImageViewHolder(View v) {
            super(v);
            imageView = (ImageView) v;
        }
    }
}
