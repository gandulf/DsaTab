package com.dsatab.data.adapter;

import android.net.Uri;

import com.dsatab.util.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ganymedes on 13.12.2016.
 */

public class ImageUriAdapter extends BaseImageAdapter<Uri> {

    public ImageUriAdapter() {
        super(new ArrayList<Uri>());
    }

    public ImageUriAdapter(List<Uri> objects) {
        super(objects);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Uri file = getItem(position);
        holder.imageView.setImageDrawable(ResUtil.getDrawableByUri(holder.imageView.getContext(), file));
    }
}
