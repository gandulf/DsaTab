package com.dsatab.data.items;

import android.net.Uri;

import com.dsatab.util.Debug;
import com.dsatab.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemContainer<T extends ItemCard> extends ArrayList<T> {

	private static final long serialVersionUID = 1L;

	public static final int SET1 = 0;
	public static final int SET2 = 1;
	public static final int SET3 = 2;

	public static final int INVALID_ID = -1;
	private int id = INVALID_ID;

	private String name;

	private int capacity;

	private Uri iconUri;

	private transient Float weightCache;

	public ItemContainer() {
		iconUri = Util.getUriForResourceId(com.dsatab.R.drawable.dsa_bag);
	}

	public ItemContainer(int id, String name, Uri iconUri) {
		this();
		this.id = id;
		this.name = name;
		this.iconUri = iconUri;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Uri getIconUri() {
		return iconUri;
	}

	public void setIconUri(Uri iconUri) {
		this.iconUri = iconUri;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public float getWeight() {
		if (weightCache == null) {
			Debug.trace("itemcontainer calculated weight");
			float weight = 0;

			for (T item : this) {
				if (item.getItem() != null) {
					weight += item.getItem().getWeight();
				}
			}

			weightCache = weight;
		}
		return weightCache;
	}

	@Override
	public void add(int index, T object) {
		weightCache = null;

		super.add(index, object);
	}

	@Override
	public boolean add(T object) {
		weightCache = null;

		return super.add(object);
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		weightCache = null;

		return super.addAll(collection);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> collection) {
		weightCache = null;

		return super.addAll(index, collection);
	}

	@Override
	public void clear() {
		weightCache = null;

		super.clear();
	}

	@Override
	public T remove(int index) {
		weightCache = null;
		return super.remove(index);
	}

	@Override
	public boolean remove(Object object) {
		weightCache = null;
		return super.remove(object);
	}

	public List<T> getItems() {
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof ItemContainer))
			return false;

		ItemContainer otherMyClass = (ItemContainer) other;
		return id == otherMyClass.id;

	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return name + " " + size();
	}
}
