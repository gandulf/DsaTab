package com.dsatab.fragment;

import java.util.List;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
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
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

public class CharacterFragment extends BaseProfileFragment {

	private static final String PREF_SHOW_BASEINFO = "SHOW_BASEINFO";

	private TextView tfExperience, tfTotalLe, tfTotalAu, tfTotalAe, tfTotalKe, tfAT, tfPA, tfFK, tfINI, tfST;
	private TextView tfLabelExperience, tfLabelAT, tfLabelPA, tfLabelFK, tfLabelINI;

	private View charAttributesList;
	private ImageButton detailsSwitch;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_character, container, false));

		detailsSwitch = (ImageButton) root.findViewById(R.id.details_switch);

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
		detailsSwitch.setOnClickListener(this);
		tfExperience.setOnClickListener(getEditListener());
		tfExperience.setOnLongClickListener(getEditListener());
		findViewById(R.id.gen_description).setOnClickListener(this);

		fillAttributeLabel((View) tfLabelMR.getParent(), AttributeType.Magieresistenz);
		fillAttributeLabel((View) tfLabelSO.getParent(), AttributeType.Sozialstatus);
		fillAttributeLabel((View) tfLabelBE.getParent(), AttributeType.Behinderung);

		fillAttributeLabel(tfLabelAT, AttributeType.at);
		fillAttributeLabel(tfLabelPA, AttributeType.pa);
		fillAttributeLabel(tfLabelFK, AttributeType.fk);
		fillAttributeLabel(tfLabelINI, AttributeType.ini);

		super.onActivityCreated(savedInstanceState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#setUserVisibleHint(boolean)
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (!isVisibleToUser && mMode != null) {
			mMode.finish();
		}
	}

	@Override
	public AbstractBeing getBeing() {
		return getHero();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gen_description:
		case R.id.details_switch:
			Editor edit = getPreferences().edit();
			edit.putBoolean(PREF_SHOW_BASEINFO, !getPreferences().getBoolean(PREF_SHOW_BASEINFO, true));
			edit.commit();
			updateBaseInfo(true);
			break;
		default:
			super.onClick(v);
		}

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

		((TextView) findViewById(R.id.gen_name)).setText(hero.getName());
		// --
		ImageView portrait = (ImageView) findViewById(R.id.gen_portrait);
		portrait.setOnClickListener(this);
		updatePortrait(hero);

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

		HeroBaseInfo baseInfo = null;
		if (getHero() != null) {
			baseInfo = getHero().getBaseInfo();
		}

		boolean showDetails = getPreferences().getBoolean(PREF_SHOW_BASEINFO, true);

		if (showDetails) {

			if (animate) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(detailsSwitch, "rotation", 180, 0);
				animator.setTarget(detailsSwitch);
				animator.setDuration(250);
				animator.start();
			} else {
				ViewHelper.setRotation(detailsSwitch, 0);
			}

			Animation slideup = AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in);

			if (baseInfo == null || TextUtils.isEmpty(baseInfo.getAussehen())) {
				findViewById(R.id.gen_aussehen).setVisibility(View.GONE);
			} else {
				((TextView) findViewById(R.id.gen_aussehen)).setText(baseInfo.getAussehen());
				if (animate)
					findViewById(R.id.gen_aussehen).startAnimation(slideup);
				findViewById(R.id.gen_aussehen).setVisibility(View.VISIBLE);
			}

			if (baseInfo == null || TextUtils.isEmpty(baseInfo.getTitel())) {
				findViewById(R.id.gen_titel).setVisibility(View.GONE);
			} else {
				((TextView) findViewById(R.id.gen_titel)).setText(baseInfo.getTitel());
				if (animate)
					findViewById(R.id.gen_titel).startAnimation(slideup);
				findViewById(R.id.gen_titel).setVisibility(View.VISIBLE);
			}

			if (baseInfo == null || TextUtils.isEmpty(baseInfo.getStand())) {
				findViewById(R.id.gen_stand).setVisibility(View.GONE);
			} else {
				((TextView) findViewById(R.id.gen_stand)).setText(baseInfo.getStand());
				if (animate)
					findViewById(R.id.gen_stand).startAnimation(slideup);
				findViewById(R.id.gen_stand).setVisibility(View.VISIBLE);
			}

			if (baseInfo == null || TextUtils.isEmpty(baseInfo.getKultur())) {
				findViewById(R.id.gen_kultur).setVisibility(View.GONE);
			} else {
				((TextView) findViewById(R.id.gen_kultur)).setText(baseInfo.getKultur());
				if (animate)
					findViewById(R.id.gen_kultur).startAnimation(slideup);
				findViewById(R.id.gen_kultur).setVisibility(View.VISIBLE);
			}

		} else {
			if (animate) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(detailsSwitch, "rotation", 0, 180);
				animator.setTarget(detailsSwitch);
				animator.setDuration(250);
				animator.start();
			} else {
				ViewHelper.setRotation(detailsSwitch, 180);
			}

			findViewById(R.id.gen_aussehen).setVisibility(View.GONE);
			findViewById(R.id.gen_kultur).setVisibility(View.GONE);
			findViewById(R.id.gen_stand).setVisibility(View.GONE);
			findViewById(R.id.gen_titel).setVisibility(View.GONE);
		}

		if (baseInfo != null) {
			((TextView) findViewById(R.id.gen_groesse)).setText(baseInfo.getGroesse() + " cm");
			((TextView) findViewById(R.id.gen_gewicht)).setText(baseInfo.getGewicht() + " Stein");
			((TextView) findViewById(R.id.gen_rasse)).setText(baseInfo.getRasse());
			((TextView) findViewById(R.id.gen_ausbildung)).setText(baseInfo.getAusbildung());
			((TextView) findViewById(R.id.gen_alter)).setText(Util.toString(baseInfo.getAlter()));
			((TextView) findViewById(R.id.gen_haar_augen)).setText(baseInfo.getHaarFarbe() + " / "
					+ baseInfo.getAugenFarbe());
		} else {
			((TextView) findViewById(R.id.gen_groesse)).setText(null);
			((TextView) findViewById(R.id.gen_gewicht)).setText(null);
			((TextView) findViewById(R.id.gen_rasse)).setText(null);
			((TextView) findViewById(R.id.gen_ausbildung)).setText(null);
			((TextView) findViewById(R.id.gen_alter)).setText(null);
			((TextView) findViewById(R.id.gen_haar_augen)).setText(null);
		}

	}

}