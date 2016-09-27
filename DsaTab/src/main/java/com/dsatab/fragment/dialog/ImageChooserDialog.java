package com.dsatab.fragment.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.adapter.BaseRecyclerAdapter;
import com.dsatab.data.adapter.ListRecyclerAdapter;
import com.dsatab.util.Util;
import com.dsatab.view.AutofitRecyclerView;
import com.gandulf.guilib.util.FileFileFilter;
import com.gandulf.guilib.util.ResUtil;
import com.h6ah4i.android.widget.advrecyclerview.animator.SwipeDismissItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.decoration.ItemShadowDecorator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageChooserDialog extends DialogFragment implements ListRecyclerAdapter.EventListener {

	public static final String TAG = "ImageChooserDialog";

	private OnImageSelectedListener imageSelectedListener;

	private AutofitRecyclerView list;
	private PortraitAdapter adapter;

	private ScaleType scaleType = ScaleType.FIT_CENTER;
	private int columnWidth;
	private int columnHeight;

	private Uri imageUri;

	private List<Uri> imageUris = new ArrayList<Uri>();

	public interface OnImageSelectedListener {
		void onImageSelected(Uri imageUri);
	}

	public static boolean hasFiles(File dir) {
		File[] files = dir.listFiles(new FileFileFilter());
		if (files != null && files.length > 0) {
			return true;
		}
		return false;
	}

	public static void pickPortrait(Fragment parent, File dir, final OnImageSelectedListener imageSelectedListener,
			int requestCode) {
		ImageChooserDialog dialog = new ImageChooserDialog();

		Bundle args = new Bundle();
		// TODO value should be set as argument
		File[] files = dir.listFiles(new FileFileFilter());
		if (files != null) {
			for (File file : files) {
				dialog.imageUris.add(Uri.fromFile(file));
			}
		}

		if (dialog.imageUris.isEmpty()) {
			String path = dir.getAbsolutePath();
            Snackbar.make(dialog.getView(), "Keine Bilder gefunden. Kopiere deine eigenen auf deine SD-Karte unter \"" + path
                    + "\" oder lade die Standardportraits in den Einstellungen herunter.", Snackbar.LENGTH_LONG).show();
			return;
		} else {
			dialog.imageSelectedListener = imageSelectedListener;

            dialog.setGridColumnWidth(DsaTabApplication.getInstance().getResources()
                    .getDimensionPixelSize(R.dimen.portrait_width));
			dialog.setGridColumnHeight(DsaTabApplication.getInstance().getResources()
                    .getDimensionPixelSize(R.dimen.portrait_height));

			dialog.setArguments(args);
			dialog.setTargetFragment(parent, requestCode);
			dialog.show(parent.getFragmentManager(), TAG);
		}
	}

	public static boolean hasPortraits() {
		return hasFiles(DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS));
	}

	public static void pickIcons(Fragment parent, FragmentManager fragmentManager,
			final OnImageSelectedListener imageSelectedListener, int requestCode) {
		ImageChooserDialog dialog = new ImageChooserDialog();

		List<Integer> itemIcons = DsaTabApplication.getInstance().getConfiguration().getDsaIcons();
		dialog.setImageIds(itemIcons);
		dialog.setGridColumnWidth(DsaTabApplication.getInstance().getResources()
                .getDimensionPixelSize(R.dimen.icon_button_size));
        dialog.setGridColumnHeight(DsaTabApplication.getInstance().getResources()
                .getDimensionPixelSize(R.dimen.icon_button_size));
		dialog.setScaleType(ScaleType.FIT_CENTER);
		dialog.setImageSelectedListener(imageSelectedListener);

		if (parent != null) {
			dialog.setTargetFragment(parent, 0);
		}
		dialog.show(fragmentManager, ImageChooserDialog.TAG);
	}

	public static void pickIcons(Fragment parent, final OnImageSelectedListener imageSelectedListener, int requestCode) {
		pickIcons(parent, parent.getFragmentManager(), imageSelectedListener, requestCode);
	}

	public static void pickPortrait(Fragment parent, final AbstractBeing being, int requestCode) {

		OnImageSelectedListener imageSelectedListener = new OnImageSelectedListener() {
			@Override
			public void onImageSelected(Uri imageUri) {
				being.setPortraitUri(imageUri);
			}
		};

		pickPortrait(parent, DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS), imageSelectedListener,
                requestCode);
	}

	public void setImageIds(List<Integer> imageIds) {
		imageUris.clear();
		for (Integer resId : imageIds) {
			imageUris.add(Util.getUriForResourceId(resId));
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(builder.getContext());

		View popupcontent = inflater.inflate(R.layout.popup_portrait_chooser,null,false);
        builder.setView(popupcontent);

		list = (AutofitRecyclerView) popupcontent.findViewById(R.id.popup_portrait_chooser_list);
		adapter = new PortraitAdapter(imageUris);
		adapter.setMinHeight(columnHeight);
        adapter.setMinWidth(columnWidth);
        adapter.setScaleType(scaleType);

        list.setColumnWidth(columnWidth);
		list.setAdapter(adapter);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(),3);
        list.setLayoutManager(layoutManager);
        list.setItemAnimator(new SwipeDismissItemAnimator());

        // additional decorations
        //noinspection StatementWithEmptyBody
        if (supportsViewElevation()) {
            // Lollipop or later has native drop shadow feature. ItemShadowDecorator is not required.
        } else {
            list.addItemDecoration(new ItemShadowDecorator((NinePatchDrawable) getResources().getDrawable(R.drawable.material_shadow_z1)));
        }

		adapter.setEventListener(this);

		builder.setTitle("Wähle ein Bild...");

		AlertDialog dialog = builder.create();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;

	}

    @Override
    public void onItemClicked(BaseRecyclerAdapter adapter, int position, View v) {
        imageUri = this.adapter.getItem(position);
        if (imageUri != null && imageSelectedListener != null) {
            imageSelectedListener.onImageSelected(imageUri);
        }
        dismiss();
    }


    @Override
    public boolean onItemLongClicked(BaseRecyclerAdapter adapter, int position, View v) {
        return false;
    }

    @Override
    public void onItemSelected(BaseRecyclerAdapter adapter, int position, boolean value) {

    }

    @Override
    public void onItemRemoved(BaseRecyclerAdapter adapter, int position) {

    }

    public Uri getImageUri() {
		return imageUri;
	}

	public ScaleType getScaleType() {
		return scaleType;
	}

	public void setScaleType(ScaleType scaleType) {
		this.scaleType = scaleType;
		if (adapter != null) {
			adapter.setScaleType(scaleType);
		}
	}

	public void setImageSelectedListener(OnImageSelectedListener imageSelectedListener) {
		this.imageSelectedListener = imageSelectedListener;
	}

	public void setGridColumnWidth(int width) {
		this.columnWidth = width;

	}

	public void setGridColumnHeight(int height) {
		this.columnHeight = height;
	}

    protected final boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }
	static class PortraitAdapter extends ListRecyclerAdapter<PortraitAdapter.PortraitViewHolder, Uri> {

		private ScaleType scaleType;

		private int minHeight;
        private int minWidth;

        protected  class PortraitViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView = null;

            public PortraitViewHolder(View v) {
                super(v);
                imageView = (ImageView) v;
            }
        }

		public PortraitAdapter() {
            super(new ArrayList<Uri>());
		}

		public PortraitAdapter(List<Uri> objects) {
			super(objects);
		}

		public ScaleType getScaleType() {
			return scaleType;
		}

		public void setScaleType(ScaleType scaleType) {
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
        public PortraitViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ImageView iv = new ImageView(parent.getContext());
            iv.setScaleType(scaleType);
            iv.setAdjustViewBounds(true);

            int padding = parent.getResources().getDimensionPixelSize(R.dimen.default_gap);
            iv.setPadding(padding,padding,padding,padding);
            return new PortraitViewHolder(iv);
        }


        @Override
        public void onBindViewHolder(PortraitViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            Uri file = getItem(position);
            holder.imageView.setImageDrawable(ResUtil.getDrawableByUri(holder.imageView.getContext(), file));
        }


	}

}
