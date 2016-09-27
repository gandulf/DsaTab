package com.dsatab.data.modifier;

import com.dsatab.DsaTabApplication;
import com.dsatab.config.DsaTabConfiguration.WoundType;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CombatShieldTalent;
import com.dsatab.data.Hero;
import com.dsatab.data.Probe;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Position;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.modifier.RulesModificator.ModificatorType;

import java.util.Arrays;
import java.util.List;

public class WoundModificator extends AbstractModificator {

	private WoundAttribute woundAttribute;

	public WoundModificator(Hero hero, WoundAttribute woundAttribute, boolean active) {
		super(hero, active);
		this.woundAttribute = woundAttribute;
	}

	@Override
	public String getModificatorName() {
		return "Wunde " + woundAttribute.getName() + " x" + woundAttribute.getValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#getAffectedModifierTypes()
	 */
	@Override
	public List<ModificatorType> getAffectedModifierTypes() {
		return Arrays.asList(ModificatorType.Attribute, ModificatorType.CombatTalent, ModificatorType.DistanceWeapon,
				ModificatorType.Shield, ModificatorType.Weapon);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#affects(com.dsatab.data.enums. AttributeType)
	 */
	@Override
	public boolean affects(AttributeType type) {
		return (type == AttributeType.ini || type == AttributeType.Initiative_Aktuell || AttributeType.isFight(type) || AttributeType
				.isEigenschaft(type));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#affects(com.dsatab.data.Probe)
	 */
	@Override
	public boolean affect(Probe probe) {
		if (probe instanceof CombatProbe || probe instanceof CombatShieldTalent
				|| probe instanceof CombatDistanceTalent || probe instanceof CombatMeleeAttribute) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getModificatorInfo() {
		String info = null;
		if (DsaTabApplication.getInstance().getConfiguration().getWoundType() == WoundType.Trefferzonen) {

			switch (woundAttribute.getPosition()) {
			case Kopf:
				info = "MU,KL,IN,INI -2";
				break;
			case Bauch:
				info = "KO,KK,GS,INI,AT,PA -1; +1W6 SP";
				break;
			case Brust:
				info = "KO,KK,AT,PA -1; +1W6 SP";
				break;
			case LinkerArm:
			case RechterArm:
				info = "KK,FF,AT,PA -2";
				break;
			case LinkesBein:
			case RechtesBein:
				info = "GE,INI,AT,PA -2; GS -1";
				break;
			default:
				// do nothing
				break;
			}
		} else {
			info = "AT,PA,FK,GE,INI -2; GS -1";
		}

		return info;
	}

	protected Integer getValue() {
		return woundAttribute.getValue();
	}

	@Override
	public int getModifierValue(AttributeType type) {
		if (isActive()) {
			int modifier = 0;
			if (DsaTabApplication.getInstance().getConfiguration().getWoundType() == WoundType.Trefferzonen) {

				switch (woundAttribute.getPosition()) {
				case Kopf:
					if (type == AttributeType.Mut || type == AttributeType.Klugheit || type == AttributeType.Intuition
							|| type == AttributeType.ini || type == AttributeType.Initiative_Aktuell) {
						modifier += -2 * getValue();
					}
					break;
				case Bauch:
					if (type == AttributeType.Körperkraft || type == AttributeType.at || type == AttributeType.fk
							|| type == AttributeType.pa || type == AttributeType.Ausweichen) {
						modifier += -1 * getValue();
					} else if (type == AttributeType.Geschwindigkeit) {
						modifier += -1 * getValue();
					}
					break;
				case Brust:
					if (type == AttributeType.Konstitution || type == AttributeType.Körperkraft
							|| type == AttributeType.at || type == AttributeType.fk || type == AttributeType.pa
							|| type == AttributeType.Ausweichen) {
						modifier += -1 * getValue();
					}
					break;
				case LinkerArm:
				case RechterArm:
					if (type == AttributeType.Fingerfertigkeit || type == AttributeType.Körperkraft
							|| type == AttributeType.at || type == AttributeType.fk || type == AttributeType.pa
							|| type == AttributeType.Ausweichen) {
						modifier += -2 * getValue();
					}
					break;
				case LinkesBein:
				case RechtesBein:
					if (type == AttributeType.Gewandtheit || type == AttributeType.ini
							|| type == AttributeType.Initiative_Aktuell || type == AttributeType.at
							|| type == AttributeType.pa || type == AttributeType.fk || type == AttributeType.Ausweichen) {
						modifier += -2 * getValue();
					} else if (type == AttributeType.Geschwindigkeit) {
						modifier += -1 * getValue();
					}
					break;
				default:
					// do nothing
					break;
				}
			} else {
				if (type == AttributeType.at || type == AttributeType.pa || type == AttributeType.fk
						|| type == AttributeType.Gewandtheit || type == AttributeType.Initiative_Aktuell
						|| type == AttributeType.Ausweichen) {
					modifier += -2 * getValue();
				} else if (type == AttributeType.Geschwindigkeit) {
					modifier += -1 * getValue();
				}
			}

			return modifier;
		} else {
			return 0;
		}
	}

	@Override
	public int getModifierValue(Probe probe) {
		if (isActive()) {
			int modifier = 0;
			if (probe instanceof Attribute) {
				Attribute attr = (Attribute) probe;
				return getModifierValue(attr.getType());
			} else if (probe instanceof CombatDistanceTalent || probe instanceof CombatShieldTalent
					|| probe instanceof CombatMeleeAttribute || probe instanceof CombatProbe) {

				if (DsaTabApplication.getInstance().getConfiguration().getWoundType() == WoundType.Trefferzonen) {
					switch (woundAttribute.getPosition()) {
					case Kopf:
						break;
					case Bauch:
					case Brust:
						modifier += -1 * getValue();
						break;
					case LinkerArm:
					case RechterArm:

						if (probe instanceof CombatProbe) {
							CombatProbe combatProbe = (CombatProbe) probe;

							EquippedItem equippedItem = combatProbe.getEquippedItem();

							if (equippedItem != null && equippedItem.getItemSpecification() instanceof Weapon) {
								Weapon w = (Weapon) equippedItem.getItemSpecification();

								if (w.isTwoHanded()) {
									modifier += -1 * getValue();
									// Debug.verbose("Zweihandwaffen Handwunde AT/PA-1*"
									// + getValue());
									break;
								} else {
									if (woundAttribute.getPosition() == Position.LinkerArm) {
										// Debug.verbose("Angriff/Parade mit Hauptwaffe und Wunde auf linkem Arm ignoriert");
										break;
									}
								}
							}
							if (equippedItem != null && equippedItem.getItemSpecification() instanceof Shield) {
								// Shield w = (Shield)
								// combatProbe.getEquippedItem().getItem();
								if (woundAttribute.getPosition() == Position.RechterArm) {
									// Debug.verbose("Angriff/Parade mit Schildwaffe und Wunde auf rechtem Arm ignoriert");
									break;
								}
							}
						}
						// Debug.verbose(" Wunde auf Arm AT/PA -2*" +
						// getValue());
						modifier += -2 * getValue();
						break;
					case LinkesBein:
					case RechtesBein:
						modifier += -2 * getValue();
						break;
					default:
						// do nothing
						break;
					}
				} else {
					modifier += -2 * getValue();
				}
			}

			return modifier;
		} else {
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.modifier.Modificator#fulfills(com.dsatab.data.Hero)
	 */
	@Override
	public boolean fulfills() {
		return woundAttribute.getValue() > 0;
	}

}
