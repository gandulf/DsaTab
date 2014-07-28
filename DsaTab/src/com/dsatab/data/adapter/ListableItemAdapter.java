package com.dsatab.data.adapter;

import java.util.Collections;
import java.util.List;

import net.simonvt.numberpicker.NumberPicker;
import net.simonvt.numberpicker.NumberPicker.OnValueChangeListener;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.FloatMath;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CustomProbe;
import com.dsatab.data.Hero;
import com.dsatab.data.Markable;
import com.dsatab.data.Probe;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.Purse.PurseUnit;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.Talent.Flags;
import com.dsatab.data.WoundAttribute;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.Position;
import com.dsatab.data.enums.UsageType;
import com.dsatab.data.filter.ListableListFilter;
import com.dsatab.data.items.Armor;
import com.dsatab.data.items.DistanceWeapon;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.Shield;
import com.dsatab.data.items.Weapon;
import com.dsatab.data.listable.FileListable;
import com.dsatab.data.listable.HeaderListItem;
import com.dsatab.data.listable.Listable;
import com.dsatab.data.listable.PurseListable;
import com.dsatab.data.listable.WoundListItem;
import com.dsatab.data.modifier.AbstractModificator;
import com.dsatab.data.modifier.CustomModificator;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.data.modifier.RulesModificator;
import com.dsatab.data.modifier.WoundModificator;
import com.dsatab.data.notes.Event;
import com.dsatab.data.notes.NotesItem;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.ListSettings;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.OnActionListener;
import com.dsatab.view.listener.ProbeListener;
import com.dsatab.view.listener.TargetListener;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.view.SeekBarEx;

import fr.castorflex.android.flipimageview.library.FlipImageView;
import fr.castorflex.android.flipimageview.library.FlipImageView.FlippableViewHolder;

public class ListableItemAdapter extends OpenArrayAdapter<Listable> implements OnSeekBarChangeListener,
		OnCheckedChangeListener, OnClickListener {

	private Hero hero;

	private ListableListFilter<Listable> filter;

	private OnActionListener actionListener;
	private ProbeListener probeListener;
	private EditListener editListener;
	private TargetListener targetListener;

	private LayoutInflater inflater;

	private Bitmap indicatorStar, indicatorStarGray, indicatorHouse, indicatorHouseGray, indicatorFlash,
			indicatorFlashGray;

	private static final int ITEM_TYPE_VIEW = 0;
	private static final int ITEM_TYPE_EDIT = 1;
	private static final int ITEM_TYPE_SEEK = 2;
	private static final int ITEM_TYPE_COMBAT_TALENT = 3;
	private static final int ITEM_TYPE_SIMPLE_TALENT = 4;
	private static final int ITEM_TYPE_MODIFIER = 5;
	private static final int ITEM_TYPE_HEADER = 6;
	private static final int ITEM_TYPE_NOTES = 7;
	private static final int ITEM_TYPE_PURSE = 8;
	private static final int ITEM_TYPE_WOUND = 9;
	private static final int ITEM_TYPE_PROBE = 10;

	private DsaTabActivity main;

	private OnItemSelectedListener onPurseItemSelectedListener = new OnItemSelectedListener() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android .widget.AdapterView,
		 * android.view.View, int, long)
		 */
		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			if (hero.getPurse() != null) {
				Currency cur = (Currency) parent.getItemAtPosition(position);
				if (cur != hero.getPurse().getActiveCurrency()) {
					hero.getPurse().setActiveCurrency(cur);
					notifyDataSetChanged();
				}
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private OnValueChangeListener onPurseValueChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			if (hero.getPurse() != null) {
				PurseUnit unit = (PurseUnit) picker.getTag();
				if (unit != null) {
					hero.getPurse().setCoins(unit, newVal);
				}
			}
		}
	};

	public ListableItemAdapter(DsaTabActivity context, Hero hero, ListSettings settings) {
		this(context, hero, (List<Listable>) Collections.EMPTY_LIST, settings);
	}

	public ListableItemAdapter(DsaTabActivity context, Hero hero, List<? extends Listable> items, ListSettings settings) {
		super(context, 0, (List) items);
		this.hero = hero;
		this.main = context;
		inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		indicatorStar = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star);
		indicatorStarGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_star_gray);
		indicatorHouse = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_house);
		indicatorHouseGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_house_gray);
		indicatorFlash = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_flash);
		indicatorFlashGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.indicator_flash_gray);

		if (settings != null && !settings.isAllVisible())
			filter(settings);
	}

	public void filter(ListSettings settings) {
		boolean hasChanged = false;
		if (settings != null) {
			hasChanged = (!getFilter().equals(settings));

			if (hasChanged) {
				getFilter().getSettings().set(settings);
				filter.filter((String) null);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		return 11;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.BaseAdapter#getItemViewType(int)
	 */
	@Override
	public int getItemViewType(int position) {
		Listable item = getItem(position);
		if (item instanceof EquippedItem) {
			EquippedItem equippedItem = (EquippedItem) item;
			if (equippedItem.getItemSpecification() instanceof Armor)
				return ITEM_TYPE_VIEW;
			else
				return ITEM_TYPE_EDIT;
		} else if (item instanceof Attribute) {
			Attribute attribute = (Attribute) item;
			if (attribute.getType() == AttributeType.Ausweichen) {
				return ITEM_TYPE_EDIT;
			} else {
				return ITEM_TYPE_SEEK;
			}
		} else if (item instanceof Talent) {
			if (item instanceof CombatMeleeTalent)
				return ITEM_TYPE_COMBAT_TALENT;
			else
				return ITEM_TYPE_SIMPLE_TALENT;
		} else if (item instanceof Spell) {
			return ITEM_TYPE_SIMPLE_TALENT;
		} else if (item instanceof Art) {
			return ITEM_TYPE_SIMPLE_TALENT;
		} else if (item instanceof Modificator) {
			return ITEM_TYPE_MODIFIER;
		} else if (item instanceof HeaderListItem) {
			return ITEM_TYPE_HEADER;
		} else if (item instanceof FileListable) {
			return ITEM_TYPE_NOTES;
		} else if (item instanceof NotesItem) {
			return ITEM_TYPE_NOTES;
		} else if (item instanceof PurseListable) {
			return ITEM_TYPE_PURSE;
		} else if (item instanceof WoundListItem) {
			return ITEM_TYPE_WOUND;
		} else if (item instanceof Probe) {
			return ITEM_TYPE_PROBE;
		} else {
			return IGNORE_ITEM_VIEW_TYPE;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public ListableListFilter<Listable> getFilter() {
		if (filter == null) {
			filter = new ListableListFilter<Listable>(this);
		}
		return filter;
	}

	public ProbeListener getProbeListener() {
		return probeListener;
	}

	public void setProbeListener(ProbeListener probeListener) {
		this.probeListener = probeListener;
	}

	public TargetListener getTargetListener() {
		return targetListener;
	}

	public void setTargetListener(TargetListener targetListener) {
		this.targetListener = targetListener;
	}

	public EditListener getEditListener() {
		return editListener;
	}

	public void setEditListener(EditListener editListener) {
		this.editListener = editListener;
	}

	public OnActionListener getActionListener() {
		return actionListener;
	}

	public void setActionListener(OnActionListener actionListener) {
		this.actionListener = actionListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {

			switch (getItemViewType(position)) {

			case ITEM_TYPE_VIEW: {
				convertView = (ItemListItem) inflater.inflate(R.layout.item_listitem_view, parent, false);

				ViewHolder holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
				holder.text3 = (TextView) convertView.findViewById(R.id.text3);
				holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);
				holder.flip = (FlipImageView) convertView.findViewById(android.R.id.icon1);

				holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);
				holder.icon_chain_bottom = (ImageView) convertView.findViewById(R.id.icon_chain_bottom);
				holder.icon_chain_top = (ImageView) convertView.findViewById(R.id.icon_chain_top);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_PROBE:
			case ITEM_TYPE_EDIT: {
				convertView = (ItemListItem) inflater.inflate(R.layout.item_listitem_equippeditem, parent, false);
				ViewHolder holder = new ViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
				holder.text3 = (TextView) convertView.findViewById(R.id.text3);
				holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);
				holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);
				holder.icon_chain_bottom = (ImageView) convertView.findViewById(R.id.icon_chain_bottom);
				holder.icon_chain_top = (ImageView) convertView.findViewById(R.id.icon_chain_top);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_SEEK: {
				convertView = (ViewGroup) inflater.inflate(R.layout.item_listitem_seek, parent, false);
				SeekViewHolder viewHolder = new SeekViewHolder();
				viewHolder.seek = (SeekBarEx) convertView.findViewById(R.id.wheel);
				viewHolder.text = (TextView) convertView.findViewById(R.id.wheel_label);
				viewHolder.value = (Button) convertView.findViewById(R.id.wheel_value);
				convertView.setTag(viewHolder);
				break;
			}
			case ITEM_TYPE_COMBAT_TALENT:
			case ITEM_TYPE_SIMPLE_TALENT: {
				convertView = inflater.inflate(R.layout.item_listitem_talent, parent, false);

				TalentViewHolder holder = new TalentViewHolder();
				// name
				holder.text1 = (TextView) convertView.findViewById(R.id.talent_list_item_text1);
				// be
				holder.text2 = (TextView) convertView.findViewById(R.id.talent_list_item_text2);
				// probe
				holder.text3 = (TextView) convertView.findViewById(R.id.talent_list_item_text3);
				// value / at
				holder.text4 = (Button) convertView.findViewById(R.id.talent_list_item_text4);
				// pa
				holder.text5 = (Button) convertView.findViewById(R.id.talent_list_item_text5);
				holder.indicator = (ImageView) convertView.findViewById(R.id.talent_list_item_indicator);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_MODIFIER: {
				// We need the layoutinflater to pick up the view from xml
				// Pick up the TwoLineListItem defined in the xml file
				convertView = inflater.inflate(R.layout.item_listitem_modifier, parent, false);

				ModifierViewHolder holder = new ModifierViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
				holder.flip = (FlipImageView) convertView.findViewById(android.R.id.icon1);
				holder.active = (CheckBox) convertView.findViewById(R.id.active);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_HEADER: {
				convertView = inflater.inflate(R.layout.item_listitem_header, parent, false);

				HeaderViewHolder holder = new HeaderViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.button1 = (ImageButton) convertView.findViewById(android.R.id.button1);
				holder.button2 = (ImageButton) convertView.findViewById(android.R.id.button2);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_NOTES: {
				convertView = inflater.inflate(R.layout.item_listitem_event, parent, false);

				EventViewHolder holder = new EventViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
				holder.text3 = (TextView) convertView.findViewById(R.id.text3);
				holder.flip = (FlipImageView) convertView.findViewById(android.R.id.icon1);
				holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);

				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_PURSE: {
				convertView = inflater.inflate(R.layout.item_listitem_purse, parent, false);
				PurseViewHolder holder = new PurseViewHolder();

				holder.currencySpinner = (Spinner) convertView.findViewById(R.id.sp_currency);
				holder.header = (TextView) convertView.findViewById(R.id.tv_currency_header);

				holder.picker = new NumberPicker[4];
				holder.picker[0] = (NumberPicker) convertView.findViewById(R.id.popup_purse_dukat);
				holder.picker[1] = (NumberPicker) convertView.findViewById(R.id.popup_purse_silver);
				holder.picker[2] = (NumberPicker) convertView.findViewById(R.id.popup_purse_heller);
				holder.picker[3] = (NumberPicker) convertView.findViewById(R.id.popup_purse_kreuzer);

				holder.labels = new TextView[4];
				holder.labels[0] = (TextView) convertView.findViewById(R.id.tv_currency1);
				holder.labels[1] = (TextView) convertView.findViewById(R.id.tv_currency2);
				holder.labels[2] = (TextView) convertView.findViewById(R.id.tv_currency3);
				holder.labels[3] = (TextView) convertView.findViewById(R.id.tv_currency4);

				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_WOUND: {
				convertView = inflater.inflate(R.layout.item_listitem_wound, parent, false);
				WoundViewHolder holder = new WoundViewHolder();

				int width = parent.getWidth();
				width -= (parent.getPaddingLeft() + parent.getPaddingRight());
				int buttonSize = getContext().getResources().getDimensionPixelSize(R.dimen.icon_button_size);
				int gap = getContext().getResources().getDimensionPixelSize(R.dimen.default_gap);
				int halfgap = gap / 2;
				int buttonCount = (int) FloatMath.floor(((float) width / (buttonSize + gap)));

				// <ToggleButton
				// android:id="@+id/wound1"
				// android:layout_width="@dimen/icon_button_size"
				// android:layout_height="@dimen/icon_button_size"
				// android:layout_marginRight="@dimen/default_gap"
				// android:textOn=""
				// android:textOff=""
				// android:background="@drawable/icon_wound_btn" />

				holder.wounds = new ToggleButton[buttonCount];
				for (int i = 0; i < buttonCount; i++) {
					ToggleButton woundButton = new ToggleButton(getContext());
					woundButton.setBackgroundResource(R.drawable.bg_wound_btn);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonSize, buttonSize);
					params.gravity = Gravity.CENTER;
					params.rightMargin = halfgap;
					params.leftMargin = halfgap;
					params.topMargin = halfgap;
					params.bottomMargin = halfgap;

					woundButton.setLayoutParams(params);
					woundButton.setTextOff("");
					woundButton.setPadding(halfgap, halfgap, halfgap, halfgap);
					woundButton.setLines(2);
					woundButton.setEllipsize(TruncateAt.MIDDLE);
					woundButton.setTextColor(Color.WHITE);
					woundButton.setTextOn("");
					woundButton.setTextSize(11.0f);

					woundButton.setChecked(false);
					woundButton.setOnClickListener(this);
					holder.wounds[i] = woundButton;
					((ViewGroup) convertView).addView(woundButton);
				}

				convertView.setTag(holder);
				break;
			}
			}
		}

		FlippableViewHolder.prepare(position, convertView, (ListView) parent);

		Listable item = getItem(position);

		if (item instanceof EquippedItem) {
			convertView = prepareView((EquippedItem) item, position, convertView, parent);
		} else if (item instanceof Attribute) {
			convertView = prepareView((Attribute) item, position, convertView, parent);
		} else if (item instanceof Talent) {
			convertView = prepareView((Talent) item, position, convertView, parent);
		} else if (item instanceof Spell) {
			convertView = prepareView((Spell) item, position, convertView, parent);
		} else if (item instanceof Art) {
			convertView = prepareView((Art) item, position, convertView, parent);
		} else if (item instanceof Modificator) {
			convertView = prepareView((Modificator) item, position, convertView, parent);
		} else if (item instanceof HeaderListItem) {
			convertView = prepareView((HeaderListItem) item, position, convertView, parent);
		} else if (item instanceof FileListable) {
			convertView = prepareView((FileListable) item, position, convertView, parent);
		} else if (item instanceof NotesItem) {
			convertView = prepareView((NotesItem) item, position, convertView, parent);
		} else if (item instanceof PurseListable) {
			convertView = prepareView((PurseListable) item, position, convertView, parent);
		} else if (item instanceof WoundListItem) {
			convertView = prepareView((WoundListItem) item, position, convertView, parent);
		} else if (item instanceof CustomProbe) {
			convertView = prepareView((CustomProbe) item, position, convertView, parent);
		}

		if (item instanceof Markable) {
			Util.applyRowStyle((Markable) item, convertView, position);
		} else {
			Util.applyRowStyle(convertView, position);
		}

		return convertView;

	}

	private View prepareView(CustomProbe probe, int position, View convertView, ViewGroup parent) {
		ViewHolder holder = (ViewHolder) convertView.getTag();

		StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
		title.append(probe.getName());
		Util.appendValue(hero, title, probe, null, true);
		holder.text1.setText(title);

		holder.text2.setText("");
		if (!TextUtils.isEmpty(probe.getDescription())) {
			holder.text2.append(probe.getDescription());
			holder.text2.append(" ");
		}

		String attrs = probe.getProbeInfo().getAttributesString();
		if (!TextUtils.isEmpty(attrs)) {
			holder.text2.append(attrs);
			holder.text2.append(" ");
		}
		String be = probe.getProbeInfo().getBe();
		if (!TextUtils.isEmpty(be)) {
			holder.text2.append(be);
		}

		holder.text3.setText(probe.getFooter());

		holder.icon1.setVisibility(View.VISIBLE);
		holder.icon1.setImageURI(probe.getIconUri());
		holder.icon1.setOnClickListener(probeListener);
		holder.icon1.setTag(probe);

		holder.icon2.setVisibility(View.GONE);

		Util.applyRowStyle(convertView, position);
		return convertView;
	}

	protected View prepareView(WoundListItem item, int position, View convertView, ViewGroup parent) {
		WoundViewHolder holder = (WoundViewHolder) convertView.getTag();

		for (int i = 0; i < holder.wounds.length; i++) {
			holder.wounds[i].setOnCheckedChangeListener(null);
		}

		int offset = 0;
		switch (DsaTabApplication.getInstance().getConfiguration().getWoundType()) {
		case Standard:

			for (WoundAttribute attr : hero.getWounds().values()) {

				for (int i = 0; i < attr.getValue() && offset + i < holder.wounds.length; i++) {
					holder.wounds[offset + i].setChecked(true);
					holder.wounds[offset + i].setTag(attr);
				}
				offset += attr.getValue();
			}

			for (int i = offset; i < holder.wounds.length; i++) {
				holder.wounds[i].setChecked(false);
				holder.wounds[i].setTag(null);
			}
			break;
		case Trefferzonen:
			for (WoundAttribute attr : hero.getWounds().values()) {

				for (int i = 0; i < attr.getValue() && offset + i < holder.wounds.length; i++) {
					holder.wounds[offset + i].setTextOn(attr.getPosition().getNameSort());
					holder.wounds[offset + i].setTextOff(attr.getPosition().getNameSort());
					holder.wounds[offset + i].setChecked(true);
					holder.wounds[offset + i].setTag(attr);
				}
				offset += attr.getValue();
			}

			for (int i = offset; i < holder.wounds.length; i++) {
				holder.wounds[i].setTextOn("");
				holder.wounds[i].setTextOff("");
				holder.wounds[i].setChecked(false);
				holder.wounds[i].setTag(null);
			}

			break;
		}

		for (int i = 0; i < holder.wounds.length; i++) {
			holder.wounds[i].setOnCheckedChangeListener(this);
		}

		return convertView;
	}

	@Override
	public void onClick(View v) {

		if (v.getTag() == null && v instanceof ToggleButton) {

			final ToggleButton button = (ToggleButton) v;

			switch (DsaTabApplication.getInstance().getConfiguration().getWoundType()) {
			case Standard:
				WoundAttribute attr = hero.getWounds().get(Position.Kopf);
				attr.addValue(1);
				button.setTag(attr);
				break;
			case Trefferzonen:
				button.setChecked(false);
				final List<Position> positions = DsaTabApplication.getInstance().getConfiguration().getWoundPositions();

				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				ArrayAdapter<Position> typeAdapter = new ArrayAdapter<Position>(getContext(),
						android.R.layout.simple_list_item_1, positions);
				builder.setTitle("Typ auswählen");
				builder.setAdapter(typeAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Position position = (Position) positions.get(which);
						WoundAttribute attr = hero.getWounds().get(position);
						button.setChecked(true);
						attr.addValue(1);
						button.setTag(attr);
					}
				});

				builder.show().setCanceledOnTouchOutside(true);
				break;
			}
		}
	}

	@Override
	public void onCheckedChanged(final CompoundButton button, final boolean checked) {
		if (button.getTag() instanceof WoundAttribute) {
			WoundAttribute attribute = (WoundAttribute) button.getTag();
			if (checked)
				attribute.addValue(1);
			else
				attribute.addValue(-1);
		}
	}

	protected View prepareView(PurseListable item, int position, View convertView, ViewGroup parent) {
		PurseViewHolder holder = (PurseViewHolder) convertView.getTag();

		Currency currency = item.getCurrency();
		if (currency == null) {
			currency = hero.getPurse().getActiveCurrency();

			SpinnerSimpleAdapter<Currency> currencyAdapter = new SpinnerSimpleAdapter<Currency>(getContext(),
					Currency.values());
			holder.currencySpinner.setOnItemSelectedListener(null);
			holder.currencySpinner.setAdapter(currencyAdapter);
			holder.currencySpinner.setSelection(currencyAdapter.getPosition(currency));
			holder.currencySpinner.setOnItemSelectedListener(onPurseItemSelectedListener);
			holder.currencySpinner.setVisibility(View.VISIBLE);
			holder.header.setVisibility(View.GONE);
		} else {
			holder.currencySpinner.setVisibility(View.GONE);
			holder.currencySpinner.setOnItemSelectedListener(null);

			holder.header.setVisibility(View.VISIBLE);
			holder.header.setText(currency.xmlName());
		}

		List<PurseUnit> units = currency.units();
		for (int i = 0; i < units.size(); i++) {
			holder.picker[i].setVisibility(View.VISIBLE);
			holder.picker[i].setTag(units.get(i));
			holder.picker[i].setMinValue(0);
			if (i == 0)
				holder.picker[i].setMaxValue(999);
			else
				holder.picker[i].setMaxValue(100);

			holder.picker[i].setValue(hero.getPurse().getCoins(units.get(i)));
			holder.picker[i].setOnValueChangedListener(onPurseValueChangeListener);
			holder.picker[i].setWrapSelectorWheel(false);

			holder.labels[i].setVisibility(View.VISIBLE);
			holder.labels[i].setText(units.get(i).xmlName());

		}

		for (int i = units.size(); i < 4; i++) {
			holder.picker[i].setVisibility(View.GONE);
			holder.picker[i].setTag(null);
			holder.labels[i].setVisibility(View.GONE);
		}

		return convertView;
	}

	protected View prepareView(HeaderListItem item, int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder = (HeaderViewHolder) convertView.getTag();

		holder.text1.setText(item.getTitle());
		holder.text1.setFocusable(true);

		if (item.getType() != null) {
			switch (item.getType()) {
			case Document:
				holder.button1.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgFilter));
				holder.button1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (actionListener != null)
							actionListener.onAction(OnActionListener.ACTION_DOCUMENTS_CHOOSE);

					}
				});
				holder.button1.setVisibility(View.VISIBLE);
				break;
			case Modificator:
				holder.button1.setImageResource(R.drawable.dsa_modifier_add);
				holder.button1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (actionListener != null)
							actionListener.onAction(OnActionListener.ACTION_MODIFICATOR_ADD);

					}
				});

				holder.button1.setVisibility(View.VISIBLE);
				break;
			case Probe:
				holder.button1.setImageResource(R.drawable.dsa_dice_add);
				holder.button1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (actionListener != null)
							actionListener.onAction(OnActionListener.ACTION_CUSTOM_PROBE_ADD);

					}
				});
				holder.button1.setVisibility(View.VISIBLE);
				break;
			case Notes:
				holder.button1.setImageResource(R.drawable.dsa_notes_add);
				holder.button1.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (actionListener != null)
							actionListener.onAction(OnActionListener.ACTION_NOTES_ADD);

					}
				});
				holder.button1.setVisibility(View.VISIBLE);

				holder.button2.setImageResource(R.drawable.dsa_speech_add);
				holder.button2.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (actionListener != null)
							actionListener.onAction(OnActionListener.ACTION_NOTES_RECORD);

					}
				});
				holder.button2.setVisibility(View.VISIBLE);
				break;
			default:
				holder.button1.setVisibility(View.GONE);
				holder.button2.setVisibility(View.GONE);
				break;
			}
		} else {
			holder.button1.setVisibility(View.GONE);
			holder.button2.setVisibility(View.GONE);
		}
		return convertView;
	}

	protected View prepareView(NotesItem e, int position, View convertView, ViewGroup parent) {
		EventViewHolder holder = (EventViewHolder) convertView.getTag();

		if (e.getCategory() != null) {

			if (holder.flip != null) {
				holder.flip.setImageResource(e.getCategory().getDrawableId());
			}

			if (holder.icon2 != null) {
				if (e instanceof Event) {
					if (((Event) e).getAudioPath() != null) {
						holder.icon2.setVisibility(View.VISIBLE);
						holder.icon2.setImageResource(R.drawable.dsa_speech);
					} else {
						holder.icon2.setVisibility(View.GONE);
					}
				} else {
					holder.icon2.setVisibility(View.GONE);
				}
			}
		}

		if (e.getCategory().hasName() && !TextUtils.isEmpty(e.getName())) {
			holder.text1.setText(e.getName().trim());
			holder.text2.setText(e.getComment().trim());
			holder.text2.setVisibility(View.VISIBLE);
		} else {
			holder.text1.setText(e.getComment().trim());
			holder.text2.setVisibility(View.GONE);
		}

		holder.text3.setText(e.getCategory().name());

		return convertView;
	}

	protected View prepareView(Modificator item, int position, View convertView, ViewGroup parent) {
		ModifierViewHolder holder = (ModifierViewHolder) convertView.getTag();

		if (item instanceof AbstractModificator) {
			AbstractModificator modificator = (AbstractModificator) item;
			holder.active.setVisibility(View.VISIBLE);
			holder.active.setChecked(modificator.isActive());
			holder.active.setClickable(true);
			holder.active.setTag(modificator);
		} else {
			holder.active.setVisibility(View.GONE);
		}

		holder.flip.setFlippable(true);
		holder.flip.setClickable(true);
		holder.flip.setBackgroundResource(0);
		holder.flip.setScaleType(ScaleType.CENTER);
		if (item instanceof WoundModificator) {
			holder.flip.setFlippable(false);
			holder.flip.setClickable(false);
			if (item.isActive())
				holder.flip.setImageResource(R.drawable.icon_wound_selected);
			else
				holder.flip.setImageResource(R.drawable.icon_wound_normal);
		} else if (item instanceof RulesModificator) {
			holder.flip.setFlippable(false);
			holder.flip.setClickable(false);
			holder.flip.setImageResource(R.drawable.ic_menu_preferences);
		} else if (item instanceof CustomModificator) {
			holder.flip.setImageResource(R.drawable.dsa_modifier);
		}

		if (item != null) {
			holder.text1.setText(item.getModificatorName());
			holder.text2.setText(item.getModificatorInfo());
		} else {
			holder.text1.setText(null);
			holder.text2.setText(null);
		}

		return convertView;
	}

	protected View prepareAusweichenView(final Attribute attribute, int position, View convertView, ViewGroup parent) {

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.icon1.setOnClickListener(getProbeListener());
		holder.icon1.setOnLongClickListener(getEditListener());
		holder.icon1.setImageResource(R.drawable.dsa_ausweichen);
		holder.icon1.setVisibility(View.VISIBLE);

		holder.icon2.setImageResource(R.drawable.dsa_archery_target);
		holder.icon2.setVisibility(View.VISIBLE);
		holder.icon2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				main.checkProbe(attribute, false);
			}
		});

		StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
		title.append(attribute.getName());

		Util.appendValue(hero, title, attribute, null, getFilter() != null ? getFilter().getSettings()
				.isIncludeModifiers() : true);

		holder.text1.setText(title);
		// holder.text2.setText("Modifikator " + Util.toProbe(attribute.getProbeInfo().getErschwernis()));
		holder.icon1.setTag(attribute);

		holder.icon_chain_top.setVisibility(View.GONE);
		holder.icon_chain_bottom.setVisibility(View.GONE);

		return convertView;
	}

	protected View prepareView(Attribute attribute, int position, View convertView, ViewGroup parent) {
		if (attribute.getType() == AttributeType.Ausweichen) {
			return prepareAusweichenView(attribute, position, convertView, parent);
		} else {
			SeekViewHolder viewHolder = (SeekViewHolder) convertView.getTag();

			viewHolder.seek.setMax(attribute.getMaximum());
			viewHolder.seek.setMin(attribute.getMinimum());
			viewHolder.seek.setValue(attribute.getValue());
			viewHolder.seek.setLabel(viewHolder.value);
			viewHolder.seek.setTag(attribute);
			viewHolder.seek.setOnSeekBarChangeListener(this);

			viewHolder.text.setText(attribute.getType().code());
			Util.setLabel(viewHolder.text, attribute.getType(), probeListener, editListener);

			Util.setValue(hero, viewHolder.value, attribute, null, false, probeListener, editListener);

			return convertView;
		}
	}

	protected View prepareView(Spell spell, int position, View convertView, ViewGroup parent) {
		TalentViewHolder holder = (TalentViewHolder) convertView.getTag();

		holder.text1.setText(spell.getName());

		Util.setVisibility(holder.text2, false, holder.text1);
		holder.text3.setText(spell.getProbeInfo().getAttributesString());

		if (spell.getValue() != null) {
			int modifier = hero.getModifier(spell);
			Util.setVisibility(holder.text4, spell.getValue() != null, holder.text1);
			Util.setText(holder.text4, spell.getValue(), modifier, null);
			holder.text4.setOnClickListener(null);
			holder.text4.setClickable(false);
			holder.text4.setFocusable(false);
		} else {
			Debug.warning(spell.getName() + " has no value");
		}
		Util.setVisibility(holder.text5, false, holder.text1);

		if (holder.indicator != null) {
			if (spell.hasFlag(Spell.Flags.ZauberSpezialisierung)
					|| !TextUtils.isEmpty(spell.getZauberSpezialisierung())) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorFlash);
			} else if (spell.hasFlag(Spell.Flags.ÜbernatürlicheBegabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStar);
			} else if (spell.hasFlag(Spell.Flags.Hauszauber)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorHouseGray);
			} else if (spell.hasFlag(Spell.Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStarGray);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}
		Util.applyRowStyle(spell, convertView, position);

		return convertView;

	}

	protected View prepareView(Art art, int position, View convertView, ViewGroup parent) {
		TalentViewHolder holder = (TalentViewHolder) convertView.getTag();

		Util.setVisibility(holder.text5, false, holder.text1);

		holder.text1.setText(art.getFullName());
		ArtInfo artInfo = art.getInfo();

		StringBuilder info = new StringBuilder();
		if (artInfo != null && !TextUtils.isEmpty(artInfo.getTarget())) {
			info.append(artInfo.getTarget());
		}
		if (artInfo != null && !TextUtils.isEmpty(artInfo.getRange())) {
			if (info.length() > 0)
				info.append(",");
			info.append(artInfo.getRange());
		}
		if (artInfo != null && !TextUtils.isEmpty(artInfo.getCastDuration())) {
			if (info.length() > 0)
				info.append(",");
			info.append(artInfo.getCastDuration());
		}

		holder.text2.setText(info);

		if (art.hasCustomProbe() && !TextUtils.isEmpty(art.getProbeInfo().getAttributesString())) {
			holder.text3.setText(art.getProbeInfo().getAttributesString());
		} else if (!TextUtils.isEmpty(artInfo.getProbe())
				&& TextUtils.isEmpty(art.getProbeInfo().getAttributesString())) {
			holder.text3.setText(artInfo.getProbe());
		} else {
			if (artInfo != null && !TextUtils.isEmpty(artInfo.getEffectDuration()))
				holder.text3.setText(artInfo.getEffectDuration());
			else
				holder.text3.setText(null);
		}
		if (art.getProbeInfo().getErschwernis() != null) {
			holder.text4.setText(Util.toProbe(art.getProbeInfo().getErschwernis()));
			holder.text4.setVisibility(View.VISIBLE);
		} else {
			holder.text4.setVisibility(View.INVISIBLE);
		}

		if (holder.indicator != null) {
			if (art.hasFlag(Art.Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStarGray);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}
		Util.applyRowStyle(art, convertView, position);

		return convertView;
	}

	protected View prepareView(FileListable file, int position, View convertView, ViewGroup parent) {
		EventViewHolder holder = (EventViewHolder) convertView.getTag();

		String fileName = file.getFile().getName();
		long fileSize = file.getFile().length();

		holder.text1.setText(fileName);
		holder.text2.setText(null);
		holder.text3.setText(Util.readableFileSize(fileSize));
		holder.flip.setImageResource(file.getIcon());
		holder.icon2.setVisibility(View.GONE);

		return convertView;
	}

	protected View prepareView(Talent talent, int position, View convertView, ViewGroup parent) {
		TalentViewHolder holder = (TalentViewHolder) convertView.getTag();

		holder.text1.setText(talent.getName());

		String be = talent.getProbeInfo().getBe();

		if (TextUtils.isEmpty(be)) {
			Util.setVisibility(holder.text2, false, holder.text1);
			holder.text2.setText(null);
		} else {
			Util.setVisibility(holder.text2, true, holder.text1);
			holder.text2.setText(be);
		}
		if (talent.getComplexity() != null) {
			Util.setVisibility(holder.text2, true, holder.text1);
			if (holder.text2.length() > 0)
				holder.text2.append(" ");
			holder.text2.append(Util.toString(talent.getComplexity()));
		}
		holder.text3.setText(talent.getProbeInfo().getAttributesString());

		if (talent instanceof CombatMeleeTalent) {
			CombatMeleeTalent meleeTalent = (CombatMeleeTalent) talent;

			if (meleeTalent.getAttack() != null && meleeTalent.getAttack().getValue() != null) {
				int modifier = 0;
				if (filter.getSettings().isIncludeModifiers()) {
					modifier = hero.getModifier(meleeTalent.getAttack());
				}
				Util.setText(holder.text4, meleeTalent.getAttack().getValue(), modifier, null);
				holder.text4.setOnClickListener(probeListener);
				holder.text4.setOnLongClickListener(editListener);
				holder.text4.setTag(R.id.TAG_KEY_VALUE, meleeTalent);
				holder.text4.setTag(R.id.TAG_KEY_PROBE, meleeTalent.getAttack());
				Util.setVisibility(holder.text4, true, holder.text1);
			} else {
				Util.setVisibility(holder.text4, false, holder.text1);
			}

			if (meleeTalent.getDefense() != null && meleeTalent.getDefense().getValue() != null) {
				int modifier = 0;
				if (filter.getSettings().isIncludeModifiers()) {
					modifier = hero.getModifier(meleeTalent.getDefense());
				}
				Util.setText(holder.text5, meleeTalent.getDefense().getValue(), modifier, null);
				holder.text5.setOnClickListener(probeListener);
				holder.text5.setOnLongClickListener(editListener);
				holder.text5.setTag(R.id.TAG_KEY_VALUE, meleeTalent);
				holder.text5.setTag(R.id.TAG_KEY_PROBE, meleeTalent.getDefense());
				Util.setVisibility(holder.text5, true, holder.text1);
			} else {
				holder.text5.setVisibility(View.INVISIBLE);
			}

		} else if (talent instanceof CombatDistanceTalent) {

			CombatDistanceTalent distanceTalent = (CombatDistanceTalent) talent;

			if (distanceTalent.getValue() != null) {
				int modifier = 0;

				if (filter.getSettings().isIncludeModifiers()) {
					modifier = hero.getModifier(distanceTalent);
				}
				Util.setText(holder.text4, distanceTalent.getValue(), modifier, null);
				holder.text4.setOnClickListener(null);
				holder.text4.setClickable(false);
				holder.text4.setFocusable(false);
				Util.setVisibility(holder.text4, true, holder.text1);
			} else {
				Util.setVisibility(holder.text4, false, holder.text1);
			}
			Util.setVisibility(holder.text5, false, holder.text1);
		} else {
			int modifier = 0;
			if (filter.getSettings().isIncludeModifiers()) {
				modifier = hero.getModifier(talent);
			}

			Util.setVisibility(holder.text4, talent.getValue() != null, holder.text1);
			Util.setText(holder.text4, talent.getValue(), modifier, null);
			holder.text4.setOnClickListener(null);
			holder.text4.setClickable(false);
			holder.text4.setFocusable(false);

			// hide text5 and expand text1 with its width
			Util.setVisibility(holder.text5, false, holder.text1);
		}

		if (holder.indicator != null) {
			if (talent.hasFlag(Talent.Flags.TalentSpezialisierung)
					|| !TextUtils.isEmpty(talent.getTalentSpezialisierung())) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorFlash);
			} else if (talent.hasFlag(Flags.Meisterhandwerk)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorHouse);
			} else if (talent.hasFlag(Flags.Talentschub)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStar);
			} else if (talent.hasFlag(Flags.Begabung)) {
				holder.indicator.setVisibility(View.VISIBLE);
				holder.indicator.setImageBitmap(indicatorStarGray);
			} else {
				holder.indicator.setVisibility(View.INVISIBLE);
			}
		}

		Util.applyRowStyle(talent, convertView, position);
		return convertView;
	}

	protected View prepareView(EquippedItem equippedItem, int position, View convertView, ViewGroup parent) {

		ViewHolder holder = (ViewHolder) convertView.getTag();

		Item item = equippedItem.getItem();
		ItemSpecification itemSpecification = equippedItem.getItemSpecification();

		// if (equippedItem.getSecondaryItem() != null
		// &&
		// (equippedItem.getSecondaryItem().getItem().hasSpecification(Shield.class)
		// || (equippedItem
		// .getSecondaryItem().getItem().hasSpecification(Weapon.class) &&
		// equippedItem.getHand() == Hand.rechts))) {
		//
		// } else {
		// fightItemsOdd = !fightItemsOdd;
		// }

		StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
		if (!TextUtils.isEmpty(item.getTitle()))
			title.append(item.getTitle());

		holder.text2.setText(itemSpecification.getInfo());
		holder.text3.setText(null);

		holder.icon1.setImageResource(DsaUtil.getResourceId(itemSpecification));
		holder.icon1.setVisibility(View.VISIBLE);

		if (itemSpecification instanceof DistanceWeapon) {
			DistanceWeapon distanceWeapon = (DistanceWeapon) itemSpecification;

			holder.icon2.setImageResource(R.drawable.dsa_archery_target);
			holder.icon2.setVisibility(View.VISIBLE);

			if (equippedItem.getTalent() != null) {
				CombatProbe probe = equippedItem.getCombatProbeAttacke();
				Util.appendValue(hero, title, probe, null, getFilter().getSettings().isIncludeModifiers());
				holder.icon2.setEnabled(true);
				holder.icon1.setEnabled(true);
				holder.icon2.setTag(equippedItem);
				holder.icon2.setOnClickListener(targetListener);
				holder.icon1.setTag(probe);
				holder.icon1.setOnClickListener(probeListener);
			} else {
				holder.icon2.setEnabled(false);
				holder.icon2.setTag(null);
				holder.icon2.setOnClickListener(null);

				holder.icon1.setEnabled(false);
				holder.icon1.setTag(null);
				holder.icon1.setOnClickListener(null);
			}

			holder.text2.setText(distanceWeapon.getInfo(hero.getModifierTP(equippedItem)));

			if (hero.getHuntingWeapon() != null && hero.getHuntingWeapon().equals(equippedItem)) {
				holder.text3.setText(" Jagdwaffe");
			}

		} else if (itemSpecification instanceof Shield) {
			holder.icon1.setVisibility(View.INVISIBLE);
			holder.icon1.setTag(null);
			holder.icon1.setOnClickListener(null);

			if (equippedItem.getUsageType() == UsageType.Paradewaffe)
				holder.icon2.setImageURI(item.getIconUri());
			else
				holder.icon2.setImageResource(R.drawable.dsa_shield_round);

			holder.icon2.setVisibility(View.VISIBLE);
			if (equippedItem.getTalent() != null) {
				holder.icon2.setEnabled(true);
				CombatProbe probe = equippedItem.getCombatProbeDefense();
				Util.appendValue(hero, title, probe, null, getFilter().getSettings().isIncludeModifiers());
				holder.icon2.setTag(probe);
				holder.icon2.setOnClickListener(probeListener);

				holder.text3.setText(equippedItem.getTalent().getName());
			} else {
				holder.icon2.setEnabled(false);
				holder.icon2.setTag(null);
				holder.icon2.setOnClickListener(null);
			}

		} else if (itemSpecification instanceof Weapon) {
			Weapon weapon = (Weapon) itemSpecification;

			holder.icon2.setImageResource(R.drawable.dsa_shield_round);
			holder.icon2.setVisibility(View.VISIBLE);
			if (equippedItem.getTalent() != null) {

				CombatProbe pa = null, at = null;

				if (weapon.isAttackable()) {
					holder.icon1.setEnabled(true);
					holder.icon1.setVisibility(View.VISIBLE);
					at = equippedItem.getCombatProbeAttacke();
					holder.icon1.setTag(at);
					holder.icon1.setOnClickListener(probeListener);
				} else {
					holder.icon1.setVisibility(View.INVISIBLE);
					holder.icon1.setTag(null);
					holder.icon1.setOnClickListener(null);
				}

				if (weapon.isDefendable()) {
					holder.icon2.setEnabled(true);
					holder.icon2.setVisibility(View.VISIBLE);
					pa = equippedItem.getCombatProbeDefense();
					holder.icon2.setTag(pa);
					holder.icon2.setOnClickListener(probeListener);
				} else {
					holder.icon2.setVisibility(View.INVISIBLE);
					holder.icon2.setTag(null);
					holder.icon2.setOnClickListener(null);
				}

				String talentName = null;
				if (equippedItem.getTalent() != null) {
					talentName = equippedItem.getTalent().getName();
				}

				SpannableStringBuilder sb = new SpannableStringBuilder();

				if (!TextUtils.isEmpty(weapon.getName())) {
					sb.append(weapon.getName());
					sb.append("/");
				}
				if (!TextUtils.isEmpty(equippedItem.getItemSpecification().getSpecificationLabel())) {
					sb.append(equippedItem.getItemSpecification().getSpecificationLabel());
					sb.append("/");
				}
				sb.append(talentName);

				if (equippedItem.getHand() == Hand.links) {
					sb.append(" (Links)");
				}

				if (equippedItem.isBeidhändigerKampf()) {
					sb.append(" - BK");
				}

				holder.text3.setText(sb);

				Util.appendValue(hero, title, at, pa, getFilter().getSettings().isIncludeModifiers());
			} else {
				holder.icon2.setEnabled(false);
				holder.icon2.setTag(null);
				holder.icon2.setOnClickListener(null);

				holder.icon1.setEnabled(false);
				holder.icon1.setTag(null);
				holder.icon1.setOnClickListener(null);
			}
			if (getFilter().getSettings().isIncludeModifiers()) {
				holder.text2.setText(weapon.getInfo(hero.getModifiedValue(AttributeType.Körperkraft, true, true),
						hero.getModifierTP(equippedItem)));
			} else {
				holder.text2.setText(weapon.getInfo());
			}
		} else if (itemSpecification instanceof Armor) {
			// Armor armor = (Armor) itemSpecification;
			holder.icon2.setVisibility(View.GONE);
			holder.icon2.setTag(null);
			holder.icon2.setOnClickListener(null);
		}

		if (holder.icon_chain_top != null && holder.icon_chain_bottom != null) {
			if (equippedItem.getSecondaryItem() != null) {
				if (position > 0 && getItem(position - 1).equals(equippedItem.getSecondaryItem())) {
					holder.icon_chain_bottom.setVisibility(View.VISIBLE);
					holder.icon_chain_top.setVisibility(View.GONE);
				} else if (position < getCount() && getItem(position + 1).equals(equippedItem.getSecondaryItem())) {
					holder.icon_chain_top.setVisibility(View.VISIBLE);
					holder.icon_chain_bottom.setVisibility(View.GONE);
				}
			} else {
				holder.icon_chain_top.setVisibility(View.GONE);
				holder.icon_chain_bottom.setVisibility(View.GONE);
			}
		}

		holder.text1.setText(title);

		return convertView;
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		SeekBarEx seekBarEx = (SeekBarEx) seekBar;
		Attribute attribute = (Attribute) seekBar.getTag();
		if (attribute != null) {
			attribute.setValue(seekBarEx.getValue());
		}
	}

	private static class ViewHolder extends FlippableViewHolder {

		TextView text1, text2, text3;
		ImageView icon1, icon2, icon_chain_top, icon_chain_bottom;

	}

	private static class SeekViewHolder {
		TextView text;
		Button value;
		SeekBarEx seek;
	}

	private static class TalentViewHolder {
		TextView text1, text2, text3;
		Button text4, text5;
		ImageView indicator;
	}

	private static class ModifierViewHolder extends FlippableViewHolder {
		TextView text1, text2;
		CheckBox active;
	}

	private static class HeaderViewHolder {
		TextView text1;
		ImageButton button1, button2;;
	}

	private static class FooterViewHolder {

	}

	private static class WoundViewHolder {
		ToggleButton[] wounds;
	}

	private static class EventViewHolder extends FlippableViewHolder {
		TextView text1, text2, text3;
		ImageView icon2;
	}

	private static class PurseViewHolder {
		private TextView header;
		private Spinner currencySpinner;
		private NumberPicker[] picker;
		private TextView[] labels;
	}

}
