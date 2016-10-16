package com.dsatab.view;

import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.dsatab.R;
import com.dsatab.util.ViewUtils;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class FABToolbarHelper {

    private FABToolbarLayout toolbar;

    private ViewGroup toolbarLayout;
    private ViewGroup fabContainer;
    private ImageView fab;

    private LinearLayout.LayoutParams params;

    public FABToolbarHelper(FABToolbarLayout layout) {
        this.toolbar = layout;

        toolbarLayout = (ViewGroup) toolbar.findViewById(R.id.fabtoolbar_toolbar);
        if (toolbarLayout == null) {
            throw new IllegalStateException("You have to place a view with id = R.id.fabtoolbar_toolbar inside FABToolbarLayout");
        }

        fabContainer = (RelativeLayout) toolbar.findViewById(R.id.fabtoolbar_container);
        if (fabContainer == null) {
            throw new IllegalStateException("You have to place a FABContainer view with id = R.id.fabtoolbar_container inside FABToolbarLayout");
        }

        fab = (ImageView) fabContainer.findViewById(R.id.fabtoolbar_fab);
        if (fab == null) {
            throw new IllegalStateException("You have to place a FAB view with id = R.id.fabtoolbar_fab inside FABContainer");
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.show();
            }
        });

        params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight=1;


        LinearLayout.LayoutParams closeParams = new LinearLayout.LayoutParams(toolbar.getResources().getDimensionPixelSize(R.dimen.icon_button_size), ViewGroup.LayoutParams.MATCH_PARENT);
        AppCompatImageView close = new AppCompatImageView(toolbar.getContext());
        close.setImageDrawable(ViewUtils.circleIcon(close.getContext(), MaterialDrawableBuilder.IconValue.CLOSE));
        close.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbar.hide();
            }
        });
        toolbarLayout.addView(close,closeParams);
    }
    public ImageView addToolbarItem(int viewId, int resourceId,View.OnClickListener clickDelegate) {
        AppCompatImageView modAdd = new AppCompatImageView(toolbar.getContext());
        if (viewId>0) {
            modAdd.setId(viewId);
        }
        modAdd.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        modAdd.setImageDrawable(ViewUtils.circleIcon(modAdd.getContext(), resourceId));
        modAdd.setOnClickListener(clickDelegate);
        toolbarLayout.addView(modAdd,toolbarLayout.getChildCount()-1, params);
        return modAdd;
    }

    public ImageView addToolbarItem(int resourceId, View.OnClickListener clickDelegate) {
        return addToolbarItem(-1,resourceId,clickDelegate);

    }

    public void removeAllViews() {
        toolbarLayout.removeViews(0, toolbarLayout.getChildCount() -1);
    }
}
