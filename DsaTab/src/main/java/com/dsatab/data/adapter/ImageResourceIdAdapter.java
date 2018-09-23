package com.dsatab.data.adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ganymedes on 13.12.2016.
 */

public class ImageResourceIdAdapter extends BaseImageAdapter<Integer> {


    public ImageResourceIdAdapter() {
        super(new ArrayList<Integer>());
    }

    public ImageResourceIdAdapter(List<Integer> objects) {
        super(objects);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Integer resId = getItem(position);
        holder.imageView.setImageResource(resId);
    }


}
