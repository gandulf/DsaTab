/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.data.items;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

/**
 * @author Ganymede
 * 
 */
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
