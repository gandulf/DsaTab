package com.dsatab.view;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dsatab.data.Art;
import com.dsatab.data.Attribute;
import com.dsatab.data.CustomProbe;
import com.dsatab.data.JSONable;
import com.dsatab.data.Markable;
import com.dsatab.data.Purse;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.Value;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.listable.FileListable;
import com.dsatab.data.listable.PurseListable;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListSettings implements JSONable, Serializable, Parcelable {

    private static final long serialVersionUID = -128741208133727278L;

    private static final String FIELD_INCLUDE_MODIFIERS = "includeModifiers";
    private static final String FIELD_SHOW_NORMAL = "showNormal";
    private static final String FIELD_SHOW_FAVORITE = "showFavorite";
    private static final String FIELD_SHOW_UNUSED = "showUnused";
    private static final String FIELD_ITEMS = "items";
    private static final String FIELD_EVENT_CATEGORIES = "eventCategories";

    private boolean showNormal, showFavorite, showUnused;

    private boolean includeModifiers;

    private List<ListItem> listItems;

    private Set<EventCategory> eventCategories;

    /**
     * Basic constructor
     */
    public ListSettings() {
        this(true, true, true, true);
    }

    public ListSettings(JSONObject jsonObject) throws JSONException {
        this(jsonObject.optBoolean(FIELD_SHOW_FAVORITE), jsonObject.optBoolean(FIELD_SHOW_NORMAL), jsonObject
                .optBoolean(FIELD_SHOW_UNUSED), jsonObject.optBoolean(FIELD_INCLUDE_MODIFIERS));

        if (jsonObject.has(FIELD_ITEMS)) {
            JSONArray array = jsonObject.getJSONArray(FIELD_ITEMS);
            listItems = new ArrayList<>(array.length());
            for (int i = 0; i < array.length(); i++) {
                JSONObject tab = array.getJSONObject(i);
                ListItem info = new ListItem(tab);
                listItems.add(info);
            }
        } else {
            listItems = new ArrayList<>();
        }

        eventCategories = new HashSet<>();
        if (jsonObject.has(FIELD_EVENT_CATEGORIES)) {
            JSONArray array = jsonObject.getJSONArray(FIELD_EVENT_CATEGORIES);
            for (int i = 0; i < array.length(); i++) {
                EventCategory category = EventCategory.valueOf(array.optString(i));
                eventCategories.add(category);
            }
        } else {
            eventCategories.addAll(Arrays.asList(EventCategory.values()));
        }
    }

    public ListSettings(ListSettings settings) {
        this(settings.showFavorite, settings.showNormal, settings.showUnused, settings.includeModifiers);
    }

    public ListSettings(boolean fav, boolean normal, boolean unused, boolean incMod) {
        this.showFavorite = fav;
        this.showNormal = normal;
        this.showUnused = unused;
        this.includeModifiers = incMod;

        listItems = new ArrayList<>();
        eventCategories = new HashSet<>(Arrays.asList(EventCategory.values()));
    }

    /**
     * @param in
     */
    public ListSettings(Parcel in) {
        boolean[] booleans = new boolean[4];
        in.readBooleanArray(booleans);
        showFavorite = booleans[0];
        showNormal = booleans[1];
        showUnused = booleans[2];
        includeModifiers = booleans[3];

        listItems = new ArrayList<>();
        in.readTypedList(listItems, ListItem.CREATOR);

        eventCategories = new HashSet<>();
        List<String> enumStrings = new ArrayList<String>();
        in.readList(enumStrings, null);
        for (String enumString : enumStrings) {
            eventCategories.add(EventCategory.valueOf(enumString));
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Creator for the Parcelable
     */
    public static final Parcelable.Creator<ListSettings> CREATOR = new Parcelable.Creator<ListSettings>() {
        @Override
        public ListSettings createFromParcel(Parcel in) {
            return new ListSettings(in);
        }

        @Override
        public ListSettings[] newArray(int size) {
            return new ListSettings[size];
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[]{showFavorite, showNormal, showUnused, includeModifiers});
        dest.writeTypedList(listItems);

        List<String> enumStrings = new ArrayList<String>();
        for (EventCategory improvement : eventCategories) {
            enumStrings.add(improvement.name());
        }
        dest.writeList(enumStrings);
    }

    public boolean isShowNormal() {
        return showNormal;
    }

    public void setShowNormal(boolean showNormal) {
        this.showNormal = showNormal;
    }

    public boolean isShowFavorite() {
        return showFavorite;
    }

    public void setShowFavorite(boolean showFavorite) {
        this.showFavorite = showFavorite;
    }

    public boolean isShowUnused() {
        return showUnused;
    }

    public void setShowUnused(boolean showUnused) {
        this.showUnused = showUnused;
    }

    public boolean isIncludeModifiers() {
        return includeModifiers;
    }

    public void setIncludeModifiers(boolean includeModifiers) {
        this.includeModifiers = includeModifiers;
    }

    public boolean equals(ListSettings settings) {
        boolean result = settings != null;

        if (settings != null) {
            result &= equals(settings.isShowFavorite(), settings.isShowNormal(), settings.isShowUnused(),
                    settings.isIncludeModifiers());

            result &= getEventCategories().containsAll(settings.getEventCategories());
            result &= getEventCategories().size() == settings.getEventCategories().size();

        }
        return result;
    }

    private boolean equals(boolean showFavorite, boolean showNormal, boolean showUnused, boolean includeModifiers) {
        return this.showFavorite == showFavorite && this.showNormal == showNormal && this.showUnused == showUnused
                && this.includeModifiers == includeModifiers;
    }

    public boolean isAllVisible() {
        return showFavorite && showNormal && showUnused && eventCategories.size() == EventCategory.values().length;
    }

    public boolean isVisible(Markable mark) {
        return (showFavorite && mark.isFavorite()) || (showUnused && mark.isUnused())
                || (showNormal && !mark.isFavorite() && !mark.isUnused());
    }

    public void set(ListSettings settings) {
        if (settings != null) {

            this.showFavorite = settings.isShowFavorite();
            this.showNormal = settings.isShowNormal();
            this.showUnused = settings.isShowUnused();
            this.includeModifiers = settings.isIncludeModifiers();

            this.eventCategories = settings.getEventCategories();
        }
    }

    public void addListItem(ListItem item) {
        listItems.add(item);
    }

    public void removeListItem(ListItem item) {
        listItems.remove(item);
    }

    public List<ListItem> getListItems() {
        return listItems;
    }

    public Set<EventCategory> getEventCategories() {
        return eventCategories;
    }

    public void setEventCategories(HashSet<EventCategory> eventCategories) {
        this.eventCategories = eventCategories;
    }

    public boolean isAffected(Value value) {

        ListItemType affectedType = ListSettings.getListItemType(value);
        String affectedName = ListSettings.getListItemName(value);
        String affectedGroup = ListSettings.getListItemGroup(value);

        if (affectedType != null) {
            return hasListItem(affectedType, affectedName) || (affectedGroup !=null && hasListItem(affectedType,affectedGroup));
        } else {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dsatab.data.JSONable#toJSONObject()
     */
    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(FIELD_INCLUDE_MODIFIERS, includeModifiers);
        jsonObject.put(FIELD_SHOW_FAVORITE, showFavorite);
        jsonObject.put(FIELD_SHOW_NORMAL, showNormal);
        jsonObject.put(FIELD_SHOW_UNUSED, showUnused);

        Util.putArray(jsonObject, listItems, FIELD_ITEMS);
        Util.putEnumArray(jsonObject, eventCategories, FIELD_EVENT_CATEGORIES);

        return jsonObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getName() + " " + showNormal + " " + showFavorite + " " + showUnused + " " + includeModifiers;
    }

    public enum ListItemType {
        Header("Überschrift"), Talent("Talente"), Spell("Zauber"), Art("Künste"), Attribute("Eigenschaften"), EquippedItem(
                "Gegenstände"), Modificator("Modifikatoren"), Document("Dokumente"), Notes("Notizen"), Purse(
                "Geldbörse"), Wound("Wunden"), Probe("Proben");

        ListItemType(String title) {
            this.title = title;
        }

        private String title;

        public String title() {
            return this.title;
        }
    }

    public static class ListItem implements JSONable, Parcelable, Comparable<ListItem> {

        private static final String JSON_TYPE = "type";
        private static final String JSON_NAME = "name";

        private ListItemType type;
        private String name;

        public ListItem(AttributeType type) {
            this.type = ListItemType.Attribute;
            setName(type.name());
        }

        public ListItem(TalentGroupType type) {
            this.type = ListItemType.Talent;
            setName(type.name());
        }

        public ListItem(ListItemType type) {
            this.type = type;
        }

        public ListItem(ListItemType type, String name) {
            this.type = type;
            setName(name);
        }

        public ListItem(ListItemType type, ListItemType subType) {
            setType(type);
            setName(subType.name());
        }

        public ListItem(JSONObject json) {
            setType(ListItemType.valueOf(json.optString(JSON_TYPE)));
            setName(json.optString(JSON_NAME, null));

        }

        public ListItem(Parcel in) {
            this.type = ListItemType.valueOf(in.readString());
            setName(in.readString());
        }

        @Override
        public int compareTo(ListItem another) {
            int result = 1000 * type.compareTo(another.type);

            if (name != null && another.name != null)
                result += name.compareTo(another.name);
            else if (name != null)
                result += 1;
            else if (another.name != null)
                result -= 1;

            return result;
        }

        /*
                 * (non-Javadoc)
                 *
                 * @see android.os.Parcelable#describeContents()
                 */
        @Override
        public int describeContents() {
            return 0;
        }

        /**
         * Creator for the Parcelable
         */
        public static final Parcelable.Creator<ListItem> CREATOR = new Parcelable.Creator<ListItem>() {
            @Override
            public ListItem createFromParcel(Parcel in) {
                return new ListItem(in);
            }

            @Override
            public ListItem[] newArray(int size) {
                return new ListItem[size];
            }
        };

        /*
         * (non-Javadoc)
         *
         * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
         */
        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(type.name());
            dest.writeString(name);
        }

        @Override
        public JSONObject toJSONObject() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(JSON_TYPE, type.name());
            json.put(JSON_NAME, name);
            return json;
        }

        public ListItemType getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public void setType(ListItemType type) {
            this.type = type;
        }

        public void setName(String name) {
            if (TextUtils.isEmpty(name))
                this.name = null;
            else
                this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ListItem listItem = (ListItem) o;

            if (type != listItem.type) return false;
            return !(name != null ? !name.equals(listItem.name) : listItem.name != null);

        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }


    }

    public static ListItemType getListItemType(Object o) {
        if (o instanceof Attribute)
            return ListItemType.Attribute;
        else if (o instanceof Art)
            return ListItemType.Art;
        else if (o instanceof Spell) {
            return ListItemType.Spell;
        } else if (o instanceof Talent) {
            return ListItemType.Talent;
        } else if (o instanceof EquippedItem) {
            return ListItemType.EquippedItem;
        } else if (o instanceof Modificator) {
            return ListItemType.Modificator;
        } else if (o instanceof FileListable) {
            return ListItemType.Document;
        } else if (o instanceof PurseListable || o instanceof Purse.PurseValue) {
            return ListItemType.Purse;
        } else if (o instanceof WoundAttribute) {
            return ListItemType.Wound;
        } else if (o instanceof CustomProbe) {
            return ListItemType.Probe;
        } else {
            return null;
        }

    }

    public static String getListItemGroup(Object o) {
        if (o instanceof Talent) {
            return ((Talent) o).getGroupType().name();
        } else {
            return null;
        }
    }

    public static String getListItemName(Object o) {
        if (o instanceof Attribute)
            return ((Attribute) o).getType().name();
        else if (o instanceof Art)
            return ((Art) o).getName();
        else if (o instanceof Spell) {
            return ((Spell) o).getName();
        } else if (o instanceof Talent) {
            return ((Talent) o).getName();
        } else if (o instanceof EquippedItem) {
            return ((EquippedItem) o).getName();
        } else if (o instanceof Modificator) {
            return ((Modificator) o).getModificatorName();
        } else if (o instanceof FileListable) {
            return ((FileListable) o).getFile().getName();
        } else if (o instanceof PurseListable) {
            if (((PurseListable) o).getCurrency() != null) {
                return ((PurseListable) o).getCurrency().name();
            } else {
                return null;
            }
        } else if (o instanceof Purse.PurseValue) {
            return ((Purse.PurseValue) o).getUnit().currency().name();
        } else if (o instanceof CustomProbe) {
            return ((CustomProbe) o).getName();
        } else {
            return null;
        }

    }

    public boolean hasListItem(ListItemType type, String name) {
        for (ListItem listItem : listItems) {
            if (listItem.getType() == type
                    && (TextUtils.isEmpty(listItem.getName()) || listItem.getName().equals(name)))
                return true;
        }
        return false;
    }

    public boolean hasOnlyListItem(ListItem... types) {
        boolean result = !listItems.isEmpty();
        List<ListItem> typeList = Arrays.asList(types);
        for (ListItem listItem : listItems) {
            if (typeList.contains(listItem)) {
                //ok
            } else {
                return false;
            }
        }
        return result;
    }

    public boolean hasListItem(ListItemType type) {
        for (ListItem listItem : listItems) {
            if (listItem.getType() == type)
                return true;
        }
        return false;
    }

}
