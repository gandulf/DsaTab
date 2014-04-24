package com.dsatab.data.listable;

import com.dsatab.view.ListSettings.ListItemType;

public class FooterListItem implements Listable {

	private ListItemType type;

	public FooterListItem(ListItemType type) {
		this.type = type;
	}

	public ListItemType getType() {
		return type;
	}

	public void setType(ListItemType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + type + " " + super.hashCode();
	}

}
