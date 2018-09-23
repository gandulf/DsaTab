package com.dsatab.data.listable;

import com.dsatab.view.ListSettings.ListItemType;

public class HeaderListItem implements Listable {

	private String title;

	private ListItemType type;

	public HeaderListItem(ListItemType type) {
		this(type.title(), type);
	}

	public HeaderListItem(String title) {
		this(title, null);
	}

	public HeaderListItem(String title, ListItemType type) {
		this.title = title;
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(ListItemType type) {
		this.type = type;
	}

	public ListItemType getType() {
		return type;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + title + " " + super.hashCode();
	}

    @Override
    public long getId() {
        return hashCode();
    }
}
