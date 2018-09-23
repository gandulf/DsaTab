package com.dsatab.data.listable;


public class WoundListItem implements Listable {

	public WoundListItem() {
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + super.hashCode();
	}

    @Override
    public long getId() {
        return hashCode();
    }
}
