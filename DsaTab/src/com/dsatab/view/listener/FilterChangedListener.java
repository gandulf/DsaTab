package com.dsatab.view.listener;

import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.FilterType;

public interface FilterChangedListener {

	public void onFilterChanged(FilterType type, ListSettings settings);
}
