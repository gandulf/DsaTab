package com.dsatab.data.items;

import com.dsatab.data.enums.ItemType;
import com.dsatab.data.enums.Position;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Locale;

@DatabaseTable(tableName = "item_armor")
public class Armor extends ItemSpecification {

	private static final long serialVersionUID = -6158237518512387722L;

	public static final String CATEGORY_ARME = "Arme";
	public static final String CATEGORY_BEINE = "Beine";
	public static final String CATEGORY_HELM = "Helm";
	public static final String CATEGORY_FULL = "Komplettrüstung";
	public static final String CATEGORY_TORSO = "Torso";

	@DatabaseField(generatedId = true)
	protected int id;

	@DatabaseField
	private int rsKopf;
	@DatabaseField
	private int rsRuecken;
	@DatabaseField
	private int rsBrust;
	@DatabaseField
	private int rsBauch;
	@DatabaseField
	private int rsLinkerArm;
	@DatabaseField
	private int rsRechterArm;
	@DatabaseField
	private int rsLinkesBein;
	@DatabaseField
	private int rsRechtesBein;

	@DatabaseField
	private int stars;
	@DatabaseField
	private boolean zonenHalfBe;
	@DatabaseField
	private int totalRs = 0;
	@DatabaseField
	private float totalBe;
	@DatabaseField
	private int totalPieces = 1;

	// cache for max value of rs
	private int maxRs = 0;

	/**
	 * no arg constructor for ormlite
	 */
	public Armor() {
		super(null, ItemType.Rüstung, 0);
	}

	public Armor(Item item) {
		super(item, ItemType.Rüstung, 0);
	}

	@Override
	public int getId() {
		return id;
	}

	public float getTotalBe() {
		return totalBe;
	}

	public void setTotalBe(float be) {
		this.totalBe = be;
	}

	public boolean isZonenHalfBe() {
		if (CATEGORY_HELM.equalsIgnoreCase(item.getCategory()) || CATEGORY_ARME.equalsIgnoreCase(item.getCategory())
				|| CATEGORY_BEINE.equalsIgnoreCase(item.getCategory())) {
			return zonenHalfBe || stars > 0;
		} else {
			return zonenHalfBe;
		}
	}

	public void setZonenHalfBe(boolean zonenHalfBe) {
		this.zonenHalfBe = zonenHalfBe;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.data.items.ItemSpecification#getName()
	 */
	@Override
	public String getName() {
		return "Rüstung";
	}

	@Override
	public String getInfo() {
		return String.format(Locale.getDefault(), "Be %1$.0f;Ko %2$s Ru %3$s Arm %4$s Bein %4$s", totalBe, rsKopf,
				Math.max(Math.max(rsBauch, rsBrust), rsRuecken), Math.max(rsLinkerArm, rsRechterArm),
				Math.max(rsLinkesBein, rsRechtesBein));
	}

	public int getStars() {
		// http://www.wiki-aventurica.de/wiki/Schwerter_%26_Helden/Offizielle_Errata
		// - MBK 68, RS/BE bei der Experten-Trefferzonen-Regel
		if (CATEGORY_HELM.equalsIgnoreCase(item.getCategory()) || CATEGORY_ARME.equalsIgnoreCase(item.getCategory())
				|| CATEGORY_BEINE.equalsIgnoreCase(item.getCategory())) {
			return 0;
		} else {
			return stars;
		}
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public int getTotalPieces() {
		return totalPieces;
	}

	public void setTotalPieces(int totalPieces) {
		this.totalPieces = totalPieces;
	}

	public int getRs(Position pos) {
		switch (pos) {
		case Bauch:
			return rsBauch;
		case Kopf:
			return rsKopf;
		case Brust:
			return rsBrust;
		case Ruecken:
			return rsRuecken;
		case LinkerArm:
			return rsLinkerArm;
		case RechterArm:
			return rsRechterArm;
		case RechtesBein:
			return rsRechtesBein;
		case LinkesBein:
			return rsLinkesBein;
		default:
			return 0;
		}

	}

	public void setRs(Position pos, int rs) {

		switch (pos) {
		case Bauch:
			rsBauch = rs;
			break;
		case Kopf:
			rsKopf = rs;
			break;
		case Brust:
			rsBrust = rs;
			break;
		case Ruecken:
			rsRuecken = rs;
			break;
		case LinkerArm:
			rsLinkerArm = rs;
			break;
		case RechterArm:
			rsRechterArm = rs;
			break;
		case RechtesBein:
			rsRechtesBein = rs;
			break;
		case LinkesBein:
			rsLinkesBein = rs;
			break;
		default:

		}
		maxRs = Math.max(maxRs, rs);
	}

	public int getMaxRs() {
		return maxRs;
	}

	public int getTotalRs() {
		return totalRs;
	}

	public void setTotalRs(int totalRs) {
		this.totalRs = totalRs;
	}

}
