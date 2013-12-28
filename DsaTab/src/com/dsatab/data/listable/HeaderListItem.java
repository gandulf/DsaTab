package com.dsatab.data.listable;

public class HeaderListItem implements Listable {

	private String title;

	public HeaderListItem(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + title + " " + super.hashCode();
	}

}
