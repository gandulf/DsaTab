package com.franlopez.flipcheckbox;

import android.widget.Checkable;

public interface CheckableListenable extends Checkable {

	void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener);

    /**
     * Set the state of this component to the given value, without applying the
     * corresponding animation, and without firing an event.
     *
     * @param checked The component state.
     */
    void setCheckedImmediate(boolean checked);
}
