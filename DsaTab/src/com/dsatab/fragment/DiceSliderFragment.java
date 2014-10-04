package com.dsatab.fragment;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Art;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.Dice;
import com.dsatab.data.Dice.DiceRoll;
import com.dsatab.data.Hero;
import com.dsatab.data.Probe;
import com.dsatab.data.Probe.ProbeType;
import com.dsatab.data.Spell;
import com.dsatab.data.adapter.ModifierAdapter;
import com.dsatab.data.adapter.ModifierAdapter.OnModifierChangedListener;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.modifier.Modifier;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.dsatab.view.dialog.TakeHitDialog;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

public class DiceSliderFragment extends BaseFragment implements View.OnClickListener, OnModifierChangedListener,
		PanelSlideListener {

	public static final String TAG = "diceSliderFragment";

	private final int SCHNELLSCHUSS_INDEX = 13;
	private static final String EVADE_GEZIELT = "Gezieltes Ausweichen DK-Mod. x2";

	private static final int DICE_DELAY = 1000;

	private static final int HANDLE_DICE_20 = 1;
	private static final int HANDLE_DICE_6 = 2;

	private SlidingUpPanelLayout slidingUpPanelLayout;

	private boolean modifierVisible = true;

	private ViewGroup sliderTitle, sliderAttributes;

	private TextView tfDiceTalentName;

	private TextView tfDiceTalentValue, tfDiceAttributeValues, tfEffectValue;

	private ImageView tfDice20, tfDice6;

	private ImageButton detailsSwitch;

	private boolean animate = true;
	private LinearLayout linDiceResult;

	private ImageButton executeButton, takeHitButton;

	private Animation shakeDice20;
	private Animation shakeDice6;

	private DiceHandler mHandler;

	private NumberFormat effectFormat = NumberFormat.getNumberInstance();

	private NumberFormat probabilityFormat = NumberFormat.getPercentInstance();

	private List<Modifier> modifiers;

	private ProbeData probeData;

	private ListView modifiersList;
	private View listDivider;
	private ModifierAdapter modifierAdapter;

	private SharedPreferences preferences;

	private SoundPool sounds;

	private boolean probeAutoRoll = true;

	private int soundNeutral;
	private int soundWin;
	private int soundFail;

	private Context themedContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// clone the inflater using the ContextThemeWrapper
		LayoutInflater localInflater = inflater.cloneInContext(themedContext);
		// inflate using the cloned inflater, not the passed in default

		ViewGroup root = (ViewGroup) localInflater.inflate(R.layout.dice_slider_content, container, false);

		sliderTitle = (ViewGroup) root.findViewById(R.id.slider_title);
		sliderAttributes = (ViewGroup) root.findViewById(R.id.slider_attributes);

		modifiersList = (ListView) root.findViewById(R.id.probe_modifier_container);
		listDivider = root.findViewById(R.id.probe_modifier_container_divider);

		modifiersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Modifier modifier = (Modifier) modifiersList.getItemAtPosition(position);
				modifier.toggleActive();
				onModifierChanged(modifier);
			}
		});

		modifierAdapter = new ModifierAdapter(themedContext, new ArrayList<Modifier>());
		modifierAdapter.setOnModifierChangedListener(this);
		modifiersList.setAdapter(modifierAdapter);

		detailsSwitch = (ImageButton) root.findViewById(R.id.details_switch);
		detailsSwitch.setVisibility(View.INVISIBLE);

		tfDiceTalentName = (TextView) root.findViewById(R.id.dice_talent);
		tfDiceTalentValue = (TextView) root.findViewById(R.id.dice_talent_value);
		tfDiceAttributeValues = (TextView) root.findViewById(R.id.dice_value);
		tfEffectValue = (TextView) root.findViewById(R.id.dice_effect_value);

		tfDice20 = (ImageView) root.findViewById(R.id.dice_w20);
		tfDice20.setOnClickListener(this);

		tfDice6 = (ImageView) root.findViewById(R.id.dice_w6);
		tfDice6.setOnClickListener(this);

		executeButton = (ImageButton) root.findViewById(R.id.dice_execute);
		executeButton.setOnClickListener(this);
		executeButton.setVisibility(View.GONE);

		takeHitButton = (ImageButton) root.findViewById(R.id.dice_take_hit);
		takeHitButton.setOnClickListener(this);
		takeHitButton.setVisibility(View.GONE);

		linDiceResult = (LinearLayout) root.findViewById(R.id.dice_dice_result);
		linDiceResult.setOnClickListener(this);

		initLayoutTransitions(root);

		resetPanelInformation();
		return root;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// create ContextThemeWrapper from the original Activity Context with the custom theme
		themedContext = new ContextThemeWrapper(activity, R.style.DsaTabTheme_Dark);
	}

	public SlidingUpPanelLayout getSlidingUpPanelLayout() {
		return slidingUpPanelLayout;
	}

	public void setSlidingUpPanelLayout(SlidingUpPanelLayout slidingUpPanelLayout) {
		this.slidingUpPanelLayout = slidingUpPanelLayout;

		slidingUpPanelLayout.setDragView(findViewById(R.id.slider));
		slidingUpPanelLayout.setCoveredFadeColor(0);
		slidingUpPanelLayout.setPanelSlideListener(this);
		// slidingUpPanelLayout.setOverdrawHeight(10);
	}

	@Override
	public void onPanelSlide(View panel, float slideOffset) {
		sliderTitle.setVisibility(View.VISIBLE);
		sliderAttributes.setVisibility(View.VISIBLE);

		sliderTitle.setAlpha(slideOffset);
		sliderAttributes.setAlpha(1.0f - slideOffset);
	}

	@Override
	public void onPanelExpanded(View panel) {
		enableLayoutTransition();

		sliderTitle.setVisibility(View.VISIBLE);
		sliderAttributes.setVisibility(View.GONE);
	}

	@Override
	public void onPanelHidden(View panel) {

	}

	@Override
	public void onPanelCollapsed(View panel) {
		if (detailsSwitch.getVisibility() == View.VISIBLE) {
			detailsSwitch.startAnimation(AnimationUtils.loadAnimation(themedContext, android.R.anim.fade_out));
		}
		detailsSwitch.setVisibility(View.INVISIBLE);

		sliderTitle.setVisibility(View.GONE);
		sliderAttributes.setVisibility(View.VISIBLE);

		resetPanelInformation();

		shakeDice20.cancel();
		shakeDice6.cancel();

		disableLayoutTransition();
	}

	private void resetPanelInformation() {
		// tblDiceProbe.setVisibility(View.GONE);
		tfDiceTalentValue.setVisibility(View.GONE);
		tfDiceTalentValue.setText(null);

		tfDiceAttributeValues.setVisibility(View.GONE);
		tfDiceAttributeValues.setText(null);

		tfEffectValue.setVisibility(View.GONE);
		tfEffectValue.setText(null);

		executeButton.setVisibility(View.GONE);
		takeHitButton.setVisibility(View.GONE);
		detailsSwitch.setVisibility(View.INVISIBLE);
		linDiceResult.setBackgroundResource(0);

		if (modifierAdapter != null) {
			modifierAdapter.clear();
			listDivider.setVisibility(View.GONE);
		}

		if (probeData != null)
			probeData.clear();
	}

	@Override
	public void onPanelAnchored(View panel) {

	}

	@Override
	public void onDetach() {
		super.onDetach();

		mHandler.removeMessages(HANDLE_DICE_20);
		mHandler.removeMessages(HANDLE_DICE_6);

		themedContext = null;
	}

	@Override
	public void onHeroLoaded(Hero hero) {

	}

	private void disableLayoutTransition() {
		if (linDiceResult.getLayoutTransition() != null) {
			linDiceResult.setLayoutTransition(null);
		}
		animate = false;

	}

	private void enableLayoutTransition() {
		if (linDiceResult.getLayoutTransition() == null) {
			linDiceResult.setLayoutTransition(new LayoutTransition());
		}

		animate = true;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void initLayoutTransitions(ViewGroup root) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && modifiersList.getLayoutTransition() != null) {
			modifiersList.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new DiceHandler(this);

		effectFormat.setMaximumFractionDigits(1);

		preferences = DsaTabApplication.getPreferences();

		sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
		soundNeutral = sounds.load(themedContext, R.raw.dice, 1);
		soundWin = sounds.load(themedContext, R.raw.dice_win, 1);
		soundFail = sounds.load(themedContext, R.raw.dice_fail, 1);

		modifierVisible = preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SHOW_MODIFIKATORS, true);

		shakeDice20 = AnimationUtils.loadAnimation(themedContext, R.anim.shake);
		shakeDice6 = AnimationUtils.loadAnimation(themedContext, R.anim.shake);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dice_execute:
			clearDice();
			if (probeData != null) {
				probeData.sound = true;
				checkProbe(probeData);
			}
			break;
		case R.id.details_switch:
			setModifiersVisible(!isModifiersVisible(), true);
			break;

		case R.id.dice_take_hit:
			if (probeData != null) {
				TakeHitDialog inlineEditdialog = new TakeHitDialog(getActivity(), probeData.being, null);
				inlineEditdialog.show();
			}
			break;
		}
		if (v == tfDice20) {
			rollDice20();
		} else if (v == tfDice6) {
			rollDice6();
		} else if (v == linDiceResult) {
			linDiceResult.removeAllViews();
			linDiceResult.scrollTo(0, 0);
		} else if (v == tfDice20) {
			rollDice20();
		}

	}

	private boolean isModifiersVisible() {
		return modifierVisible;
	}

	public void onModifierChanged(Modifier mod) {
		if (getActivity() == null)
			return;

		if (EVADE_GEZIELT.equals(mod.getTitle())) {
			for (Modifier m : modifiers) {
				if (getString(R.string.label_distance).equals(m.getTitle())) {
					mod.setModifier(m.getModifier());
					break;
				}
			}
		} else if (getString(R.string.label_distance).equals(mod.getTitle())) {
			for (Modifier m : modifiers) {
				if (EVADE_GEZIELT.equals(m.getTitle())) {
					m.setModifier(mod.getModifier());
					break;
				}
			}

			if (probeData.probe instanceof CombatProbe) {
				CombatProbe combatProbe = (CombatProbe) probeData.probe;

				// combatProbe.getProbeInfo().setErschwernis(erschwernis);
				EquippedItem equippedItem = combatProbe.getEquippedItem();
				if (equippedItem != null && equippedItem.getItemSpecification() instanceof DistanceWeapon) {
					DistanceWeapon distanceWeapon = (DistanceWeapon) equippedItem.getItemSpecification();
					combatProbe.setTpModifier(distanceWeapon.getTpDistance(mod.getSpinnerIndex()));
				} else {
					combatProbe.setTpModifier(null);
				}
			}

		}

		Editor edit = getPreferences().edit();
		edit.putBoolean(Modifier.PREF_PREFIX_ACTIVE + mod.getTitle(), mod.isActive());
		edit.commit();

		Util.notifyDatasetChanged(modifiersList);

		updateProgressView(probeData, mod);
		if (isAutoRoll()) {
			probeData.sound = false;
			checkProbe(probeData, mod);
		}
	}

	private void showEffect(Double effect, Integer erschwernis, ProbeData info) {
		if (info.successOne == null)
			info.successOne = false;

		if (info.failureTwenty == null)
			info.failureTwenty = false;

		if (info.sound && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_ROLL_DICE, true)
				&& !preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_RESULT_DICE, true)) {
			sounds.play(soundNeutral, 1.0f, 1.0f, 0, 0, 1.0f);
		}

		CombatTalent combatTalent = getCombatTalent(info.probe);

		if (effect != null) {
			tfEffectValue.setVisibility(View.VISIBLE);
			tfEffectValue.setText(effectFormat.format(effect));
			if (erschwernis != null)
				tfEffectValue.append(" (" + erschwernis + ")");

			final int colorRed = getResources().getColor(R.color.ValueRed);
			final int colorGreen = getResources().getColor(R.color.ValueGreen);

			if (!isAttributesHidden(info) && info.value != null && info.dice != null) {
				StyleableSpannableStringBuilder valueSb = new StyleableSpannableStringBuilder();
				for (int i = 0; i < info.value.length; i++) {
					if (info.value[i] != null) {
						if (i > 0) {
							valueSb.append("/");
						}
						if (info.dice[i] != null && info.value[i] < info.dice[i])
							valueSb.appendColor(colorRed, Util.toString(info.value[i]));
						else
							valueSb.append(Util.toString(info.value[i]));
					}
				}
				tfDiceAttributeValues.setText(valueSb);
			}

			// misslungen
			if ((effect < 0 && !info.successOne) || info.failureTwenty) {
				tfEffectValue.setTextColor(colorRed);

				// bestätigter patzer
				if (info.failureTwenty && effect < 0) {
					tfDiceTalentName.setText(info.probe.getName() + " total verpatzt!");

					linDiceResult.setBackgroundResource(R.drawable.probe_red_highlight);

					if (combatTalent instanceof CombatDistanceTalent) {
						tfDiceTalentName.append(" | ");
						tfDiceTalentName.append(getFailureDistance());

					} else if (combatTalent instanceof CombatMeleeTalent
							|| combatTalent instanceof CombatMeleeAttribute) {
						tfDiceTalentName.append(" | ");
						tfDiceTalentName.append(getFailureMelee());
					}

				} else { // unbestätigter patzer
					linDiceResult.setBackgroundResource(0);
					tfDiceTalentName.setText(info.probe.getName() + " misslungen");
				}
				tfDiceTalentName.setTextColor(colorRed);

				if (isCombatDefense(info.probe)) {
					takeHitButton.setVisibility(View.VISIBLE);

					Hint.showHint(getClass().getSimpleName(), "TAKE_HIT_SLIDER", getActivity());
				} else {
					takeHitButton.setVisibility(View.GONE);
				}

				if (info.sound && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_ROLL_DICE, true)
						&& preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_RESULT_DICE, true)) {
					sounds.play(soundFail, 1.0f, 1.0f, 0, 0, 1.0f);
				}
			} else { // geschafft

				if (info.successOne && effect >= 0) {
					tfDiceTalentName.setText(info.probe.getName() + " glücklich gemeistert!");
					tfEffectValue.setTextColor(colorGreen);
					tfDiceTalentName.setTextColor(colorGreen);
					linDiceResult.setBackgroundResource(R.drawable.probe_green_highlight);
				} else {
					tfDiceTalentName.setText(info.probe.getName() + " gelungen");
					tfEffectValue.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
					tfDiceTalentName.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
					linDiceResult.setBackgroundResource(0);
				}

				StringBuilder sb = new StringBuilder();
				if (info.tp != null) {
					sb.append(Integer.toString(info.tp));
					sb.append(" TP ");
				} else if (combatTalent != null && info.target != null) {
					sb.append("Treffer ");
				}

				if (combatTalent != null && info.target != null) {
					sb.append("auf ");
					sb.append(combatTalent.getPosition(info.target).getName());
					// sb.append(" (" + info.target + ")");
				}
				if (sb.length() > 0) {
					tfDiceTalentName.append(" | " + sb);
				}

				if (info.sound && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_ROLL_DICE, true)
						&& preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_RESULT_DICE, true)) {
					sounds.play(soundWin, 1.0f, 1.0f, 0, 0, 1.0f);
				}

				takeHitButton.setVisibility(View.GONE);
			}
		} else {
			takeHitButton.setVisibility(View.GONE);
			tfEffectValue.setVisibility(View.INVISIBLE);
		}
	}

	public void clearDice() {
		// clear any pending dice still in queue
		mHandler.removeMessages(HANDLE_DICE_20);
		mHandler.removeMessages(HANDLE_DICE_6);

		linDiceResult.removeAllViews();
		if (probeData != null) {
			probeData.clearDice();
		}

	}

	public void checkProbe(AbstractBeing hero, Probe probe, boolean autoRoll) {

		if (getActivity() != null) {
			Hint.showRandomHint("DiceSlider", getActivity());
		}
		this.probeAutoRoll = autoRoll;

		clearDice();

		Integer value1 = null;
		Integer value2 = null;
		Integer value3 = null;

		switch (probe.getProbeType()) {
		case ThreeOfThree:
			value1 = probe.getProbeValue(0);
			value2 = probe.getProbeValue(1);
			value3 = probe.getProbeValue(2);
			break;
		case TwoOfThree:
		case One:
			value1 = value2 = value3 = probe.getProbeValue(0);
			break;
		}

		populateModifiers(hero, probe);

		if (probeData == null)
			probeData = new ProbeData();
		else
			probeData.clear();

		probeData.being = hero;
		probeData.probe = probe;
		probeData.value = new Integer[] { value1, value2, value3 };

		if (!slidingUpPanelLayout.isPanelExpanded())
			animate = false;

		if (isAutoRoll()) {
			executeButton.setVisibility(View.GONE);
			updateView(probeData);
			checkProbe(probeData);
		} else {
			updateView(probeData);
			executeButton.setVisibility(View.VISIBLE);
		}

		getView().postDelayed(new Runnable() {

			@Override
			public void run() {
				ensureVisibility();
			}
		}, 200);

	}

	private void populateModifiers(AbstractBeing hero, Probe probe) {
		modifiers = hero.getModifiers(probe, true, true);

		if (probe.getProbeInfo().getErschwernis() != null && probe.getProbeInfo().getErschwernis() != 0) {
			Modifier erschwernisModifier = new Modifier(0, "Probenerschwernis");
			erschwernisModifier.setModifier(-1 * probe.getProbeInfo().getErschwernis());
			erschwernisModifier.setActive(getPreferences().getBoolean(
					Modifier.PREF_PREFIX_ACTIVE + erschwernisModifier.getTitle(), true));

			addModifier(erschwernisModifier);
		}

		if (probe instanceof CombatProbe) {

			CombatProbe combatProbe = (CombatProbe) probe;

			EquippedItem equippedItem = combatProbe.getEquippedItem();
			if (equippedItem != null && equippedItem.getItemSpecification() instanceof DistanceWeapon) {

				String[] distances = getResources().getStringArray(R.array.archeryDistance);
				DistanceWeapon item = equippedItem.getItem().getSpecification(DistanceWeapon.class);
				if (item != null) {
					String from, to;

					for (int i = 0; i < distances.length; i++) {
						to = item.getDistance(i);

						if (to != null) {
							distances[i] += " (";
							if (i > 0) {
								from = item.getDistance(i - 1);
								distances[i] += from;
							}

							distances[i] += " bis " + to + "m)";
						}
					}
				}

				Modifier distance = new Modifier(0, getString(R.string.label_distance));
				distance.setSpinnerOptions(distances);
				distance.setSpinnerValues(getResources().getIntArray(R.array.archeryDistanceValues));
				addModifier(distance);

				Modifier size = new Modifier(0, getString(R.string.label_size));
				size.setSpinnerOptions(getResources().getStringArray(R.array.archerySize));
				size.setSpinnerValues(getResources().getIntArray(R.array.archerySizeValues));
				addModifier(size);

				int[] modificationValues = getResources().getIntArray(R.array.archeryModificationValues);
				String[] modificationStrings = getResources().getStringArray(R.array.archeryModificationStrings);
				if (hero.hasFeature(FeatureType.Meisterschütze)) {
					modificationStrings[SCHNELLSCHUSS_INDEX] = "Schnellschuß +0";
					modificationValues[SCHNELLSCHUSS_INDEX] = 0;

				} else if (hero.hasFeature(FeatureType.Scharfschütze)) {
					modificationStrings[SCHNELLSCHUSS_INDEX] = "Schnellschuß +1";
					modificationValues[SCHNELLSCHUSS_INDEX] = 1;
				}

				for (int i = 0; i < modificationStrings.length; i++) {
					Modifier mod = new Modifier(-modificationValues[i], modificationStrings[i]);
					mod.setActive(false);
					addModifier(mod);
				}
			}
		}

		if (probe instanceof Attribute) {
			Attribute attribute = (Attribute) probe;

			switch (attribute.getType()) {
			case Ausweichen:
				Modifier distance = new Modifier(0, "Abstand");
				distance.setSpinnerOptions(getResources().getStringArray(R.array.evadeDistance));
				distance.setSpinnerValues(getResources().getIntArray(R.array.evadeDistanceValues));
				addModifier(distance);

				Modifier enemies = new Modifier(0, getString(R.string.label_enemy));
				enemies.setSpinnerOptions(getResources().getStringArray(R.array.evadeEnemy));
				enemies.setSpinnerValues(getResources().getIntArray(R.array.evadeEnemyValues));
				addModifier(enemies);

				Modifier modGezielt = new Modifier(0, EVADE_GEZIELT);
				addModifier(modGezielt);

				String[] modStrings = getResources().getStringArray(R.array.evadeModificationStrings);
				int[] modValues = getResources().getIntArray(R.array.evadeModificationValues);
				for (int i = 0; i < modStrings.length; i++) {
					Modifier mod = new Modifier(-modValues[i], modStrings[i]);
					mod.setActive(false);
					addModifier(mod);
				}
				break;
			}
		}

		Modifier manualModifer = new Modifier(0, Modifier.TITLE_MANUAL, Modifier.TITLE_MANUAL);
		addModifier(manualModifer);

	}

	protected boolean isAutoRoll() {
		return probeAutoRoll && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_AUTO_ROLL_DICE, true);
	}

	private void ensureVisibility() {
		showPanel();
		expandPanel();
	}

	public void expandPanel() {
		if (!slidingUpPanelLayout.isPanelExpanded() && !slidingUpPanelLayout.isPanelSliding()) {
			disableLayoutTransition();
			slidingUpPanelLayout.expandPanel();
		}
	}

	public void collapsePanel() {
		if (slidingUpPanelLayout.isPanelExpanded() && !slidingUpPanelLayout.isPanelSliding()) {
			disableLayoutTransition();
			slidingUpPanelLayout.collapsePanel();
		}
	}

	public void showPanel() {
		if (slidingUpPanelLayout.isPanelHidden()) {
			slidingUpPanelLayout.showPanel();
		}
	}

	public void hidePanel() {
		if (!slidingUpPanelLayout.isPanelHidden()) {
			slidingUpPanelLayout.hidePanel();
		}
	}

	public boolean isPanelExpanded() {
		return (slidingUpPanelLayout != null && slidingUpPanelLayout.isPanelExpanded());
	}

	private void updateProgressView(ProbeData info, Modifier... modificators) {
		Probe probe = probeData.probe;
		if (probe == null) {
			return;
		}

		addModifier(modificators);
		int modifiersSum = Modifier.sum(modifiers);

		if (probe.getProbeBonus() != null) {
			tfDiceTalentValue.setText(Integer.toString(probe.getProbeBonus()));
			tfDiceTalentValue.setVisibility(View.VISIBLE);
			if (modifiersSum != 0) {
				tfDiceTalentValue.append(" ");
				tfDiceTalentValue.append(Util.toProbe(modifiersSum));
			}
		} else {
			tfDiceTalentValue.setText(null);
		}

		// if no probe is present and all values are the same display them as
		// bonus
		if (isAttributesHidden(info)) {
			tfDiceAttributeValues.setHint(null);
			tfDiceAttributeValues.setVisibility(View.GONE);

			if (info.value[0] != null) {
				tfDiceTalentValue.setText(Util.toString(info.value[0]));
				tfDiceTalentValue.setVisibility(View.VISIBLE);
			} else {
				tfDiceTalentValue.setVisibility(View.INVISIBLE);
			}

			if (modifiersSum != 0) {
				tfDiceTalentValue.append(" ");
				tfDiceTalentValue.append(Util.toProbe(modifiersSum));
			}
		} else {
			tfDiceAttributeValues.setHint(probe.getProbeInfo().getAttributesString());
			tfDiceAttributeValues.setText(Util.toString(info.value[0]) + "/" + Util.toString(info.value[1]) + "/"
					+ Util.toString(info.value[2]));
			tfDiceAttributeValues.setVisibility(View.VISIBLE);
		}

		if (probe != null) {
			int taw = 0;
			if (probe.getProbeBonus() != null) {
				taw += probe.getProbeBonus();
			}
			taw += modifiersSum;

			tfEffectValue.setVisibility(View.VISIBLE);
			if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_PROBABILITY, false)) {
				Double probability = null;

				// House rule preferences
				ProbeType probeType = probe.getProbeType();
				if (probeType == ProbeType.TwoOfThree
						&& preferences.getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_2_OF_3_DICE, false) == false) {
					probeType = ProbeType.One;
				}

				switch (probeType) {

				case ThreeOfThree:

					if (info.value[0] != null && info.value[1] != null && info.value[2] != null) {
						probability = DsaUtil.testTalent(info.value[0], info.value[1], info.value[2], taw);
						// Debug.verbose("Change for success is :" + probability);
					}

					break;
				case TwoOfThree:

					if (info.value[0] != null) {
						probability = DsaUtil.testEigen(info.value[0], taw);
						// Debug.verbose("Change for success is :" + probability);
					}
					break;
				case One:
					if (info.value[0] != null) {
						probability = DsaUtil.testEigen(info.value[0], taw);
						// Debug.verbose("Change for success is :" + probability);
					}
					break;
				}

				tfEffectValue.setTextColor(DsaTabApplication.getInstance().getResources()
						.getColor(android.R.color.secondary_text_dark));

				if (probability != null) {
					tfEffectValue.setText(probabilityFormat.format(probability));
				} else {
					tfEffectValue.setText(null);
				}
			} else {
				tfEffectValue.setText(null);
			}

		}

	}

	private boolean isAttributesHidden(ProbeData info) {
		Probe probe = probeData.probe;

		return (probe instanceof CombatDistanceTalent || probe instanceof CombatMeleeAttribute || (probe.getProbeInfo()
				.getAttributeValues().isEmpty()
				&& info.value[0] == info.value[1] && info.value[1] == info.value[2]));
	}

	private void updateView(ProbeData info) {

		tfDiceTalentName.setText(probeData.probe.getName());
		tfDiceTalentName.setTextColor(getResources().getColor(android.R.color.primary_text_dark));

		if (!isAutoRoll()) {
			executeButton.setImageResource(DsaUtil.getResourceId(probeData.probe));
		}
		detailsSwitch.setOnClickListener(this);

		setModifiersVisible(isModifiersVisible(), false);

		updateProgressView(probeData);
	}

	private void setModifiersVisible(boolean visible, final boolean animate) {

		if (detailsSwitch.getVisibility() != View.VISIBLE) {
			detailsSwitch.startAnimation(AnimationUtils.loadAnimation(themedContext, android.R.anim.fade_in));
		}
		detailsSwitch.setVisibility(View.VISIBLE);

		modifierVisible = visible;

		if (isModifiersVisible()) {
			if (animate) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(detailsSwitch, "rotation", 0f, 180f);
				animator.setTarget(detailsSwitch);
				animator.setDuration(500);
				animator.start();
			} else {
				detailsSwitch.setRotation(180f);
			}

			// --

			modifierAdapter.clear();
			modifierAdapter.addAll(modifiers);

		} else {
			if (animate) {
				ObjectAnimator animator = ObjectAnimator.ofFloat(detailsSwitch, "rotation", 180f, 0f);
				animator.setTarget(detailsSwitch);
				animator.setDuration(500);
				animator.start();
			} else {
				detailsSwitch.setRotation(0f);
			}

			modifierAdapter.clear();

		}

		if (modifierAdapter.isEmpty())
			listDivider.setVisibility(View.GONE);
		else
			listDivider.setVisibility(View.VISIBLE);
	}

	private CombatTalent getCombatTalent(Probe probe) {
		CombatTalent lastCombatTalent = null;
		if (probe instanceof CombatProbe) {
			lastCombatTalent = ((CombatProbe) probe).getCombatTalent();
		} else if (probe instanceof CombatTalent) {
			lastCombatTalent = (CombatTalent) probe;
		} else if (probe instanceof CombatMeleeAttribute) {
			lastCombatTalent = ((CombatMeleeAttribute) probe).getTalent();
		}

		return lastCombatTalent;
	}

	private boolean isCombatDefense(Probe probe) {
		if (probe instanceof CombatProbe) {
			return !((CombatProbe) probe).isAttack();
		} else if (probe instanceof CombatTalent) {
			return false;
		} else if (probe instanceof CombatMeleeAttribute) {
			return !((CombatMeleeAttribute) probe).isAttack();
		} else if (probe instanceof Attribute) {
			Attribute attribute = (Attribute) probe;
			return attribute.getType() == AttributeType.Ausweichen;
		} else {
			return false;
		}
	}

	protected void addModifier(Modifier... mods) {
		for (Modifier mod : mods) {
			if (!modifiers.contains(mod)) {
				mod.setActive(getPreferences().getBoolean(Modifier.PREF_PREFIX_ACTIVE + mod.getTitle(), mod.isActive()));

				if (mod.getSpinnerOptions() != null && mod.getSpinnerValues() != null) {
					int index = preferences.getInt(Modifier.PREF_PREFIX_SPINNER_INDEX + mod.getTitle(), 0);
					if (index < 0 || index >= mod.getSpinnerValues().length) {
						index = 0;
					}
					mod.setSpinnerIndex(index);
					mod.setModifier(-mod.getSpinnerValues()[index]);
				}
				modifiers.add(mod);
			}
		}
	}

	private Double checkProbe(ProbeData info, Modifier... modificators) {

		probeData = info;
		Probe probe = probeData.probe;

		addModifier(modificators);
		int modifiersSum = Modifier.sum(modifiers);

		// special case ini
		if (probe instanceof Attribute) {
			Attribute attribute = (Attribute) probe;
			if (attribute.getType() == AttributeType.ini) {
				if (info.dice[0] == null)
					info.dice[0] = rollDice6();

				double effect;

				if (info.being.hasFeature(FeatureType.Klingentänzer)) {
					if (info.dice[1] == null)
						info.dice[1] = rollDice6();

					effect = info.being.getAttributeValue(AttributeType.ini) + info.dice[0] + info.dice[1]
							+ modifiersSum;
				} else {
					effect = info.being.getAttributeValue(AttributeType.ini) + info.dice[0] + modifiersSum;
				}

				showEffect(effect, null, info);

				info.being.getAttribute(AttributeType.Initiative_Aktuell).setValue((int) effect);
				return effect;
			}
		}

		Double effect = null;

		Integer erschwernis = null;

		int diceRolled = 0;

		if (info.value[0] != null && info.value[1] != null && info.value[2] != null) {

			effect = 0.0;

			int taw = 0;
			if (probe.getProbeBonus() != null) {
				taw += probe.getProbeBonus();
			}
			taw += modifiersSum;

			int valueModifier = 0;
			if (taw < 0) {
				valueModifier = taw;
			} else {
				effect = Double.valueOf(taw);
			}

			// House rule preferences
			ProbeType probeType = probe.getProbeType();
			if (probeType == ProbeType.TwoOfThree
					&& preferences.getBoolean(DsaTabPreferenceActivity.KEY_HOUSE_RULES_2_OF_3_DICE, false) == false) {
				probeType = ProbeType.One;
			}

			int PATZER_THRESHOLD = 20;
			if ((probe instanceof Spell || probe instanceof Art)) {
				if (info.being.hasFeature(FeatureType.WildeMagie)) {
					PATZER_THRESHOLD = 19;
				}
			} else {
				if (info.being.hasFeature(FeatureType.Tollpatsch)) {
					PATZER_THRESHOLD = 19;
				}
			}
			switch (probeType) {

			case ThreeOfThree: {

				if (info.dice[0] == null)
					info.dice[0] = rollDice20(DICE_DELAY * 0, info.value[0] + valueModifier);

				if (info.dice[1] == null)
					info.dice[1] = rollDice20(DICE_DELAY * 1, info.value[1] + valueModifier);

				if (info.dice[2] == null)
					info.dice[2] = rollDice20(DICE_DELAY * 2, info.value[2] + valueModifier);

				int w20Count = 0, w1Count = 0;
				for (int i = 0; i < 3; i++) {
					if (info.dice[i] >= PATZER_THRESHOLD)
						w20Count++;

					if (info.dice[i] == 1)
						w1Count++;
				}
				if (w20Count >= 2) {
					info.failureTwenty = Boolean.TRUE;

					// Wege des Helden 251: Patzer bei Zaubern nur wenn der
					// dritte Würfel auch 18,19,20 ist.
					if (probe instanceof Spell && info.being.hasFeature(FeatureType.FesteMatrix)) {
						for (int i = 0; i < 3; i++) {
							// sobald einer der Würfel unter 18 ist, kann es
							// kein Patzer gewesen sein
							if (info.dice[i] < 18) {
								info.failureTwenty = Boolean.FALSE;
								break;
							}
						}

					}

				}
				if (w1Count >= 2)
					info.successOne = Boolean.TRUE;

				int effect1 = (info.value[0] + valueModifier) - info.dice[0];
				int effect2 = (info.value[1] + valueModifier) - info.dice[1];
				int effect3 = (info.value[2] + valueModifier) - info.dice[2];

				if (effect1 < 0) {
					// Debug.verbose("Dice1 fail result=" + effect1);
					effect += effect1;
				}
				if (effect2 < 0) {
					// Debug.verbose("Dice2 fail result=" + effect2);
					effect += effect2;
				}
				if (effect3 < 0) {
					// Debug.verbose("Dice3 fail result=" + effect3);
					effect += effect3;
				}
				break;
			}
			case TwoOfThree: {

				if (info.dice[0] == null) {

					info.dice[0] = rollDice20(DICE_DELAY * diceRolled++, info.value[0] + taw);

					if (info.dice[0] == 1) {
						info.successOne = Boolean.TRUE;
						info.dice[0] = rollDice20(DICE_DELAY * 3, info.value[0] + taw);
					} else if (info.dice[0] >= PATZER_THRESHOLD) {
						info.failureTwenty = Boolean.TRUE;
						info.dice[0] = rollDice20(DICE_DELAY * 3, info.value[0] + taw);
					}
				}
				if (info.dice[1] == null)
					info.dice[1] = rollDice20(DICE_DELAY * diceRolled++, info.value[1] + taw);

				if (info.dice[2] == null)
					info.dice[2] = rollDice20(DICE_DELAY * diceRolled++, info.value[2] + taw);

				int[] dices = new int[] { info.dice[0], info.dice[1], info.dice[2] };
				Arrays.sort(dices);
				// we only need the two best
				int dice1 = dices[0];
				int dice2 = dices[1];

				// Debug.verbose("Value Modifier (Be, Wm, Manuell) " + taw);

				// check for success
				int effect1 = info.value[0] - dice1 + taw;
				int effect2 = info.value[1] - dice2 + taw;

				// full success or failure
				if ((effect1 >= 0 && effect2 >= 0) || (effect1 < 0 && effect2 < 0)) {
					effect = (double) effect1 + effect2;
				} else if (effect1 < 0) {
					effect = (double) effect1;
				} else { // effect2 < 0
					effect = (double) effect2;
				}

				effect = (effect / 2.0);

				if (effect >= 0) {
					erschwernis = Math.min(effect1, effect2);
				}

				break;
			}
			case One: {

				if (info.dice[0] == null) {
					info.dice[0] = rollDice20(DICE_DELAY * diceRolled++, info.value[0] + taw);

					if (info.dice[0] == 1) {
						info.dice[0] = rollDice20(DICE_DELAY * diceRolled++, info.value[0] + taw);
						info.successOne = Boolean.TRUE;
					} else if (info.dice[0] >= PATZER_THRESHOLD) {
						info.dice[0] = rollDice20(DICE_DELAY * diceRolled++, info.value[0] + taw);
						info.failureTwenty = Boolean.TRUE;
					}
				}

				Debug.verbose("Value Modifier (Be, Wm) " + taw);

				// check for success
				effect = (double) info.value[0] - info.dice[0] + taw;

				break;
			}
			}
		}

		CombatTalent combatTalent = getCombatTalent(info.probe);
		if (combatTalent != null) {
			if (info.target == null) {
				info.target = Dice.dice(20);
			}
		}

		if (probe instanceof CombatProbe) {
			CombatProbe combatProbe = (CombatProbe) probe;
			EquippedItem item = combatProbe.getEquippedItem();
			// remove target in case of defense
			if (!combatProbe.isAttack()) {
				info.target = null;
			}

			boolean success = ((info.successOne != null && info.successOne) || (effect != null && effect >= 0))
					&& (info.failureTwenty == null || !info.failureTwenty);

			if (item != null && info.tp == null && combatProbe.isAttack() && success) {
				boolean realSuccessOne = info.successOne != null && info.successOne && effect >= 0;
				int diceSize = info.tpDices.size();
				if (item.getItemSpecification() instanceof Weapon) {
					Weapon weapon = (Weapon) item.getItemSpecification();

					info.tp = weapon.getTp(info.being.getModifiedValue(AttributeType.Körperkraft, true, true),
							info.being.getModifierTP(item), realSuccessOne, info.tpDices);

				} else if (item.getItemSpecification() instanceof DistanceWeapon) {
					DistanceWeapon weapon = (DistanceWeapon) item.getItemSpecification();

					info.tp = weapon.getTp(info.being.getModifiedValue(AttributeType.Körperkraft, true, true),
							info.being.getModifierTP(item), realSuccessOne, info.tpDices);
				}

				for (int i = diceSize; i < info.tpDices.size(); i++) {
					DiceRoll diceRoll = info.tpDices.get(i);
					if (diceRoll.dice == 6)
						rollDice6(DICE_DELAY * diceRolled++, diceRoll.result);
					else if (diceRoll.dice == 20)
						rollDice20(DICE_DELAY * diceRolled++, diceRoll.result, -1);
				}

				if (info.tp != null && combatProbe.getTpModifier() != null) {
					info.tp += combatProbe.getTpModifier();
				}
			}
		}

		showEffect(effect, erschwernis, info);

		return effect;
	}

	protected int rollDice20(int delay, int referenceValue) {
		int result = Dice.dice(20);
		return rollDice20(delay, result, referenceValue);
	}

	protected int rollDice20(int delay, int result, int referenceValue) {
		if (animate && delay > 0 && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {

			if ((!shakeDice20.hasStarted() || shakeDice20.hasEnded())) {
				shakeDice20.reset();
				tfDice20.startAnimation(shakeDice20);
			}
			mHandler.sendMessageDelayed(Message.obtain(mHandler, HANDLE_DICE_20, result, referenceValue), delay);
		} else {
			showDice20(result, referenceValue);
		}
		return result;
	}

	public int rollDice20() {
		return rollDice20(DICE_DELAY, -1);
	}

	protected int rollDice6(int delay, int result) {
		if (animate && delay > 0 && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {
			if ((!shakeDice6.hasStarted() || shakeDice6.hasEnded())) {
				shakeDice6.reset();
				tfDice6.startAnimation(shakeDice6);
			}

			mHandler.sendMessageDelayed(Message.obtain(mHandler, HANDLE_DICE_6, result, 0), delay);
		} else {
			showDice6(result);
		}
		return result;
	}

	public int rollDice6() {
		int result = Dice.dice(6);
		return rollDice6(DICE_DELAY, result);
	}

	private TextView showDice20(int value, int referenceValue) {
		TextView res = new TextView(themedContext);

		int width = getResources().getDimensionPixelSize(R.dimen.dices_size);
		int padding = getResources().getDimensionPixelSize(R.dimen.default_gap);

		res.setWidth(width);
		res.setHeight(width);

		if (referenceValue < 0 || value <= referenceValue)
			res.setBackgroundResource(R.drawable.w20_empty);
		else
			res.setBackgroundResource(R.drawable.w20_red_empty);

		res.setText(Integer.toString(value));
		res.setTextColor(Color.WHITE);

		res.setTypeface(Typeface.DEFAULT_BOLD);
		res.setGravity(Gravity.CENTER);
		res.setPadding(padding, 0, padding, 0);
		res.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width - res.getPaddingTop() - res.getPaddingBottom()) / 3);

		addDice(res, width, width);

		return res;
	}

	private void addDice(View res, int width, int height) {
		linDiceResult.addView(res, width, width);
		if (animate && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {
			res.startAnimation(AnimationUtils.loadAnimation(themedContext, R.anim.flip_in));
		}

		if (linDiceResult.getChildCount() > 1 && linDiceResult.getWidth() > 0
				&& linDiceResult.getChildCount() * width > linDiceResult.getWidth()) {
			View dice = linDiceResult.getChildAt(0);

			if (animate) {
				dice.startAnimation(AnimationUtils.loadAnimation(themedContext, R.anim.dice_right));
			}
			linDiceResult.removeViewAt(0);
		}
	}

	private ImageView showDice6(int value) {
		ImageView res = new ImageView(themedContext);

		int width = getResources().getDimensionPixelSize(R.dimen.dices_size);
		int padding = getResources().getDimensionPixelSize(R.dimen.default_gap);

		res.setImageResource(Util.getDrawableByName("w6_" + value));
		res.setPadding(padding, 0, padding, 0);

		addDice(res, width, width);

		return res;
	}

	private String getFailureMelee() {
		int w6 = Dice.dice(6) + Dice.dice(6);

		switch (w6) {
		case 2:
			return "Waffe zerstört";
		case 3:
		case 4:
		case 5:
			return "Sturz";
		case 6:
		case 7:
		case 8:
			return "Stolpern";
		case 9:
		case 10:
			return "Waffe verloren";
		case 11:
			return "Selbst verletzt (TP Waffe)";
		case 12:
			return "Schwerer Eigentreffer (2x TP Waffe)";
		default:
			return "Ungültiger Wert: " + w6;
		}

	}

	private String getFailureDistance() {
		int w6 = Dice.dice(6) + Dice.dice(6);

		switch (w6) {
		case 2:
			return "Waffe zerstört";
		case 3:
			return "Waffe beschädigt";
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			return "Fehlschuss";
		case 11:
		case 12:
			return "Kamerad getroffen";
		default:
			return "Ungültiger Wert: " + w6;
		}

	}

	static class DiceHandler extends Handler {

		private final WeakReference<DiceSliderFragment> diceSliderRef;

		/**
		 * 
		 */
		public DiceHandler(DiceSliderFragment diceSlider) {
			this.diceSliderRef = new WeakReference<DiceSliderFragment>(diceSlider);
		}

		@Override
		public void handleMessage(Message msg) {
			DiceSliderFragment diceSlider = diceSliderRef.get();

			if (diceSlider != null && diceSlider.getActivity() != null) {
				int result = msg.arg1;

				switch (msg.what) {
				case HANDLE_DICE_6: {
					diceSlider.showDice6(result);
					break;
				}
				case HANDLE_DICE_20: {
					diceSlider.showDice20(result, msg.arg2);
					break;
				}
				}
			}
		}
	}

	static class ProbeData {
		Integer[] value = new Integer[3];

		Integer[] dice = new Integer[3];

		Integer tp;
		List<DiceRoll> tpDices = new ArrayList<DiceRoll>(3);

		Integer target;

		AbstractBeing being;

		Probe probe;

		Boolean successOne, failureTwenty;

		boolean sound = true;

		public void clear() {
			value[0] = null;
			value[1] = null;
			value[2] = null;
			being = null;
			probe = null;
			sound = true;

			clearDice();
		}

		public void clearDice() {
			dice[0] = null;
			dice[1] = null;
			dice[2] = null;

			failureTwenty = null;
			successOne = null;
			tp = null;
			target = null;
			tpDices.clear();
		}

	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (hidden)
			hidePanel();
		else
			showPanel();

	}

}
