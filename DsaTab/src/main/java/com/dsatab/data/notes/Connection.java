package com.dsatab.data.notes;

import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.listable.Listable;

import java.util.Comparator;
import java.util.UUID;

public class Connection implements NotesItem, Listable {

	private static final long serialVersionUID = -4271662079398408292L;

	public static Comparator<Connection> NAME_COMPARATOR = new Comparator<Connection>() {
		@Override
		public int compare(Connection object1, Connection object2) {
			return object1.getName().compareTo(object2.getName());
		}
	};

	private UUID id;

	private String description, name;

	private int sozialStatus = 1;

	public Connection() {
		this.id = UUID.randomUUID();
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

	public int getSozialStatus() {
		return sozialStatus;
	}

	public void setSozialStatus(int value) {
		this.sozialStatus = value;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Connection))
			return false;

		Connection otherMyClass = (Connection) other;
		if (id != null && otherMyClass.id != null)
			return id.equals(otherMyClass.id);
		else
			return super.equals(other);
	}

	@Override
	public int hashCode() {
		if (id != null)
			return id.hashCode();
		else
			return super.hashCode();
	}

    @Override
    public long getId() {
        return id.hashCode();
    }
}
