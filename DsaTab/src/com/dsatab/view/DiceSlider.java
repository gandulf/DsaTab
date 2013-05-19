package com.dsatab.view;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.OnWheelClickedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabPreferenceActivity;
import com.dsatab.common.DsaMath;
import com.dsatab.data.Art;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeAttribute;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CombatTalent;
import com.dsatab.data.Hero;
import com.dsatab.data.Modifier;
import com.dsatab.data.Probe;
import com.dsatab.data.Probe.ProbeType;
import com.dsatab.data.Spell;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.FeatureType;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Weapon;
import com.dsatab.util.Debug;
import com.dsatab.util.Hint;
import com.dsatab.util.Util;
import com.gandulf.guilib.view.WrappingSlidingDrawer;

public class DiceSlider extends WrappingSlidingDrawer implements View.OnClickListener, OnWheelChangedListener,
		OnWheelClickedListener, OnDrawerOpenListener, OnDrawerCloseListener {

	private static final int HANDLE_DICE_20 = 1;
	private static final int HANDLE_DICE_6 = 2;

	private View slideHandleButton;
	private boolean sliderVisible = true;

	private TableLayout tblDiceProbe;
	private TextView tfDiceTalent, tfDiceTalentValue, tfDiceProbesAttr, tfDiceProbesAttrValues, tfEffect,
			tfEffectValue;
	private ImageView tfDice20, tfDice6;

	private ImageButton infoButton;

	private LinearLayout linDiceResult;

	private View executeButton;

	private int dice20Count, dice6Count;
	private Animation shakeDice20;
	private Animation shakeDice6;

	private DiceHandler mHandler;

	private NumberFormat effectFormat = NumberFormat.getNumberInstance();

	private NumberFormat probabilityFormat = NumberFormat.getPercentInstance();

	private List<Modifier> modifiers;

	private Modifier manualModifer, erschwernisModifier;

	private ProbeData probeData;

	private View modifiersContainer;
	private WheelView modifierWheel;
	private NumericWheelAdapter modifierAdpater;

	private SharedPreferences preferences;

	private SoundPool sounds;

	private int soundNeutral;
	private int soundWin;
	private int soundFail;
	private Activity activity;
	private Hero hero;

	public DiceSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if (context instanceof Activity) {
			this.activity = (Activity) context;
		}
		init();
	}

	public DiceSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (context instanceof Activity) {
			this.activity = (Activity) context;
		}
		init();
	}

	public View getSlideHandleButton() {
		return slideHandleButton;
	}

	public void setSlideHandleButton(View slideHandleButton) {
		this.slideHandleButton = slideHandleButton;
	}

	public boolean isSliderVisible() {
		return sliderVisible;
	}

	public void setSliderVisible(boolean sliderVisible) {
		this.sliderVisible = sliderVisible;
		if (this.sliderVisible) {
			showSlideHandle();
		} else {
			hideSlideHandle();
		}
	}

	/**
	 * 
	 */
	private void showSlideHandle() {
		if (getSlideHandleButton().getVisibility() != View.VISIBLE) {
			getSlideHandleButton().setVisibility(View.VISIBLE);
			getHandle().startAnimation(AnimationUtils.makeInChildBottomAnimation(getContext()));
		}
	}

	/**
	 * 
	 */
	private void hideSlideHandle() {
		if (getSlideHandleButton().getVisibility() != View.GONE) {
			getSlideHandleButton().startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
			getSlideHandleButton().setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 */
	private void init() {
		mHandler = new DiceHandler(this);

		erschwernisModifier = new Modifier(0, "Probenerschwernis");

		effectFormat.setMaximumFractionDigits(1);
		if (!isInEditMode()) {
			preferences = DsaTabApplication.getPreferences();

			sounds = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
			soundNeutral = sounds.load(getContext(), R.raw.dice, 1);
			soundWin = sounds.load(getContext(), R.raw.dice_win, 1);
			soundFail = sounds.load(getContext(), R.raw.dice_fail, 1);

		}
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

		if (v.getId() == R.id.dice_probe_table || v.getId() == R.id.dice_info) {

			AlertDialog.Builder builder;
			AlertDialog alertDialog;

			LayoutInflater inflater = LayoutInflater.from(getContext());
			View probeInfoView = inflater.inflate(R.layout.popup_probe_info, null, false);
			LinearLayout linearLayout = (LinearLayout) probeInfoView.findViewById(R.id.popup_probe_layout);

			fillModifierList(linearLayout, false);

			// build
			builder = new AlertDialog.Builder(getContext());
			builder.setView(probeInfoView);
			builder.setTitle("Probenzuschläge");
			builder.setNeutralButton(getContext().getString(R.string.label_ok), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			alertDialog = builder.create();
			alertDialog.setCanceledOnTouchOutside(true);
			alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_AUTO_ROLL_DICE, true)) {
						checkProbe(probeData, getManualModifier());
					}
					modifierWheel = null;
					modifierAdpater = null;
				}
			});
			alertDialog.show();

		}
	}

	/**
	 * @param linearLayout
	 */
	private void fillModifierList(LinearLayout linearLayout, boolean inverse) {
		linearLayout.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		if (modifiers != null) {
			for (Modifier mod : modifiers) {

				if (mod == getManualModifier())
					continue;

				View listItem = inflater.inflate(R.layout.popup_probe_list_item, linearLayout, false);

				TextView text1 = (TextView) listItem.findViewById(R.id.popup_probelist_item_text1);
				text1.setText(mod.getTitle());
				if (inverse) {
					text1.setTextColor(DsaTabApplication.getInstance().getResources()
							.getColor(android.R.color.primary_text_dark));
				}

				TextView text2 = (TextView) listItem.findViewById(R.id.popup_probelist_item_text2);
				text2.setText(Util.toProbe(-mod.getModifier()));
				Util.setTextColor(text2, mod.getModifier(), inverse);

				linearLayout.addView(listItem);
			}
		}

		// manual modifier
		View editListItem = inflater.inflate(R.layout.popup_probe_manual_list_item, linearLayout, false);
		modifierWheel = (WheelView) editListItem.findViewById(R.id.popup_probelist_item_text2);
		modifierWheel.setOrientation(WheelView.HORIZONTAL);
		modifierWheel.setOnWheelChangedListeners(null);
		modifierWheel.setOnWheelClickedListeners(null);
		if (modifierAdpater == null) {
			modifierAdpater = new NumericWheelAdapter(getContext());
			modifierAdpater.setRange(-20, 20);
			modifierAdpater.setTextColor(AbstractWheelTextAdapter.STATE_POSITIVE_VALUE,
					getResources().getColor(R.color.ValueRed));
			modifierAdpater.setTextColor(AbstractWheelTextAdapter.STATE_NEGATIVE_VALUE,
					getResources().getColor(R.color.ValueGreen));
		}
		modifierWheel.setViewAdapter(modifierAdpater);
		modifierWheel.setCurrentItem(modifierAdpater.getPosition(-getManualModifier().getModifier()));
		modifierWheel.setOnWheelChangedListeners(this);
		modifierWheel.setOnWheelClickedListeners(this);
		TextView text1 = (TextView) editListItem.findViewById(R.id.popup_probelist_item_text1);
		text1.setText(getManualModifier().getTitle());

		linearLayout.addView(editListItem);

	}

	private Modifier getManualModifier() {
		if (manualModifer == null) {
			manualModifer = new Modifier(0, "Manuell", "Manuell");
		}
		return manualModifer;
	}

	@Override
	protected void onFinishInflate() {

		BitmapDrawable tileMe = new BitmapDrawable(getResources(), BitmapFactory.decodeResource(getResources(),
				R.drawable.bg_tab_dice));
		tileMe.setTileModeX(Shader.TileMode.MIRROR);
		tileMe.setTileModeY(Shader.TileMode.CLAMP);

		findViewById(R.id.inc_dice_slider_content).setBackgroundDrawable(tileMe);

		tblDiceProbe = (TableLayout) findViewById(R.id.dice_probe_table);
		tblDiceProbe.setVisibility(View.GONE);

		tfDiceTalent = (TextView) findViewById(R.id.dice_talent);
		tfDiceTalentValue = (TextView) findViewById(R.id.dice_talent_value);
		tfDiceProbesAttr = (TextView) findViewById(R.id.dice_probe);
		tfDiceProbesAttrValues = (TextView) findViewById(R.id.dice_value);

		tfEffect = (TextView) findViewById(R.id.dice_effect);
		tfEffectValue = (TextView) findViewById(R.id.dice_effect_value);

		tfDice20 = (ImageView) findViewById(R.id.dice_w20);
		tfDice20.setOnClickListener(this);

		tfDice6 = (ImageView) findViewById(R.id.dice_w6);
		tfDice6.setOnClickListener(this);

		executeButton = findViewById(R.id.dice_execute);
		executeButton.setOnClickListener(this);
		executeButton.setVisibility(View.GONE);

		linDiceResult = (LinearLayout) findViewById(R.id.dice_dice_result);
		linDiceResult.setOnClickListener(this);

		shakeDice20 = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
		shakeDice6 = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
		setOnDrawerOpenListener(this);
		setOnDrawerCloseListener(this);

		infoButton = (ImageButton) findViewById(R.id.dice_info);
		infoButton.setVisibility(View.GONE);

		super.onFinishInflate();
	}

	@Override
	public void onDrawerClosed() {
		tblDiceProbe.setVisibility(View.GONE);
		infoButton.setVisibility(View.INVISIBLE);
		executeButton.setVisibility(View.GONE);

		tfDiceTalent.setText(null);
		if (modifiersContainer != null) {
			modifiersContainer.setVisibility(View.GONE);
		}
		if (!sliderVisible) {
			hideSlideHandle();
		}
	}

	private boolean isModifiersPopup() {
		return !preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SHOW_MODIFIKATORS, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SlidingDrawer.OnDrawerOpenListener#onDrawerOpened()
	 */
	@Override
	public void onDrawerOpened() {
		showSlideHandle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kankan.wheel.widget.OnWheelChangedListener#onChanged(kankan.wheel.widget .WheelView, int, int)
	 */
	@Override
	public void onWheelChanged(WheelView wheel, int oldValue, int newValue) {
		if (probeData != null) {
			probeData.sound = false;

			int erschwernis = 0;
			if (modifierWheel != null) {
				erschwernis = modifierAdpater.getItem(modifierWheel.getCurrentItem());
			}
			getManualModifier().setModifier(-erschwernis);

			updateProgressView(probeData, getManualModifier());
			if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_AUTO_ROLL_DICE, true)) {
				checkProbe(probeData, getManualModifier());
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see kankan.wheel.widget.OnWheelClickedListener#onWheelClick(kankan.wheel. widget.WheelView, int)
	 */
	@Override
	public void onWheelClick(WheelView wheel, int itemIndex) {
		if (probeData != null) {
			probeData.sound = true;

			updateProgressView(probeData, getManualModifier());
			checkProbe(probeData, getManualModifier());
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
			tfEffect.setVisibility(View.VISIBLE);

			tfEffectValue.setText(effectFormat.format(effect));
			if (erschwernis != null)
				tfEffectValue.append(" (" + erschwernis + ")");

			// misslungen
			if ((effect < 0 && !info.successOne) || info.failureTwenty) {
				tfEffectValue.setTextColor(getResources().getColor(R.color.ValueRed));

				// bestätigter patzer
				if (info.failureTwenty && effect < 0) {
					tfDiceTalent.setText(info.probe.getName() + " total verpatzt!");

					linDiceResult.setBackgroundResource(R.drawable.probe_red_highlight);

					if (combatTalent instanceof CombatDistanceTalent) {
						tfDiceTalent.append(" | ");
						tfDiceTalent.append(getFailureDistance());

					} else if (combatTalent instanceof CombatMeleeTalent
							|| combatTalent instanceof CombatMeleeAttribute) {
						tfDiceTalent.append(" | ");
						tfDiceTalent.append(getFailureMelee());
					}

				} else { // unbestätigter patzer
					linDiceResult.setBackgroundResource(0);
					tfDiceTalent.setText(info.probe.getName() + " misslungen");
				}
				tfDiceTalent.setTextColor(getResources().getColor(R.color.ValueRed));

				if (info.sound && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_ROLL_DICE, true)
						&& preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_RESULT_DICE, true)) {
					sounds.play(soundFail, 1.0f, 1.0f, 0, 0, 1.0f);
				}
			} else { // geschafft

				if (info.successOne && effect >= 0) {
					tfDiceTalent.setText(info.probe.getName() + " glücklich gemeistert!");
					tfEffectValue.setTextColor(DsaTabApplication.getInstance().getResources()
							.getColor(R.color.ValueGreen));
					tfDiceTalent.setTextColor(getResources().getColor(R.color.ValueGreen));
					linDiceResult.setBackgroundResource(R.drawable.probe_green_highlight);
				} else {
					tfDiceTalent.setText(info.probe.getName() + " gelungen");
					tfEffectValue.setTextColor(getResources().getColor(android.R.color.secondary_text_dark));
					tfDiceTalent.setTextColor(getResources().getColor(android.R.color.primary_text_dark));
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
					tfDiceTalent.append(" | " + sb);
				}

				if (info.sound && preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_ROLL_DICE, true)
						&& preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_SOUND_RESULT_DICE, true)) {
					sounds.play(soundWin, 1.0f, 1.0f, 0, 0, 1.0f);
				}

			}
		} else {
			tfEffectValue.setVisibility(View.INVISIBLE);
			tfEffect.setVisibility(View.INVISIBLE);
		}
	}

	public void clearDice() {
		// clear any pending w20 still in queue
		mHandler.removeMessages(HANDLE_DICE_20);
		mHandler.removeMessages(HANDLE_DICE_6);
		dice20Count = 0;
		dice6Count = 0;
		linDiceResult.removeAllViews();
		if (probeData != null) {
			probeData.clearDice();
		}
	}

	public Double checkProbe(Hero hero, Probe probe) {
		if (!isOpened())
			animateOpen();

		if (activity != null) {
			Hint.showRandomHint("DiceSlider", activity);
		}
		this.hero = hero;

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

		modifiers = hero.getModifiers(probe, true, true);

		if (probe.getProbeInfo().getErschwernis() != null) {
			erschwernisModifier.setModifier(-1 * probe.getProbeInfo().getErschwernis());
			modifiers.add(erschwernisModifier);
		} else {

		}

		getManualModifier().setModifier(0);
		probeData = new ProbeData();
		probeData.hero = hero;
		probeData.probe = probe;
		probeData.value = new Integer[] { value1, value2, value3 };

		if (modifiersContainer != null && !isModifiersPopup()) {
			modifiersContainer.setVisibility(View.VISIBLE);
			if (findViewById(R.id.popup_probe_layout) != null)
				fillModifierList((LinearLayout) findViewById(R.id.popup_probe_layout), true);
		}
		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_AUTO_ROLL_DICE, true)) {
			executeButton.setVisibility(View.GONE);
			updateView(probeData);
			updateProgressView(probeData);
			return checkProbe(probeData);
		} else {
			updateView(probeData);
			updateProgressView(probeData);
			executeButton.setVisibility(View.VISIBLE);
			return null;
		}
	}

	private void updateProgressView(ProbeData info, Modifier... modificators) {
		Probe probe = probeData.probe;

		for (Modifier mod : modificators) {
			if (!modifiers.contains(mod))
				modifiers.add(mod);
		}
		int modifiersSum = Modifier.sum(modifiers);

		if (probe.getProbeBonus() != null) {

			tfDiceTalentValue.setText(Integer.toString(probe.getProbeBonus()));
			if (modifiersSum != 0) {
				tfDiceTalentValue.append(" ");
				tfDiceTalentValue.append(Util.toProbe(modifiersSum));
			}
		}

		if (isAttributesHidden(info)) {
			tfDiceTalentValue.setText(Util.toString(info.value[0]));
			if (modifiersSum != 0) {
				tfDiceTalentValue.append(" ");
				tfDiceTalentValue.append(Util.toProbe(modifiersSum));
			}
		}

		int taw = 0;
		if (probe.getProbeBonus() != null) {
			taw += probe.getProbeBonus();
		}
		taw += modifiersSum;

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
					probability = DsaMath.testTalent(info.value[0], info.value[1], info.value[2], taw);
					// Debug.verbose("Change for success is :" + probability);
				}

				break;
			case TwoOfThree:

				if (info.value[0] != null) {
					probability = DsaMath.testEigen(info.value[0], taw);
					// Debug.verbose("Change for success is :" + probability);
				}
				break;
			case One:
				if (info.value[0] != null) {
					probability = DsaMath.testEigen(info.value[0], taw);
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

	private boolean isAttributesHidden(ProbeData info) {
		Probe probe = probeData.probe;

		return (probe instanceof CombatDistanceTalent || probe instanceof CombatMeleeAttribute || (probe.getProbeInfo()
				.getAttributeTypes() == null && info.value[0] == info.value[1] && info.value[1] == info.value[2]));
	}

	private void updateView(ProbeData info) {
		Probe probe = probeData.probe;

		// --
		tblDiceProbe.setVisibility(View.VISIBLE);
		tfDiceTalent.setText(probe.getName());
		tfDiceTalent.setTextColor(DsaTabApplication.getInstance().getResources()
				.getColor(android.R.color.primary_text_dark));

		if (probe.getProbeBonus() != null) {
			tfDiceTalentValue.setText(Integer.toString(probe.getProbeBonus()));
			tfDiceTalentValue.setVisibility(View.VISIBLE);
		} else {
			tfDiceTalentValue.setVisibility(View.INVISIBLE);
		}

		// if no probe is present and all values are the same display them as
		// bonus
		if (isAttributesHidden(info)) {
			tfDiceProbesAttr.setVisibility(View.GONE);
			tfDiceProbesAttrValues.setVisibility(View.GONE);

			tfDiceTalentValue.setText(Util.toString(info.value[0]));
			tfDiceTalentValue.setVisibility(View.VISIBLE);
		} else {
			tfDiceProbesAttr.setText(probe.getProbeInfo().getAttributesString());
			tfDiceProbesAttr.setVisibility(View.VISIBLE);

			tfDiceProbesAttrValues.setText(Util.toString(info.value[0]) + "/" + Util.toString(info.value[1]) + "/"
					+ Util.toString(info.value[2]));
			tfDiceProbesAttrValues.setVisibility(View.VISIBLE);
		}

		if (isModifiersPopup()) {
			if (modifiersContainer != null) {
				modifiersContainer.setVisibility(View.GONE);
			}

			findViewById(R.id.dice_probe_table).setOnClickListener(this);
			if (infoButton != null) {
				infoButton.setVisibility(View.VISIBLE);
				infoButton.setOnClickListener(this);
			}

		} else {

			if (modifiersContainer == null) {
				ViewStub modifierStub = (ViewStub) findViewById(R.id.modifier_container_stub);
				modifiersContainer = modifierStub.inflate();

				LinearLayout linearLayout = (LinearLayout) modifiersContainer.findViewById(R.id.popup_probe_layout);
				fillModifierList(linearLayout, true);
			}

			findViewById(R.id.dice_probe_table).setOnClickListener(null);
			if (infoButton != null) {
				infoButton.setOnClickListener(null);
				infoButton.setVisibility(View.GONE);
			}

		}

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

	private Double checkProbe(ProbeData info, Modifier... modificators) {

		probeData = info;
		Probe probe = probeData.probe;

		for (Modifier mod : modificators) {
			if (!modifiers.contains(mod))
				modifiers.add(mod);
		}
		int modifiersSum = Modifier.sum(modifiers);

		// special case ini
		if (probe instanceof Attribute) {
			Attribute attribute = (Attribute) probe;
			if (attribute.getType() == AttributeType.ini) {
				if (info.dice[0] == null)
					info.dice[0] = rollDice6();

				double effect;

				if (info.hero.hasFeature(FeatureType.Klingentänzer)) {
					if (info.dice[1] == null)
						info.dice[1] = rollDice6();

					effect = info.hero.getAttributeValue(AttributeType.ini) + info.dice[0] + info.dice[1]
							+ modifiersSum;
				} else {
					effect = info.hero.getAttributeValue(AttributeType.ini) + info.dice[0] + modifiersSum;
				}

				showEffect(effect, null, info);

				info.hero.getAttribute(AttributeType.Initiative_Aktuell).setValue((int) effect);
				return effect;
			}
		}

		Double effect = null;

		Integer erschwernis = null;

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
				if (info.hero.hasFeature(FeatureType.WildeMagie)) {
					PATZER_THRESHOLD = 19;
				}
			} else {
				if (info.hero.hasFeature(FeatureType.Tollpatsch)) {
					PATZER_THRESHOLD = 19;
				}
			}
			switch (probeType) {

			case ThreeOfThree: {

				if (info.dice[0] == null)
					info.dice[0] = rollDice20(500, info.value[0] + valueModifier);

				if (info.dice[1] == null)
					info.dice[1] = rollDice20(1000, info.value[1] + valueModifier);

				if (info.dice[2] == null)
					info.dice[2] = rollDice20(1500, info.value[2] + valueModifier);

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
					if (probe instanceof Spell && info.hero.hasFeature(FeatureType.FesteMatrix)) {
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
					info.dice[0] = rollDice20(500, info.value[0] + taw);

					if (info.dice[0] == 1) {
						info.successOne = Boolean.TRUE;
						info.dice[0] = rollDice20(2000, info.value[0] + taw);
					} else if (info.dice[0] >= PATZER_THRESHOLD) {
						info.failureTwenty = Boolean.TRUE;
						info.dice[0] = rollDice20(2000, info.value[0] + taw);
					}
				}
				if (info.dice[1] == null)
					info.dice[1] = rollDice20(1000, info.value[1] + taw);

				if (info.dice[2] == null)
					info.dice[2] = rollDice20(1500, info.value[2] + taw);

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
					info.dice[0] = rollDice20(500, info.value[0] + taw);

					if (info.dice[0] == 1) {
						info.dice[0] = rollDice20(2000, info.value[0] + taw);
						info.successOne = Boolean.TRUE;
					} else if (info.dice[0] >= PATZER_THRESHOLD) {
						info.dice[0] = rollDice20(2000, info.value[0] + taw);
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
				info.target = Util.dice(20);
			}
		}

		if (probe instanceof CombatProbe) {
			CombatProbe combatProbe = (CombatProbe) probe;
			EquippedItem item = combatProbe.getEquippedItem();
			// remove target in case of defense
			if (!combatProbe.isAttack()) {
				info.target = null;
			}

			if (item != null && info.tp == null && combatProbe.isAttack()) {
				boolean realSuccessOne = info.successOne != null && info.successOne && effect >= 0;
				if (item.getItemSpecification() instanceof Weapon) {

					Weapon weapon = (Weapon) item.getItemSpecification();

					info.tp = weapon.getTp(hero.getModifiedValue(AttributeType.Körperkraft, true, true),
							hero.getModifierTP(item), realSuccessOne);

				} else if (item.getItemSpecification() instanceof DistanceWeapon) {
					DistanceWeapon weapon = (DistanceWeapon) item.getItemSpecification();

					info.tp = weapon.getTp(hero.getModifiedValue(AttributeType.Körperkraft, true, true),
							hero.getModifierTP(item), realSuccessOne);
				}

				if (info.tp != null && combatProbe.getTpModifier() != null) {
					info.tp += combatProbe.getTpModifier();
				}
			}
		}

		showEffect(effect, erschwernis, info);

		return effect;
	}

	public int rollDice20(int delay, int referenceValue) {
		if (!isOpened())
			animateOpen();

		dice20Count++;

		int dice = Util.dice(20);

		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {

			if (dice20Count == 1 && (!shakeDice20.hasStarted() || shakeDice20.hasEnded())) {
				shakeDice20.reset();
				tfDice20.startAnimation(shakeDice20);
			}

			mHandler.sendMessageDelayed(Message.obtain(mHandler, HANDLE_DICE_20, dice, referenceValue), delay);
		} else {
			showDice20(dice, referenceValue);
		}
		return dice;
	}

	public int rollDice20() {
		return rollDice20(1000, -1);
	}

	public int rollDice6(int delay) {
		if (!isOpened())
			animateOpen();

		dice6Count++;

		int dice = Util.dice(6);

		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {
			if (dice6Count == 1 && (!shakeDice6.hasStarted() || shakeDice6.hasEnded())) {
				shakeDice6.reset();
				tfDice6.startAnimation(shakeDice6);
			}

			mHandler.sendMessageDelayed(Message.obtain(mHandler, HANDLE_DICE_6, dice, 0), delay);
		} else {
			showDice6(dice);
		}
		return dice;
	}

	public int rollDice6() {
		return rollDice6(1000);
	}

	private TextView showDice20(int value, int referenceValue) {
		TextView res = new TextView(getContext());

		int width = getResources().getDimensionPixelSize(R.dimen.dices_size);
		int padding = getResources().getDimensionPixelSize(R.dimen.dices_padding);

		res.setWidth(width);
		res.setHeight(width);

		if (referenceValue < 0 || value <= referenceValue)
			res.setBackgroundResource(R.drawable.w20_empty);
		else
			res.setBackgroundResource(R.drawable.w20_red_empty);

		res.setText(Integer.toString(value));
		res.setTextColor(Color.WHITE);
		res.setTextSize(getResources().getInteger(R.integer.dices_font_size));
		res.setTypeface(Typeface.DEFAULT_BOLD);
		res.setGravity(Gravity.CENTER);
		res.setPadding(padding, 0, padding, 0);
		linDiceResult.addView(res, width, width);
		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {
			res.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.flip_in));
		}

		if (linDiceResult.getWidth() > 0 && linDiceResult.getChildCount() * width > linDiceResult.getWidth()) {
			linDiceResult.removeViewAt(0);
		}

		return res;
	}

	private ImageView showDice6(int value) {
		ImageView res = new ImageView(getContext());

		int width = getResources().getDimensionPixelSize(R.dimen.dices_size);
		int padding = getResources().getDimensionPixelSize(R.dimen.dices_padding);

		res.setImageResource(Util.getDrawableByName("w6_" + value));
		res.setPadding(padding, 0, padding, 0);
		linDiceResult.addView(res, width, width);
		if (preferences.getBoolean(DsaTabPreferenceActivity.KEY_PROBE_ANIM_ROLL_DICE, true)) {
			res.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.flip_in));
		}

		if (linDiceResult.getWidth() > 0 && linDiceResult.getChildCount() * width > linDiceResult.getWidth()) {
			linDiceResult.removeViewAt(0);
		}

		return res;
	}

	private String getFailureMelee() {
		int w6 = Util.dice(6) + Util.dice(6);

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
		int w6 = Util.dice(6) + Util.dice(6);

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

		private final WeakReference<DiceSlider> diceSliderRef;

		/**
		 * 
		 */
		public DiceHandler(DiceSlider diceSlider) {
			this.diceSliderRef = new WeakReference<DiceSlider>(diceSlider);
		}

		@Override
		public void handleMessage(Message msg) {
			DiceSlider diceSlider = diceSliderRef.get();

			if (diceSlider != null) {
				int result = msg.arg1;

				switch (msg.what) {
				case HANDLE_DICE_6: {
					diceSlider.showDice6(result);
					diceSlider.dice6Count--;
					break;
				}
				case HANDLE_DICE_20: {
					diceSlider.showDice20(result, msg.arg2);
					diceSlider.dice20Count--;
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

		Integer target;

		Hero hero;

		Probe probe;

		Boolean successOne, failureTwenty;

		boolean sound = true;

		public void clearDice() {
			dice[0] = null;
			dice[1] = null;
			dice[2] = null;

			failureTwenty = null;
			successOne = null;
			tp = null;
			target = null;
		}

	}

}
