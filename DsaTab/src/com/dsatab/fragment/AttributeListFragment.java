package com.dsatab.fragment;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.data.Attribute;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.view.listener.HeroChangedListener;

public class AttributeListFragment extends BaseAttributesFragment implements HeroChangedListener {

	public static final String TAG = "attributeListFragment";

	private TextView tfName;

	/**
	 * 
	 */
	public AttributeListFragment() {
		this.inverseColors = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// create ContextThemeWrapper from the original Activity Context with the custom theme
		Context context = new ContextThemeWrapper(getActivity(), R.style.DsaTabTheme_Dark);
		// clone the inflater using the ContextThemeWrapper
		LayoutInflater localInflater = inflater.cloneInContext(context);
		// inflate using the cloned inflater, not the passed in default
		View view = localInflater.inflate(R.layout.attributes_list, container, false);

		tfName = (TextView) view.findViewById(R.id.attr_name);

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onConfigurationChanged(android.content .res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		updateView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (tfName != null) {
			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/harrington.ttf");
			tfName.setTypeface(tf);
		}

		fillAttributeLabel(tfLabelLE, AttributeType.Lebensenergie_Aktuell);
		fillAttributeLabel(tfLabelAU, AttributeType.Ausdauer_Aktuell);
		fillAttributeLabel(tfLabelKE, AttributeType.Karmaenergie_Aktuell);
		fillAttributeLabel(tfLabelAE, AttributeType.Astralenergie_Aktuell);

		updateView();

		super.onActivityCreated(savedInstanceState);
	}

	private void updateView() {
		SharedPreferences preferences = DsaTabApplication.getPreferences();

		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_NAME, true)) {
			tfName.setVisibility(View.VISIBLE);
		} else {
			tfName.setVisibility(View.GONE);
		}

		int visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_LE, true) ? View.VISIBLE : View.GONE;
		tfLE.setVisibility(visible);
		tfLabelLE.setVisibility(visible);

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_AU, true) ? View.VISIBLE : View.GONE;
		tfAU.setVisibility(visible);
		tfLabelAU.setVisibility(visible);

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_AE, true) ? View.VISIBLE : View.GONE;
		if (visible == View.VISIBLE && getHero() != null
				&& getHero().getAttributeValue(AttributeType.Astralenergie_Aktuell) != null) {
			tfAE.setVisibility(visible);
			tfLabelAE.setVisibility(visible);
		} else {
			tfAE.setVisibility(View.GONE);
			tfLabelAE.setVisibility(View.GONE);
		}

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_KE, true) ? View.VISIBLE : View.GONE;
		if (visible == View.VISIBLE && getHero() != null
				&& getHero().getAttributeValue(AttributeType.Karmaenergie_Aktuell) != null) {
			tfKE.setVisibility(visible);
			tfLabelKE.setVisibility(visible);
		} else {
			tfKE.setVisibility(View.GONE);
			tfLabelKE.setVisibility(View.GONE);
		}

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_BE, true) ? View.VISIBLE : View.GONE;
		tfBE.setVisibility(visible);
		tfLabelBE.setVisibility(visible);

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_MR, true) ? View.VISIBLE : View.GONE;
		tfMR.setVisibility(visible);
		tfLabelMR.setVisibility(visible);

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_GS, true) ? View.VISIBLE : View.GONE;
		tfGS.setVisibility(visible);
		tfLabelGS.setVisibility(visible);

		visible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_WS, true) ? View.VISIBLE : View.GONE;
		tfWS.setVisibility(visible);
		tfLabelWS.setVisibility(visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.ValueChangedListener#onValueChanged(com.dsatab .data.Value)
	 */
	@Override
	public void onValueChanged(Value value) {
		if (value instanceof Attribute) {
			Attribute attr = (Attribute) value;

			switch (attr.getType()) {
			case Konstitution:
				if (tfWS != null && getHero() != null) {
					int[] ws = getHero().getWundschwelle();
					tfWS.setText(ws[0] + "/" + ws[1] + "/" + ws[2]);
				}
				// no break because we have to call fillAttribute too!!!
			case Mut:
			case Klugheit:
			case Intuition:
			case Körperkraft:
			case Fingerfertigkeit:
			case Gewandtheit:
			case Charisma:
				fillAttribute(attr, true);
				break;
			case Lebensenergie_Aktuell:
				fillAttributeValue(tfLE, AttributeType.Lebensenergie_Aktuell, null, true, true);
				break;
			case Ausdauer_Aktuell:
				fillAttributeValue(tfAU, AttributeType.Ausdauer_Aktuell, null, true, true);
				break;
			case Karmaenergie_Aktuell:
				fillAttributeValue(tfKE, AttributeType.Karmaenergie_Aktuell, null, true, true);
				break;
			case Astralenergie_Aktuell:
				fillAttributeValue(tfAE, AttributeType.Astralenergie_Aktuell, null, true, true);
				break;
			case Behinderung:
				fillAttributeValue(tfBE, AttributeType.Behinderung, null, true, true);
				fillAttributeValue(tfGS, AttributeType.Geschwindigkeit, null, true, true);
				break;
			case Geschwindigkeit:
				fillAttributeValue(tfGS, AttributeType.Geschwindigkeit, null, true, true);
				break;
			default:
				break;
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onModifierAdded(com.dsatab.data.modifier .Modificator)
	 */
	@Override
	public void onModifierAdded(Modificator value) {
		super.onModifierAdded(value);
		fillAttributesList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onModifierChanged(com.dsatab.data.modifier .Modificator)
	 */
	@Override
	public void onModifierChanged(Modificator value) {
		super.onModifierChanged(value);
		fillAttributesList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onModifierRemoved(com.dsatab.data.modifier .Modificator)
	 */
	@Override
	public void onModifierRemoved(Modificator value) {
		super.onModifierRemoved(value);
		fillAttributesList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onModifiersChanged(java.util.List)
	 */
	@Override
	public void onModifiersChanged(List<Modificator> values) {
		super.onModifiersChanged(values);
		fillAttributesList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMenuActivity#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {
		if (tfName != null)
			tfName.setText(hero.getName());

		fillAttributesList();
	}

	protected void fillAttributesList() {

		fillAttributeValue(tfMU, AttributeType.Mut, null, true, true);
		fillAttributeValue(tfKL, AttributeType.Klugheit, null, true, true);
		fillAttributeValue(tfIN, AttributeType.Intuition, null, true, true);
		fillAttributeValue(tfCH, AttributeType.Charisma, null, true, true);
		fillAttributeValue(tfFF, AttributeType.Fingerfertigkeit, null, true, true);
		fillAttributeValue(tfGE, AttributeType.Gewandtheit, null, false, true);
		fillAttributeValue(tfKO, AttributeType.Konstitution, null, true, true);
		fillAttributeValue(tfKK, AttributeType.Körperkraft, null, true, true);

		fillAttributeLabel(tfLabelMU, AttributeType.Mut);
		fillAttributeLabel(tfLabelKL, AttributeType.Klugheit);
		fillAttributeLabel(tfLabelIN, AttributeType.Intuition);
		fillAttributeLabel(tfLabelCH, AttributeType.Charisma);
		fillAttributeLabel(tfLabelFF, AttributeType.Fingerfertigkeit);
		fillAttributeLabel(tfLabelGE, AttributeType.Gewandtheit);
		fillAttributeLabel(tfLabelKO, AttributeType.Konstitution);
		fillAttributeLabel(tfLabelKK, AttributeType.Körperkraft);

		fillAttributeValue(tfLE, AttributeType.Lebensenergie_Aktuell, null, true, true);
		fillAttributeLabel(tfLabelLE, AttributeType.Lebensenergie_Aktuell);

		fillAttributeValue(tfAU, AttributeType.Ausdauer_Aktuell, null, true, true);
		fillAttributeLabel(tfLabelAU, AttributeType.Ausdauer_Aktuell);

		fillAttributeValue(tfMR, AttributeType.Magieresistenz, null, true, true);
		fillAttributeLabel(tfLabelMR, AttributeType.Magieresistenz);

		final Hero hero = getHero();

		if (hero.getAttributeValue(AttributeType.Karmaenergie) == null
				|| hero.getAttributeValue(AttributeType.Karmaenergie) == 0) {
			tfKE.setVisibility(View.GONE);
			tfLabelKE.setVisibility(View.GONE);
		} else if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_KE, true)) {
			fillAttributeValue(tfKE, AttributeType.Karmaenergie_Aktuell, null, true, true);
			fillAttributeLabel(tfLabelKE, AttributeType.Karmaenergie_Aktuell);
			tfKE.setVisibility(View.VISIBLE);
			tfLabelKE.setVisibility(View.VISIBLE);
		}

		if (hero.getAttributeValue(AttributeType.Astralenergie) == null
				|| hero.getAttributeValue(AttributeType.Astralenergie) == 0) {
			tfAE.setVisibility(View.GONE);
			tfLabelAE.setVisibility(View.GONE);
		} else if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_HEADER_AE, true)) {
			fillAttributeValue(tfAE, AttributeType.Astralenergie_Aktuell, null, true, true);
			fillAttributeLabel(tfLabelAE, AttributeType.Astralenergie_Aktuell);
			tfAE.setVisibility(View.VISIBLE);
			tfLabelAE.setVisibility(View.VISIBLE);
		}

		fillAttributeLabel(tfLabelGS, AttributeType.Geschwindigkeit);
		fillAttributeValue(tfGS, AttributeType.Geschwindigkeit, null, true, true);

		fillAttributeValue(tfBE, AttributeType.Behinderung, null, true, true);
		fillAttributeLabel(tfLabelBE, AttributeType.Behinderung);

		if (tfWS != null) {
			int[] ws = hero.getWundschwelle();
			tfWS.setText(ws[0] + "/" + ws[1] + "/" + ws[2]);
		}

	}

	public void onHeroUnloaded(Hero hero) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onSharedPreferenceChanged(android.content .SharedPreferences,
	 * java.lang.String)
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.startsWith("header_")) {
			updateView();
		}
		super.onSharedPreferenceChanged(sharedPreferences, key);
	}

}
