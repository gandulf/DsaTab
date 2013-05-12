package com.dsatab.db;

import com.dsatab.data.enums.TalentType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class TalentTypeWrapper {

	@DatabaseField(id = true)
	private String combatTalentType;

	/**
	 * 
	 */
	public TalentTypeWrapper() {
	}

	public TalentTypeWrapper(TalentType type) {
		if (type != null)
			combatTalentType = type.name();
		else
			combatTalentType = null;
	}

	public TalentType get() {
		if (combatTalentType != null)
			return TalentType.valueOf(combatTalentType);
		else
			return null;
	}

}
