package com.dsatab.data.modifier;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.text.TextUtils;
import bsh.EvalError;
import bsh.Interpreter;

import com.dsatab.common.DsaTabRuntimeException;
import com.dsatab.data.Art;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.Hero;
import com.dsatab.data.Modifier;
import com.dsatab.data.Probe;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.enums.TalentType;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.util.Util;

public class RulesModificator extends AbstractModificator {

	public enum ModificatorType {
		ALL, Attribute, Talent, CombatTalent, Spell, Art, Weapon, DistanceWeapon, Shield, Armor
	}

	private String[] affectedTalentNames;
	private String[] affectedSpellNames;
	private String[] affectedArtNames;
	private TalentType[] affectedTalentTypes;
	private AttributeType[] affectedAttributeTypes;
	private String[] affectedItemSpecifications;

	private FeatureType[] requiredSpecialFeatures;
	private FeatureType[] excludeSpecialFeatures;
	private String[] requiredTalentNames;

	private String title;
	private String description;

	private Integer modifier;
	private String modifierExpression;

	private boolean dynamic = false;

	private Modifier modifierObject;

	private Interpreter interpreter;

	/**
	 * 
	 */
	public RulesModificator(Hero hero, String title, String description, Integer modifier) {
		super(hero);
		this.title = title;
		this.description = description;
		this.modifier = modifier;

		modifierObject = new Modifier(modifier, title, description);

	}

	protected Interpreter getInterpreter() throws EvalError {
		if (interpreter == null) {
			interpreter = new Interpreter(); // Construct an interpreter
			interpreter.getNameSpace().importClass(Util.class.getName());
			interpreter.getNameSpace().importPackage(com.dsatab.data.Value.class.getPackage().getName());
			interpreter.getNameSpace().importPackage(com.dsatab.data.enums.AttributeType.class.getPackage().getName());
			interpreter.getNameSpace().importPackage(com.dsatab.data.items.Item.class.getPackage().getName());

			interpreter.set("hero", hero); // Set variables
		}

		return interpreter;
	}

	@Override
	public List<ModificatorType> getAffectedModifierTypes() {
		List<ModificatorType> modificatorTypes = new LinkedList<ModificatorType>();

		if (affectedAttributeTypes != null)
			modificatorTypes.add(ModificatorType.Attribute);
		if (affectedTalentNames != null)
			modificatorTypes.add(ModificatorType.Talent);
		if (affectedSpellNames != null)
			modificatorTypes.add(ModificatorType.Spell);
		if (affectedArtNames != null)
			modificatorTypes.add(ModificatorType.Art);
		if (affectedItemSpecifications != null) {
			for (String clazz : affectedItemSpecifications) {
				if (Weapon.class.getSimpleName().equals(clazz))
					modificatorTypes.add(ModificatorType.Weapon);
				else if (DistanceWeapon.class.getSimpleName().equals(clazz))
					modificatorTypes.add(ModificatorType.DistanceWeapon);
				if (Armor.class.getSimpleName().equals(clazz))
					modificatorTypes.add(ModificatorType.Armor);
				if (Shield.class.getSimpleName().equals(clazz))
					modificatorTypes.add(ModificatorType.Shield);
			}
		}
		if (affectedTalentTypes != null) {
			modificatorTypes.add(ModificatorType.CombatTalent);
		}

		return modificatorTypes;
	}

	public void setAffectedItemSpecifications(List<String> affectedItemSpecifications) {
		this.affectedItemSpecifications = affectedItemSpecifications.toArray(new String[affectedItemSpecifications
				.size()]);
		Arrays.sort(this.affectedItemSpecifications);
	}

	public void setAffectedTalentNames(List<String> affectedTalentNames) {
		this.affectedTalentNames = affectedTalentNames.toArray(new String[affectedTalentNames.size()]);
		Arrays.sort(this.affectedTalentNames);
	}

	public void setAffectedSpellNames(List<String> affectedNames) {
		this.affectedSpellNames = affectedNames.toArray(new String[affectedNames.size()]);
		Arrays.sort(this.affectedSpellNames);
	}

	public void setAffectedArtNames(List<String> affectedNames) {
		this.affectedArtNames = affectedNames.toArray(new String[affectedNames.size()]);
		Arrays.sort(this.affectedArtNames);
	}

	public void setAffectedAttributeTypes(List<AttributeType> affectedAttributeTypes) {
		this.affectedAttributeTypes = affectedAttributeTypes.toArray(new AttributeType[affectedAttributeTypes.size()]);
		Arrays.sort(this.affectedAttributeTypes);
	}

	public void setAffectedTalentTypes(List<TalentType> affectedTalentTypes) {
		this.affectedTalentTypes = affectedTalentTypes.toArray(new TalentType[affectedTalentTypes.size()]);
		Arrays.sort(this.affectedTalentTypes);
	}

	public void setRequiredSpecialFeatures(List<FeatureType> requiredSpecialFeatures) {
		this.requiredSpecialFeatures = requiredSpecialFeatures.toArray(new FeatureType[requiredSpecialFeatures.size()]);
		Arrays.sort(this.requiredSpecialFeatures);
	}

	public void setExcludeSpecialFeatures(List<FeatureType> excludeSpecialFeatures) {
		this.excludeSpecialFeatures = excludeSpecialFeatures.toArray(new FeatureType[excludeSpecialFeatures.size()]);
		Arrays.sort(this.excludeSpecialFeatures);
	}

	public void setRequiredTalentNames(List<String> requiredTalentNames) {
		this.requiredTalentNames = requiredTalentNames.toArray(new String[requiredTalentNames.size()]);
		Arrays.sort(this.requiredTalentNames);
	}

	public Integer getModifier() {
		return modifier;
	}

	public void setModifier(Integer modifier) {
		this.modifier = modifier;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public String getModifierExpression() {
		return modifierExpression;
	}

	public void setModifierExpression(String modifierExpression) {
		if (TextUtils.isEmpty(modifierExpression)) {
			this.modifierExpression = null;
		} else {
			this.modifierExpression = modifierExpression;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#getModificatorName()
	 */
	@Override
	public String getModificatorName() {
		return title;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#getModificatorInfo()
	 */
	@Override
	public String getModificatorInfo() {
		return description;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#affects(com.dsatab.data.enums. AttributeType)
	 */
	@Override
	public boolean affects(AttributeType type) {
		if (affectedAttributeTypes != null) {
			return Arrays.binarySearch(affectedAttributeTypes, type) >= 0;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#affects(com.dsatab.data.Probe)
	 */
	@Override
	public boolean affect(Probe probe) {
		boolean affected = false;

		if (affectedTalentTypes != null) {
			if (probe instanceof CombatMeleeTalent || probe instanceof CombatMeleeAttribute) {
				CombatMeleeTalent meleeTalent;
				if (probe instanceof CombatMeleeAttribute)
					meleeTalent = ((CombatMeleeAttribute) probe).getTalent();
				else
					meleeTalent = (CombatMeleeTalent) probe;

				affected |= Arrays.binarySearch(affectedTalentTypes, meleeTalent.getType()) >= 0;
			} else if (probe instanceof CombatProbe) {
				CombatProbe combatProbe = (CombatProbe) probe;
				affected |= Arrays.binarySearch(affectedTalentTypes, combatProbe.getCombatTalent().getType()) >= 0;
			}
		}
		if (affectedItemSpecifications != null) {
			if (probe instanceof CombatProbe) {
				CombatProbe combatProbe = (CombatProbe) probe;
				affected |= Arrays.binarySearch(affectedItemSpecifications, combatProbe.getEquippedItem()
						.getItemSpecification().getClass().getSimpleName()) >= 0;
			}
		}

		if (affectedTalentNames != null) {
			if (probe instanceof Talent) {
				Talent talent = (Talent) probe;
				affected |= Arrays.binarySearch(affectedTalentNames, talent.getName()) >= 0;
			}
		}
		if (affectedSpellNames != null) {
			if (probe instanceof Spell) {
				Spell spell = (Spell) probe;
				affected |= Arrays.binarySearch(affectedSpellNames, spell.getName()) >= 0;
			}
		}
		if (affectedArtNames != null) {
			if (probe instanceof Art) {
				Art art = (Art) probe;
				affected |= Arrays.binarySearch(affectedArtNames, art.getName()) >= 0;
			}
		}

		return affected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#fulfills()
	 */
	@Override
	public boolean fulfills() {
		boolean fulfills = false;
		if (requiredSpecialFeatures == null && requiredTalentNames == null)
			fulfills = true;

		if (requiredSpecialFeatures != null) {
			for (FeatureType sf : requiredSpecialFeatures) {
				fulfills |= hero.hasFeature(sf);
			}
		}
		if (excludeSpecialFeatures != null) {
			for (FeatureType sf : excludeSpecialFeatures) {
				fulfills &= !hero.hasFeature(sf);
			}
		}
		if (requiredTalentNames != null) {
			for (String talentName : requiredTalentNames) {
				fulfills |= hero.getTalent(talentName) != null;
			}
		}
		return fulfills;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.AbstractModificator#getModifier(com.dsatab.data .Probe)
	 */
	@Override
	public int getModifierValue(Probe type) {
		if (type instanceof Attribute) {
			return getModifierValue(((Attribute) type).getType());
		} else {
			handleModifierExpression(type, null);
			return modifierObject.getModifier();
		}
	}

	protected void handleModifierExpression(Probe probe, AttributeType type) {
		if (modifierExpression != null) {
			try {
				Interpreter i = getInterpreter();
				i.set("attributeType", type);
				i.set("probe", probe);
				if (probe instanceof CombatProbe) {
					CombatProbe combatProbe = (CombatProbe) probe;
					i.set("equippedItem", combatProbe.getEquippedItem());
					i.set("item", combatProbe.getEquippedItem().getItem());
					i.set("talent", combatProbe.getCombatTalent());
					i.set("weapon", combatProbe.getEquippedItem().getItemSpecification());
				}

				// Eval a statement and get the result
				Object result = i.eval(modifierExpression);
				if (result == null) {
					throw new IllegalArgumentException("modifierExpression '" + modifierExpression
							+ "' does not return value");
				} else {
					modifierObject.setModifier(Util.parseInteger(result.toString()));
				}
			} catch (EvalError e) {
				throw new DsaTabRuntimeException(e);
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.AbstractModificator#getModifier(com.dsatab.data .enums.AttributeType)
	 */
	@Override
	public int getModifierValue(AttributeType type) {
		handleModifierExpression(null, type);
		return modifierObject.getModifier();
	}
}
