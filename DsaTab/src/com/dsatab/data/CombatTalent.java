package com.dsatab.data;

import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.TalentType;

public interface CombatTalent {

	String getName();

	TalentType getType();

	Probe getAttack();

	Probe getDefense();

	Position getPosition(int w20);
}
