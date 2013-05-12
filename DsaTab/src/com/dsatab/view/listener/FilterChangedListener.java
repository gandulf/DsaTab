package com.dsatab.view.listener;

import com.dsatab.view.FilterSettings;
import com.dsatab.view.FilterSettings.FilterType;

public interface FilterChangedListener {

	public void onFilterChanged(FilterType type, FilterSettings settings);
}
