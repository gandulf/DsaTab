package com.dsatab.view;

import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

public class SeekBarEx extends AppCompatSeekBar {

	public interface SeekBarLabelRenderer {
		String render(int value);
	}

	private class OnSeekBarChangeListenerWrapper implements OnSeekBarChangeListener {

		private OnSeekBarChangeListener wrapped;

		public OnSeekBarChangeListenerWrapper(OnSeekBarChangeListener wrapped) {
			this.wrapped = wrapped;
		}

		public OnSeekBarChangeListener getWrapped() {
			return wrapped;
		}

		public void setWrapped(OnSeekBarChangeListener wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (label != null) {
				if (labelRenderer != null) {
					label.setText(labelRenderer.render(getValue()));
				} else {
					label.setText(Integer.toString(getValue()));
				}
			}
			if (wrapped != null) {
				wrapped.onProgressChanged(seekBar, progress, fromUser);
			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
            if (wrapped != null) {
				wrapped.onStartTrackingTouch(seekBar);
			}
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			if (label != null) {
				if (labelRenderer != null) {
					label.setText(labelRenderer.render(getValue()));
				} else {
					label.setText(Integer.toString(getValue()));
				}
			}
			if (wrapped != null) {
				wrapped.onStopTrackingTouch(seekBar);
			}
		}

	}

	private OnSeekBarChangeListenerWrapper wrapper;

	private SeekBarLabelRenderer labelRenderer;

	private int min = 0;

	private TextView label;

	public SeekBarEx(Context context) {
		super(context);
		init();
	}

	public SeekBarEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SeekBarEx(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public int getMinimum() {
		return min;
	}

	public void setMinimum(int min) {
		int diff = this.min - min;

		super.setMax(getMax() + diff);
		super.setProgress(getProgress() + diff);
		this.min = min;
		invalidate();
	}

	@Override
	public synchronized void setMax(int max) {
		super.setMax(max - getMinimum());
		invalidate();
	}

	public int getValue() {
		return getProgress() + min;
	}

	public void setValue(int v) {
		setProgress(v - min);
	}

	public TextView getLabel() {
		return label;
	}

	public void setLabel(TextView label) {
		this.label = label;
	}

	private void init() {
		wrapper = new OnSeekBarChangeListenerWrapper(null);
		super.setOnSeekBarChangeListener(wrapper);
	}

	@Override
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener l) {
		wrapper.setWrapped(l);
	}

	public SeekBarLabelRenderer getLabelRenderer() {
		return labelRenderer;
	}

	public void setLabelRenderer(SeekBarLabelRenderer labelRenderer) {
		this.labelRenderer = labelRenderer;
	}

}
