package com.dsatab.data;

import java.util.Comparator;

import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.listable.Listable;

public class Connection implements NotesItem, Listable {

	public static Comparator<Connection> NAME_COMPARATOR = new Comparator<Connection>() {
		@Override
		public int compare(Connection object1, Connection object2) {
			return object1.getName().compareTo(object2.getName());
		}
	};

	private String description, name, sozialStatus;

	/**
	 * 
	 */
	public Connection() {

	}

	public EventCategory getCategory() {
		return EventCategory.Bekanntschaft;
	}

	@Override
	public String getComment() {
		return getDescription();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String value) {
		this.description = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public String getSozialStatus() {
		return sozialStatus;
	}

	public void setSozialStatus(String value) {
		this.sozialStatus = value;
	}

}
