package com.dsatab.data.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.activity.DsaTabActivity;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.Art;
import com.dsatab.data.ArtInfo;
import com.dsatab.data.Attribute;
import com.dsatab.data.CombatDistanceTalent;
import com.dsatab.data.CombatMeleeTalent;
import com.dsatab.data.CombatProbe;
import com.dsatab.data.CustomModificator;
import com.dsatab.data.Hero;
import com.dsatab.data.Markable;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.Talent.Flags;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.Hand;
import com.dsatab.data.enums.UsageType;
import com.dsatab.data.filter.FilterableListFilter;
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
import com.dsatab.data.modifier.AbstractModificator;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.data.modifier.RulesModificator;
import com.dsatab.data.modifier.WoundModificator;
import com.dsatab.util.Debug;
import com.dsatab.util.Util;
import com.dsatab.view.EvadeChooserDialog;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.ListSettings;
import com.dsatab.view.listener.CheckableListenable;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.OnCheckedChangeListener;
import com.dsatab.view.listener.ProbeListener;
import com.dsatab.view.listener.TargetListener;
import com.gandulf.guilib.data.OpenArrayAdapter;
import com.gandulf.guilib.view.SeekBarEx;

import fr.castorflex.android.flipimageview.library.FlipImageView;

public class ListableItemAdapter extends OpenArrayAdapter<Listable> implements OnSeekBarChangeListener,
		OnCheckedChangeListener, OnClickListener {

	private Hero hero;

	private FilterableListFilter<Listable> filter;

	private ProbeListener probeListener;
	private EditListener editListener;
	private TargetListener targetListener;

	private EvadeChooserDialog ausweichenModificationDialog;

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

	private DsaTabActivity main;

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
		return 7;
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
			return ITEM_TYPE_VIEW;
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
	public FilterableListFilter<Listable> getFilter() {
		if (filter == null) {
			filter = new FilterableListFilter<Listable>(this);
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
				if (parent instanceof ListView) {
					holder.list = (ListView) parent;
					holder.position = position;
					holder.flip.setOnClickListener(this);
					holder.flip.setTag(holder);
				} else {
					holder.flip.setOnClickListener(null);
				}

				holder.icon2 = (ImageView) convertView.findViewById(android.R.id.icon2);
				holder.icon_chain_bottom = (ImageView) convertView.findViewById(R.id.icon_chain_bottom);
				holder.icon_chain_top = (ImageView) convertView.findViewById(R.id.icon_chain_top);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_EDIT: {
				convertView = (ItemListItem) inflater.inflate(R.layout.item_listitem, parent, false);
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
				convertView = (ViewGroup) inflater.inflate(R.layout.item_wheelview, parent, false);
				SeekViewHolder viewHolder = new SeekViewHolder();
				viewHolder.seek = (SeekBarEx) convertView.findViewById(R.id.wheel);
				viewHolder.text = (TextView) convertView.findViewById(R.id.wheel_label);
				viewHolder.value = (Button) convertView.findViewById(R.id.wheel_value);
				convertView.setTag(viewHolder);
				break;
			}
			case ITEM_TYPE_COMBAT_TALENT:
			case ITEM_TYPE_SIMPLE_TALENT: {
				convertView = inflater.inflate(R.layout.talent_list_item, parent, false);

				TalentViewHolder holder = new TalentViewHolder();
				// name
				holder.text1 = (TextView) convertView.findViewById(R.id.talent_list_item_text1);
				// be
				holder.text2 = (TextView) convertView.findViewById(R.id.talent_list_item_text2);
				// probe
				holder.text3 = (TextView) convertView.findViewById(R.id.talent_list_item_text3);
				// value / at
				holder.text4 = (TextView) convertView.findViewById(R.id.talent_list_item_text4);
				// pa
				holder.text5 = (TextView) convertView.findViewById(R.id.talent_list_item_text5);
				holder.indicator = (ImageView) convertView.findViewById(R.id.talent_list_item_indicator);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_MODIFIER: {
				// We need the layoutinflater to pick up the view from xml
				// Pick up the TwoLineListItem defined in the xml file
				convertView = inflater.inflate(R.layout.fight_sheet_modifier, parent, false);

				ModifierViewHolder holder = new ModifierViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				holder.text2 = (TextView) convertView.findViewById(android.R.id.text2);
				holder.icon1 = (ImageView) convertView.findViewById(android.R.id.icon1);
				holder.active = (CheckBox) convertView.findViewById(R.id.active);
				convertView.setTag(holder);
				break;
			}
			case ITEM_TYPE_HEADER: {
				convertView = inflater.inflate(R.layout.item_listitem_header, parent, false);

				HeaderViewHolder holder = new HeaderViewHolder();
				holder.text1 = (TextView) convertView.findViewById(android.R.id.text1);
				convertView.setTag(holder);
				break;
			}
			}

		}

		if (convertView.getTag() instanceof ViewHolder && parent instanceof ListView) {
			((ViewHolder) convertView.getTag()).position = position;
			((ViewHolder) convertView.getTag()).list = (ListView) parent;
		}

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
		}

		if (item instanceof Markable) {
			Util.applyRowStyle((Markable) item, convertView, position);
		} else {
			Util.applyRowStyle(convertView, position);
		}

		return convertView;

	}

	protected View prepareView(HeaderListItem item, int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder = (HeaderViewHolder) convertView.getTag();

		holder.text1.setText(item.getTitle());
		holder.text1.setFocusable(true);
		return convertView;
	}

	protected View prepareView(Modificator item, int position, View convertView, ViewGroup parent) {
		ModifierViewHolder holder = (ModifierViewHolder) convertView.getTag();

		if (item instanceof AbstractModificator) {
			AbstractModificator modificator = (AbstractModificator) item;
			holder.active.setVisibility(View.VISIBLE);
			holder.active.setChecked(modificator.isActive());
			holder.active.setClickable(false);
			holder.active.setFocusable(false);
			holder.active.setTag(modificator);
		} else {
			holder.active.setVisibility(View.GONE);
		}

		holder.icon1.setBackgroundResource(0);
		holder.icon1.setScaleType(ScaleType.CENTER);
		if (item instanceof WoundModificator) {
			if (item.isActive())
				holder.icon1.setImageResource(R.drawable.icon_wound_selected);
			else
				holder.icon1.setImageResource(R.drawable.icon_wound_normal);
		} else if (item instanceof RulesModificator) {
			holder.icon1.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgSettings));
		} else if (item instanceof CustomModificator) {
			holder.icon1.setImageResource(Util.getThemeResourceId(getContext(), R.attr.imgModifier));
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

	protected View prepareAusweichenView(Attribute attribute, int position, View convertView, ViewGroup parent) {

		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.icon1.setOnClickListener(getProbeListener());
		holder.icon1.setOnLongClickListener(getEditListener());
		holder.icon1.setImageResource(R.drawable.icon_ausweichen);
		holder.icon1.setVisibility(View.VISIBLE);

		holder.icon2.setImageResource(R.drawable.icon_target);
		holder.icon2.setVisibility(View.VISIBLE);
		holder.icon2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ausweichenModificationDialog == null) {
					ausweichenModificationDialog = new EvadeChooserDialog(main);
				}
				ausweichenModificationDialog.show();
			}
		});

		StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
		title.append(attribute.getName());

		Util.appendValue(hero, title, attribute, null, getFilter() != null ? getFilter().getSettings()
				.isIncludeModifiers() : true);

		holder.text1.setText(title);
		holder.text2.setText("Modifikator " + Util.toProbe(attribute.getProbeInfo().getErschwernis()));
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

			Util.setValue(hero, viewHolder.value, attribute, null, false, false, probeListener, editListener);

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
			Util.setText(holder.text4, spell.getValue(), modifier, null);
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
		} else if (!TextUtils.isEmpty(artInfo.getProbe()) && art.getProbeInfo().getAttributeTypes() == null) {
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
		} else
			holder.text4.setVisibility(View.INVISIBLE);

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
		ViewHolder holder = (ViewHolder) convertView.getTag();

		String fileName = file.getFile().getName();
		long fileSize = file.getFile().length();

		holder.text1.setText(fileName);
		holder.text2.setText(null);
		holder.text3.setText(Util.readableFileSize(fileSize));
		holder.icon1.setImageResource(file.getIcon());
		holder.icon2.setImageResource(0);

		if (holder.flip != null) {
			if (convertView instanceof CheckableListenable) {
				CheckableListenable checkable = (CheckableListenable) convertView;
				holder.flip.setFlipped(checkable.isChecked());
				holder.flip.setRotationReversed(checkable.isChecked());
				checkable.setOnCheckedChangeListener(this);
			}
		}
		if (holder.icon_chain_top != null && holder.icon_chain_bottom != null) {
			holder.icon_chain_top.setVisibility(View.GONE);
			holder.icon_chain_bottom.setVisibility(View.GONE);
		}

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
			Util.setText(holder.text4, talent.getValue(), modifier, null);
			holder.text4.setOnClickListener(null);
			holder.text4.setClickable(false); // hide text5 and expand text1
												// with its width
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

		holder.icon1.setImageResource(itemSpecification.getResourceId());
		holder.icon1.setVisibility(View.VISIBLE);
		holder.icon1.setTag(null);
		holder.icon1.setOnClickListener(null);
		holder.icon2.setTag(null);
		holder.icon2.setOnClickListener(null);
		holder.text3.setText("");
		if (itemSpecification instanceof DistanceWeapon) {
			DistanceWeapon distanceWeapon = (DistanceWeapon) itemSpecification;

			holder.icon2.setImageResource(R.drawable.icon_target);
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
				holder.icon1.setEnabled(false);
			}

			holder.text2.setText(distanceWeapon.getInfo(hero.getModifierTP(equippedItem)));
		} else if (itemSpecification instanceof Shield) {
			holder.icon1.setVisibility(View.INVISIBLE);
			if (equippedItem.getUsageType() == UsageType.Paradewaffe)
				holder.icon2.setImageURI(item.getIconUri());
			else
				holder.icon2.setImageResource(R.drawable.icon_shield);

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
			}

		} else if (itemSpecification instanceof Weapon) {
			Weapon weapon = (Weapon) itemSpecification;

			holder.icon2.setImageResource(R.drawable.icon_shield);
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
				}

				if (weapon.isDefendable()) {
					holder.icon2.setEnabled(true);
					holder.icon2.setVisibility(View.VISIBLE);
					pa = equippedItem.getCombatProbeDefense();
					holder.icon2.setTag(pa);
					holder.icon2.setOnClickListener(probeListener);
				} else {
					holder.icon2.setVisibility(View.INVISIBLE);
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
				holder.icon1.setEnabled(false);
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
			holder.icon1.setFocusable(false);
			holder.icon1.setClickable(false);
		}

		if (hero.getHuntingWeapon() != null && hero.getHuntingWeapon().equals(equippedItem)) {
			holder.text3.setText(" Jagdwaffe");
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

	@Override
	public void onClick(View v) {
		FlipImageView flipImageView = (FlipImageView) v;
		ViewHolder listHolder = (ViewHolder) v.getTag();
		listHolder.list.setItemChecked(listHolder.position, !flipImageView.isFlipped());
	}

	@Override
	public void onCheckedChanged(View checkableView, boolean isChecked) {
		if (checkableView.getTag() instanceof ViewHolder) {
			ViewHolder holder = (ViewHolder) checkableView.getTag();
			if (holder.flip != null) {
				holder.flip.setRotationReversed(isChecked);
				holder.flip.setFlipped(isChecked, true);
			}
		}
	}

	private static class ViewHolder {

		ListView list;
		int position;

		TextView text1, text2, text3;
		ImageView icon1, icon2, icon_chain_top, icon_chain_bottom;
		FlipImageView flip;
	}

	private static class SeekViewHolder {
		TextView text;
		Button value;
		SeekBarEx seek;
	}

	private static class TalentViewHolder {
		TextView text1, text2, text3, text4, text5;
		ImageView indicator;
	}

	private static class ModifierViewHolder {
		TextView text1, text2;
		ImageView icon1;
		CheckBox active;
	}

	private static class HeaderViewHolder {
		TextView text1;
	}

}
