package com.dsatab.data.items;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

public class ItemContainer {

	private int id;

	private String name;

	private int capacity;

	private int weight;

	private Uri iconUri;

	private List<Item> items;

	public ItemContainer() {
		this.items = new ArrayList<Item>();
	}

	public ItemContainer(int id, String name) {
		this();
		this.id = id;
		this.name = name;
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
		float weight = 0;
		if (items != null) {
			for (Item item : items) {
				weight += item.getWeight();
			}
		}
		return weight;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

}
