package com.dsatab.data.modifier;

import com.dsatab.data.Probe;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.listable.Listable;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;

import java.util.List;

public interface Modificator extends Listable {

	public Modifier getModifier(Probe type);

	public Modifier getModifier(AttributeType type);

	public int getModifierValue(Probe type);

	public int getModifierValue(AttributeType type);

	public String getModificatorName();

	public String getModificatorInfo();

	public List<ModificatorType> getAffectedModifierTypes();

	public boolean affects(Probe probe);

	public boolean affects(AttributeType probe);

	public boolean fulfills();

	public boolean isActive();
}
