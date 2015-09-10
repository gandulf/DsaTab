package com.dsatab.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Attribute;
import com.dsatab.data.Experience;
import com.dsatab.data.Hero;
import com.dsatab.data.HeroBaseInfo;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;

import java.util.List;

public class CharacterFragment extends BaseProfileFragment {

	private TextView tfExperience, tfTotalLe, tfTotalAu, tfTotalAe, tfTotalKe, tfAT, tfPA, tfFK, tfINI, tfST;
	private TextView tfLabelExperience, tfLabelAT, tfLabelPA, tfLabelFK, tfLabelINI;

	private View charAttributesList;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_character, container, false));

		charAttributesList = root.findViewById(R.id.gen_attributes);
		tfExperience = (TextView) root.findViewById(R.id.attr_abp);
		tfLabelExperience = (TextView) root.findViewById(R.id.attr_abp_label);

		tfTotalAe = (TextView) root.findViewById(R.id.attr_total_ae);
		tfTotalKe = (TextView) root.findViewById(R.id.attr_total_ke);
		tfTotalLe = (TextView) root.findViewById(R.id.attr_total_le);
		tfTotalAu = (TextView) root.findViewById(R.id.attr_total_au);

		tfAT = (TextView) root.findViewById(R.id.attr_at);
		tfPA = (TextView) root.findViewById(R.id.attr_pa);
		tfFK = (TextView) root.findViewById(R.id.attr_fk);
		tfINI = (TextView) root.findViewById(R.id.attr_ini);
		tfST = (TextView) root.findViewById(R.id.attr_st);

		tfLabelAT = (TextView) root.findViewById(R.id.attr_at_label);
		tfLabelPA = (TextView) root.findViewById(R.id.attr_pa_label);
		tfLabelFK = (TextView) root.findViewById(R.id.attr_fk_label);
		tfLabelINI = (TextView) root.findViewById(R.id.attr_ini_label);

		Util.applyRowStyle((TableLayout) root.findViewById(R.id.gen_attributes));

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		tfExperience.setOnClickListener(getEditListener());
		tfExperience.setOnLongClickListener(getEditListener());

		fillAttributeLabel((View) tfLabelMR.getParent(), AttributeType.Magieresistenz);
		fillAttributeLabel((View) tfLabelSO.getParent(), AttributeType.Sozialstatus);
		fillAttributeLabel((View) tfLabelBE.getParent(), AttributeType.Behinderung);

		fillAttributeLabel(tfLabelAT, AttributeType.at);
		fillAttributeLabel(tfLabelPA, AttributeType.pa);
		fillAttributeLabel(tfLabelFK, AttributeType.fk);
		fillAttributeLabel(tfLabelINI, AttributeType.ini);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public AbstractBeing getBeing() {
		return getHero();
	}

	@Override
	public void onModifierAdded(Modificator value) {
		updateValues();
	}

	@Override
	public void onModifierRemoved(Modificator value) {
		updateValues();
	}

	@Override
	public void onModifierChanged(Modificator value) {
		updateValues();
	}

	@Override
	public void onModifiersChanged(List<Modificator> values) {
		updateValues();
	}

	@Override
	public void onValueChanged(Value value) {

		if (value == null) {
			return;
		}

		if (value instanceof Attribute) {
			Attribute attr = (Attribute) value;

			switch (attr.getType()) {
			case Lebensenergie_Aktuell:
				fillAttributeValue(tfLE, attr);
				break;
			case Lebensenergie:
				fillAttributeValue(tfTotalLe, attr);
				break;
			case Astralenergie_Aktuell:
				fillAttributeValue(tfAE, attr);
				break;
			case Astralenergie:
				fillAttributeValue(tfTotalAe, attr);
				break;
			case Ausdauer_Aktuell:
				fillAttributeValue(tfAU, attr);
				break;
			case Ausdauer:
				fillAttributeValue(tfTotalAu, attr);
				break;
			case Karmaenergie_Aktuell:
				fillAttributeValue(tfKE, attr);
				break;
			case Karmaenergie:
				fillAttributeValue(tfTotalKe, attr);
				break;
			case Magieresistenz:
				fillAttributeValue(tfMR, attr);
				break;
			case Sozialstatus:
				fillAttributeValue(tfSO, attr);
				break;
			case at:
				fillAttributeValue(tfAT, attr);
				break;
			case pa:
				fillAttributeValue(tfPA, attr);
				break;
			case fk:
				fillAttributeValue(tfFK, attr);
				break;
			case ini:
				fillAttributeValue(tfINI, attr);
				break;
			case Behinderung:
				fillAttributeValue(tfBE, attr);
				break;
			case Geschwindigkeit:
				fillAttributeValue(tfGS, attr);
				break;
			case Gewandtheit:
			case Mut:
			case Klugheit:
			case Intuition:
			case Körperkraft:
			case Fingerfertigkeit:
			case Konstitution:
			case Charisma:
				fillAttribute(attr);
				break;
			default:
				// do nothing
				break;
			}

		} else if (value instanceof Experience) {
			Util.setText(tfExperience, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		updateValues();

		Util.setText(tfExperience, hero.getExperience(), null);
		tfExperience.setTag(hero.getExperience());

		View xpRow = (View) tfLabelExperience.getParent();
		xpRow.setTag(hero.getExperience());
		xpRow.setOnLongClickListener(getEditListener());
		xpRow.setOnClickListener(getEditListener());

		fillAttributeValue(tfAE, AttributeType.Astralenergie_Aktuell);
		fillAttributeValue(tfAU, AttributeType.Ausdauer_Aktuell);
		fillAttributeValue(tfKE, AttributeType.Karmaenergie_Aktuell);
		fillAttributeValue(tfLE, AttributeType.Lebensenergie_Aktuell);
		fillAttributeValue(tfMR, AttributeType.Magieresistenz);
		fillAttributeValue(tfSO, AttributeType.Sozialstatus);

		fillAttributeValue(tfTotalLe, AttributeType.Lebensenergie);
		fillAttributeValue(tfTotalAu, AttributeType.Ausdauer);

		if (hero.getAttributeValue(AttributeType.Karmaenergie) == null
				|| hero.getAttributeValue(AttributeType.Karmaenergie) == 0) {
			findViewById(R.id.row_ke).setVisibility(View.GONE);
		} else {
			fillAttributeValue(tfTotalKe, AttributeType.Karmaenergie);
			findViewById(R.id.row_ke).setVisibility(View.VISIBLE);
		}

		if (hero.getAttributeValue(AttributeType.Astralenergie) == null
				|| hero.getAttributeValue(AttributeType.Astralenergie) == 0) {
			findViewById(R.id.row_ae).setVisibility(View.GONE);
		} else {
			fillAttributeValue(tfTotalAe, AttributeType.Astralenergie);
			findViewById(R.id.row_ae).setVisibility(View.VISIBLE);
		}

		Util.setText(tfST, hero.getLevel(), 0, null);

		int[] ws = hero.getWundschwelle();
		tfWS.setText(ws[0] + "/" + ws[1] + "/" + ws[2]);

		updateBaseInfo(false);
		//

		fillSpecialFeatures(hero);

		// --
		getDsaActivity().updatePortrait(hero);

		TableLayout attribute2 = (TableLayout) findViewById(R.id.gen_attributes2);
		Util.applyRowStyle(attribute2);

		if (!getHero().getAnimals().isEmpty()) {
			Hint.showHint("CharacterFragment", "ANIMAL_FRAGMENT", getActivity());
		}
	}

	protected void updateValues() {
		fillAttributesList(charAttributesList);

		fillAttributeValue(tfGS, AttributeType.Geschwindigkeit);

		fillAttributeValue(tfAT, AttributeType.at, false);
		fillAttributeValue(tfPA, AttributeType.pa, false);
		fillAttributeValue(tfFK, AttributeType.fk, false);
		fillAttributeValue(tfINI, AttributeType.ini, false);
		fillAttributeValue(tfBE, AttributeType.Behinderung);
	}

	protected void updateBaseInfo(boolean animate) {
		super.updateBaseInfo(animate);

		HeroBaseInfo baseInfo = null;
		if (getHero() != null) {
			baseInfo = getHero().getBaseInfo();
		}

		TextView aussehen = (TextView) findViewById(R.id.gen_aussehen);
		TextView title = (TextView) findViewById(R.id.gen_titel);
		TextView stand = (TextView) findViewById(R.id.gen_stand);
		TextView kultur = (TextView) findViewById(R.id.gen_kultur);
		TextView rasse = ((TextView) findViewById(R.id.gen_rasse));
		TextView ausbildung = ((TextView) findViewById(R.id.gen_ausbildung));



		if (baseInfo == null || TextUtils.isEmpty(baseInfo.getAussehen())) {
			aussehen.setVisibility(View.GONE);
		} else {
			aussehen.setText(baseInfo.getAussehen());
			aussehen.setVisibility(View.VISIBLE);
		}

		if (baseInfo == null || TextUtils.isEmpty(baseInfo.getTitle())) {
			title.setVisibility(View.GONE);
		} else {
			title.setText(baseInfo.getTitle());
			title.setVisibility(View.VISIBLE);
		}

		if (baseInfo == null || TextUtils.isEmpty(baseInfo.getStand())) {
			stand.setVisibility(View.GONE);
		} else {
			stand.setText(baseInfo.getStand());
			stand.setVisibility(View.VISIBLE);
		}

		if (baseInfo == null || TextUtils.isEmpty(baseInfo.getKultur())) {
			kultur.setVisibility(View.GONE);
		} else {
			kultur.setText(baseInfo.getKultur());
			kultur.setVisibility(View.VISIBLE);
		}
		rasse.setVisibility(View.VISIBLE);
		ausbildung.setVisibility(View.VISIBLE);

		if (baseInfo != null) {
			((TextView) findViewById(R.id.gen_groesse)).setText(baseInfo.getGroesse() + " cm");
			((TextView) findViewById(R.id.gen_gewicht)).setText(baseInfo.getGewicht() + " Stein");
			rasse.setText(baseInfo.getRasse());
			ausbildung.setText(baseInfo.getAusbildung());
			((TextView) findViewById(R.id.gen_alter)).setText(Util.toString(baseInfo.getAlter()));
			((TextView) findViewById(R.id.gen_haar_augen)).setText(baseInfo.getHaarFarbe() + " / "
					+ baseInfo.getAugenFarbe());
		} else {
			((TextView) findViewById(R.id.gen_groesse)).setText(null);
			((TextView) findViewById(R.id.gen_gewicht)).setText(null);
			rasse.setText(null);
			ausbildung.setText(null);
			((TextView) findViewById(R.id.gen_alter)).setText(null);
			((TextView) findViewById(R.id.gen_haar_augen)).setText(null);
		}

		if (animate) {
			descriptions.startLayoutAnimation();
		}
	}
}