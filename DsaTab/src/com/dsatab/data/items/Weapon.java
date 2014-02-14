package com.dsatab.data.items;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.Dice;
import com.dsatab.data.enums.ItemType;
import com.dsatab.fragment.DiceSliderFragment.DiceRoll;
import com.dsatab.util.Util;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "item_weapon")
public class Weapon extends CloseCombatItem {

	private static final long serialVersionUID = -9104544279435755450L;

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	private String tp;
	@DatabaseField
	private Integer tpKKMin;
	@DatabaseField
	private Integer tpKKStep;
	@DatabaseField
	private boolean twoHanded;
	@DatabaseField
	private String distance;

	// transient cache for the info string
	private String info;

	/**
	 * no arg constructor for ormlite
	 */
	public Weapon() {

	}

	public Weapon(Item item, int version) {
		super(item, ItemType.Waffen, version);
	}

	/**
	 * 
	 */
	public Weapon(Item item) {
		super(item, ItemType.Waffen, 0);
	}

	@Override
	public int getId() {
		return id;
	}

	public String getTp() {
		return tp;
	}

	public void setTp(String tp) {
		this.tp = tp;
	}

	public Integer getTpKKMin() {
		return tpKKMin;
	}

	public void setTpKKMin(Integer tpKKMin) {
		this.tpKKMin = tpKKMin;
	}

	public Integer getTpKKStep() {
		return tpKKStep;
	}

	public void setTpKKStep(Integer tpKKStep) {
		this.tpKKStep = tpKKStep;
	}

	public boolean isTwoHanded() {
		return twoHanded;
	}

	public void setTwoHanded(boolean twoHanded) {
		this.twoHanded = twoHanded;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public int getKKModifier(int kk) {
		kk = kk - getTpKKMin();
		int kkModifier = 0;
		if (kk > 0)
			kkModifier = (int) Math.floor((float) kk / getTpKKStep());
		else
			kkModifier = (int) Math.ceil((float) kk / getTpKKStep());

		return kkModifier;
	}

	/**
	 * @param kk
	 * @param modifier
	 * @return an actual tp amount using dice, so this returns a different result for each call
	 */
	public Integer getTp(int kk, int modifier, boolean successOne, List<DiceRoll> diceRolls) {
		Integer result = null;
		Dice dice = Dice.parseDice(getTp());
		if (dice != null) {
			if (diceRolls == null) {
				diceRolls = new ArrayList<DiceRoll>(dice.diceCount);
			}
			result = 0;
			for (int i = 0; i < dice.diceCount; i++) {
				while (diceRolls.size() <= i) {
					diceRolls.add(Dice.diceRoll(dice.diceType));
				}
				result += diceRolls.get(i).result;
			}
			result += dice.constant;
			// only multiply weapon damage in case of successOne
			if (successOne) {
				// Debug.verbose("ONE *2" + result);
				result *= 2;
			}

			// the rest is added only once
			result += getKKModifier(kk);
			result += modifier;

			// Debug.verbose("const=" + dice.constant + ",mod=" + modifier + ",kk=" + getKKModifier(kk));
		}

		return result;

	}

	public CharSequence getInfo(int kk, int modifier) {

		StyleableSpannableStringBuilder info = new StyleableSpannableStringBuilder();

		String tp = getTp();

		int tpModifier = getKKModifier(kk);

		if (tpModifier != 0 || modifier != 0) {
			Dice dice = Dice.parseDice(tp);
			if (dice != null) {
				dice.constant += tpModifier;
				dice.constant += modifier;
				if (tpModifier + modifier > 0) {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen),
							dice.toString());
				} else {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed),
							dice.toString());
				}
			} else {
				info.append(tp);
				if (tpModifier + modifier > 0) {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueGreen),
							Util.toProbe(tpModifier + modifier));
				} else {
					info.appendColor(DsaTabApplication.getInstance().getResources().getColor(R.color.ValueRed),
							Util.toProbe(tpModifier + modifier));
				}
			}
		} else {
			info.append(tp);
		}

		info.append(" ");
		info.append(Util.toString(getTpKKMin()) + "/" + Util.toString(getTpKKStep()));
		info.append(" ");
		info.append(Util.toString(getWmAt()) + "/" + Util.toString(getWmPa()));
		info.append(" ");
		info.append("Ini " + Util.toString(getIni()));
		info.append(" ");
		info.append(getDistance());
		if (isTwoHanded()) {
			info.append(" 2H");
		}

		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getName()
	 */
	@Override
	public String getName() {
		return "Nahkampf";
	}

	@Override
	public String getInfo() {
		if (info == null) {
			info = TextUtils.expandTemplate("^1 ^2/^3 ^4/^5 Ini ^6 ^7 ^8", getTp(), Util.toString(getTpKKMin()),
					Util.toString(getTpKKStep()), Util.toString(getWmAt()), Util.toString(getWmPa()),
					Util.toString(getIni()), getDistance(), isTwoHanded() ? "2H" : "").toString();
		}
		return info;

	}

	public boolean isAttackable() {
		return wmAt != null;
	}

	public boolean isDefendable() {
		return wmPa != null;
	}

}
