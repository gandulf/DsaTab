package com.dsatab.data;

import java.util.Comparator;

import com.dsatab.data.enums.EventCategory;

public interface NotesItem {

	public static final Comparator<NotesItem> COMPARATOR = new Comparator<NotesItem>() {
		@Override
		public int compare(NotesItem object1, NotesItem object2) {
			int compare1 = object1.getCategory().compareTo(object2.getCategory());

			int compare2 = 0;
			if (object1.getName() == null && object2.getName() == null)
				compare2 = 0;
			else if (object1.getName() == null)
				compare2 = 1;
			else if (object2.getName() == null)
				compare2 = -1;
			else
				compare2 = object1.getName().compareTo(object2.getName());

			return compare1 * 10000 + compare2;
		}
	};

	public EventCategory getCategory();

	public String getName();

	public String getComment();
}
