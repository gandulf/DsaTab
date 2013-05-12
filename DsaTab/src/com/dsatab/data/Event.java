package com.dsatab.data;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

import com.dsatab.data.enums.EventCategory;

public class Event implements JSONable {

	public static final Comparator<Event> COMPARATOR = new Comparator<Event>() {
		@Override
		public int compare(Event object1, Event object2) {
			int compare1 = object1.getCategory().compareTo(object2.getCategory());
			int compare2 = (int) (object1.getTime() - object2.getTime());

			return compare1 * 10000 + compare2;
		}
	};

	private static final String FIELD_NAME = "name";
	private static final String FIELD_COMMENT = "comment";
	private static final String FIELD_CATEGORY = "category";
	private static final String FIELD_AUDIO_PATH = "auidoPath";
	private static final String FIELD_TIME = "time";

	private String audioPath;

	private String name;

	private String comment;

	private EventCategory category;

	private long time;

	public Event() {
		this.time = System.currentTimeMillis();
		this.category = EventCategory.Misc;
	}

	public Event(JSONObject json) throws JSONException {

		if (json.has(FIELD_COMMENT))
			this.comment = json.getString(FIELD_COMMENT);

		if (json.has(FIELD_NAME))
			this.name = json.getString(FIELD_NAME);

		if (json.has(FIELD_AUDIO_PATH))
			this.audioPath = json.getString(FIELD_AUDIO_PATH);

		if (json.has(FIELD_TIME))
			this.time = json.getLong(FIELD_TIME);
		else
			this.time = System.currentTimeMillis();

		if (json.has(FIELD_CATEGORY))
			this.category = EventCategory.valueOf(json.getString(FIELD_CATEGORY));
		else
			this.category = EventCategory.Misc;

	}

	public void setComment(String message) {
		this.comment = message;

	}

	public String getComment() {
		return comment;
	}

	public long getTime() {
		return time;
	}

	public EventCategory getCategory() {
		return category;
	}

	public void setCategory(EventCategory category) {
		this.category = category;
	}

	public String getAudioPath() {
		return audioPath;
	}

	public void setAudioPath(String audioPath) {
		this.audioPath = audioPath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDeletable() {
		return category != EventCategory.Heldensoftware;
	}

	/**
	 * Constructs a json object with the current data
	 * 
	 * @return
	 * @throws JSONException
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		// do not return json for events with an element they are stored in the
		// xml data
		if (category == EventCategory.Heldensoftware)
			return null;

		JSONObject out = new JSONObject();

		out.put(FIELD_NAME, name);
		out.put(FIELD_COMMENT, comment);
		out.put(FIELD_CATEGORY, category.name());
		out.put(FIELD_AUDIO_PATH, audioPath);
		out.put(FIELD_TIME, time);

		return out;
	}

}
