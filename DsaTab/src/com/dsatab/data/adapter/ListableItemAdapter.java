package com.dsatab.data.adapter;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
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
import com.dsatab.data.Purse;
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
import com.dsatab.fragment.dialog.DiceSliderFragment;
import com.dsatab.fragment.dialog.TakeHitDialog;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.StyleableSpannableStringBuilder;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.ListSettings;
import com.dsatab.view.listener.EditListener;
import com.dsatab.view.listener.OnActionListener;
import com.dsatab.view.listener.ProbeListener;
import com.dsatab.view.listener.TargetListener;
import com.franlopez.flipcheckbox.FlipCheckBox;
import com.gandulf.guilib.view.SeekBarEx;
import com.wnafee.vector.compat.ResourcesCompat;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.Collections;
import java.util.List;

public class ListableItemAdapter extends ListRecyclerAdapter<RecyclerView.ViewHolder, Listable> implements
        OnSeekBarChangeListener,
        OnCheckedChangeListener, OnClickListener {

    private Hero hero;

    private ListableListFilter<Listable> filter;

    private OnActionListener actionListener;
    private ProbeListener probeListener;
    private EditListener editListener;
    private TargetListener targetListener;

    private Bitmap indicatorStar, indicatorStarGray, indicatorHouse, indicatorHouseGray, indicatorFlash,
            indicatorFlashGray;

    private static final int ITEM_TYPE_VIEW = 1;
    private static final int ITEM_TYPE_EDIT = 2;
    private static final int ITEM_TYPE_SEEK = 3;
    private static final int ITEM_TYPE_COMBAT_TALENT = 4;
    private static final int ITEM_TYPE_SIMPLE_TALENT = 5;
    private static final int ITEM_TYPE_MODIFIER = 6;
    private static final int ITEM_TYPE_HEADER = 7;
    private static final int ITEM_TYPE_NOTES = 8;
    private static final int ITEM_TYPE_PURSE = 9;
    private static final int ITEM_TYPE_WOUND = 10;
    private static final int ITEM_TYPE_PROBE = 11;

    private Fragment fragment;

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
                if (cur != hero.getHeroConfiguration().getActiveCurrency()) {
                    hero.getHeroConfiguration().setActiveCurrency(cur);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {

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

    public ListableItemAdapter(Fragment fragment, Hero hero, ListSettings settings) {
        this(fragment, hero, (List<Listable>) Collections.EMPTY_LIST, settings);
    }

    public ListableItemAdapter(Fragment fragment, Hero hero, List<Listable> items, ListSettings settings) {
        super(items);
        this.hero = hero;
        this.fragment = fragment;

        indicatorStar = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.indicator_star);
        indicatorStarGray = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.indicator_star_gray);
        indicatorHouse = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.indicator_house);
        indicatorHouseGray = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.indicator_house_gray);
        indicatorFlash = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.indicator_flash);
        indicatorFlashGray = BitmapFactory.decodeResource(fragment.getResources(), R.drawable.indicator_flash_gray);

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
            return 0;
        }
    }


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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {

            case ITEM_TYPE_VIEW: {
                return new ViewHolder(inflate(inflater, parent, R.layout.item_listitem_view, true));
            }
            case ITEM_TYPE_PROBE:
            case ITEM_TYPE_EDIT: {
                return new ViewHolder(inflate(inflater, parent, R.layout.item_listitem_equippeditem, true));
            }
            case ITEM_TYPE_SEEK: {
                return new SeekViewHolder(inflate(inflater, parent, R.layout.item_listitem_seek, false));
            }
            case ITEM_TYPE_COMBAT_TALENT:
            case ITEM_TYPE_SIMPLE_TALENT: {
                return new TalentViewHolder(inflate(inflater, parent, R.layout.item_listitem_talent, true));
            }
            case ITEM_TYPE_MODIFIER: {
                return new ModifierViewHolder(inflate(inflater, parent, R.layout.item_listitem_modifier, true));
            }
            case ITEM_TYPE_HEADER: {
                return new HeaderViewHolder(inflate(inflater, parent, R.layout.item_listitem_header, false));
            }
            case ITEM_TYPE_NOTES: {
                return new EventViewHolder(inflate(inflater, parent, R.layout.item_listitem_event, true));
            }
            case ITEM_TYPE_PURSE: {
                return new PurseViewHolder(inflate(inflater, parent, R.layout.item_listitem_purse, false));
            }
            case ITEM_TYPE_WOUND: {
                return new WoundViewHolder(inflate(inflater, parent, R.layout.item_listitem_wound, false), parent, this);
            }
            default:
                return null;
        }

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Object item = getItem(position);

        if (item instanceof EquippedItem) {
            prepareView((EquippedItem) item, position, (ViewHolder) holder);
        } else if (item instanceof Attribute) {
            Attribute attribute = (Attribute) item;
            if (attribute.getType() == AttributeType.Ausweichen)
                prepareAusweichenView(attribute, position, (ViewHolder) holder);
            else
                prepareView(attribute, position, (SeekViewHolder) holder);
        } else if (item instanceof Talent) {
            prepareView((Talent) item, position, (TalentViewHolder) holder);
        } else if (item instanceof Spell) {
            prepareView((Spell) item, position, (TalentViewHolder) holder);
        } else if (item instanceof Art) {
            prepareView((Art) item, position, (TalentViewHolder) holder);
        } else if (item instanceof Modificator) {
            prepareView((Modificator) item, position, (ModifierViewHolder) holder);
        } else if (item instanceof HeaderListItem) {
            prepareView((HeaderListItem) item, position, (HeaderViewHolder) holder);
        } else if (item instanceof FileListable) {
            prepareView((FileListable) item, position, (EventViewHolder) holder);
        } else if (item instanceof NotesItem) {
            prepareView((NotesItem) item, position, (EventViewHolder) holder);
        } else if (item instanceof PurseListable) {
            prepareView((PurseListable) item, position, (PurseViewHolder) holder);
        } else if (item instanceof WoundListItem) {
            prepareView((WoundListItem) item, position, (WoundViewHolder) holder);
        } else if (item instanceof CustomProbe) {
            prepareView((CustomProbe) item, position, (ViewHolder) holder);
        }

        if (item instanceof Markable) {
            Util.applyRowStyle((Markable) item, holder.itemView, position);
        } else {
            Util.applyRowStyle(holder.itemView, position);
        }

    }


    private void prepareView(CustomProbe probe, int position, ViewHolder holder) {
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
        holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(),probe.getIconUri()));
        holder.icon1.setOnClickListener(probeListener);
        holder.icon1.setTag(probe);

        holder.icon2.setVisibility(View.GONE);

        Util.applyRowStyle(holder.itemView, position);
    }

    protected void prepareView(WoundListItem item, int position, WoundViewHolder holder) {


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

                    PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                    for (int i = 0; i < positions.size(); i++) {
                        popupMenu.getMenu().add(0, i, i, positions.get(i).getName());
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            Position position = (Position) positions.get(item.getItemId());
                            WoundAttribute attr = hero.getWounds().get(position);
                            button.setChecked(true);
                            attr.addValue(1);
                            button.setTag(attr);
                            return true;
                        }
                    });

                    popupMenu.show();
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
        } else if (button.getTag() instanceof AbstractModificator) {
            AbstractModificator modificator = (AbstractModificator) button.getTag();
            modificator.setActive(checked);
        }
    }

    protected void prepareView(PurseListable item, int position, PurseViewHolder holder) {

        Currency currency = item.getCurrency();
        if (currency == null) {
            currency = hero.getHeroConfiguration().getActiveCurrency();

            SpinnerSimpleAdapter<Currency> currencyAdapter = new SpinnerSimpleAdapter<Currency>(holder.currencySpinner.getContext(),
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
            holder.picker[i].setTag(new Purse.PurseValue(hero.getPurse(), units.get(i)));
            holder.picker[i].setText(String.valueOf(hero.getPurse().getCoins(units.get(i))));
            holder.picker[i].setOnClickListener(getEditListener());

            holder.labels[i].setVisibility(View.VISIBLE);
            holder.labels[i].setText(units.get(i).xmlName());
        }

        for (int i = units.size(); i < 4; i++) {
            holder.picker[i].setVisibility(View.GONE);
            holder.picker[i].setTag(null);
            holder.labels[i].setVisibility(View.GONE);
        }


    }

    protected void prepareView(HeaderListItem item, int position, HeaderViewHolder holder) {
        holder.text1.setText(item.getTitle());
        holder.text1.setFocusable(true);

        if (item.getType() != null) {
            switch (item.getType()) {
                case Document:
                    //TODO cache drawable
                    holder.button1.setImageDrawable(ViewUtils.icon(holder.button1.getContext(), MaterialDrawableBuilder.IconValue.FILE_DOCUMENT));
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
                    // holder.button1.setImageResource(R.drawable.dsa_modifier_add);
                    // holder.button1.setOnClickListener(new View.OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // if (actionListener != null)
                    // actionListener.onAction(OnActionListener.ACTION_MODIFICATOR_ADD);
                    //
                    // }
                    // });
                    // holder.button1.setVisibility(View.VISIBLE);
                    holder.button1.setVisibility(View.GONE);
                    break;
                case Probe:
                    // holder.button1.setImageResource(R.drawable.dsa_dice_add);
                    // holder.button1.setOnClickListener(new View.OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // if (actionListener != null)
                    // actionListener.onAction(OnActionListener.ACTION_CUSTOM_PROBE_ADD);
                    //
                    // }
                    // });
                    // holder.button1.setVisibility(View.VISIBLE);
                    holder.button1.setVisibility(View.GONE);
                    break;
                case Notes:
                    // holder.button1.setImageResource(R.drawable.dsa_speech_add);
                    // holder.button1.setOnClickListener(new View.OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // if (actionListener != null)
                    // actionListener.onAction(OnActionListener.ACTION_NOTES_RECORD);
                    //
                    // }
                    // });
                    // holder.button1.setVisibility(View.VISIBLE);
                    //
                    // holder.button2.setImageResource(R.drawable.dsa_notes_add);
                    // holder.button2.setOnClickListener(new View.OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // if (actionListener != null)
                    // actionListener.onAction(OnActionListener.ACTION_NOTES_ADD);
                    //
                    // }
                    // });
                    // holder.button2.setVisibility(View.VISIBLE);
                    holder.button1.setVisibility(View.GONE);

                    break;
                case Wound:
                    holder.button1.setImageDrawable(ResourcesCompat.getDrawable(holder.button1.getContext(), R.drawable.vd_sticking_plaster));
                    holder.button1.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            TakeHitDialog.show(fragment, hero, null, 0);
                        }
                    });
                    holder.button1.setVisibility(View.VISIBLE);
                    break;
                default:
                    holder.button1.setVisibility(View.GONE);
                    break;
            }
        } else {
            holder.button1.setVisibility(View.GONE);
        }
    }

    protected void prepareView(NotesItem e, int position, EventViewHolder holder) {
        if (e.getCategory() != null) {

            if (holder.icon1 != null) {
                holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), e.getCategory().getDrawableId()));
            }

            if (holder.icon2 != null) {
                if (e instanceof Event) {
                    if (((Event) e).getAudioPath() != null) {
                        holder.icon2.setVisibility(View.VISIBLE);
                        holder.icon2.setImageDrawable(ResourcesCompat.getDrawable(holder.icon2.getContext(), R.drawable.vd_nothing_to_say));
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


    }

    protected void prepareView(Modificator item, int position, ModifierViewHolder holder) {

        if (item instanceof AbstractModificator) {
            AbstractModificator modificator = (AbstractModificator) item;
            holder.active.setOnCheckedChangeListener(null);
            holder.active.setVisibility(View.VISIBLE);
            holder.active.setChecked(modificator.isActive());
            holder.active.setTag(modificator);
            holder.active.setOnCheckedChangeListener(this);
        } else {
            holder.active.setVisibility(View.GONE);
        }

        if (item instanceof WoundModificator) {
            holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), R.drawable.vd_broken_heart));
        } else if (item instanceof RulesModificator) {
            holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), R.drawable.vd_scales));
        } else if (item instanceof CustomModificator) {
            holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), R.drawable.vd_orb_direction));
        }

        if (item != null) {
            holder.text1.setText(item.getModificatorName());
            holder.text2.setText(item.getModificatorInfo());
        } else {
            holder.text1.setText(null);
            holder.text2.setText(null);
        }
    }

    protected void prepareAusweichenView(final Attribute attribute, int position, ViewHolder holder) {

        holder.icon1.setOnClickListener(getProbeListener());
        holder.icon1.setOnLongClickListener(getEditListener());
        holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), R.drawable.vd_foot_trip));
        holder.icon1.setVisibility(View.VISIBLE);

        holder.icon2.setImageDrawable(ViewUtils.circleIcon(holder.icon2.getContext(), R.drawable.vd_archery_target));
        holder.icon2.setVisibility(View.VISIBLE);
        holder.icon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiceSliderFragment.show(fragment, hero, attribute, false, 0);
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


    }

    protected void prepareView(Attribute attribute, int position, SeekViewHolder viewHolder) {
        if (attribute.getValue() != null) {
            viewHolder.seek.setMax(attribute.getMaximum());
            viewHolder.seek.setMin(attribute.getMinimum());
            viewHolder.seek.setValue(attribute.getValue());
            viewHolder.seek.setEnabled(true);
        } else {
            viewHolder.seek.setEnabled(false);
        }
        viewHolder.seek.setLabel(viewHolder.value);
        viewHolder.seek.setTag(attribute);
        viewHolder.seek.setOnSeekBarChangeListener(this);

        viewHolder.text.setText(attribute.getType().code());
        Util.setLabel(viewHolder.text, attribute.getType(), probeListener, editListener);

        Util.setValue(hero, viewHolder.value, attribute, null, false, probeListener, editListener);


    }

    protected void prepareView(Spell spell, int position, TalentViewHolder holder) {

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
        Util.applyRowStyle(spell, holder.itemView, position);
    }

    protected void prepareView(Art art, int position, TalentViewHolder holder) {

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
        Util.setVisibility(holder.text2, !TextUtils.isEmpty(info), holder.text1);

        String probe;
        if (art.hasCustomProbe() && !TextUtils.isEmpty(art.getProbeInfo().getAttributesString())) {
            probe = art.getProbeInfo().getAttributesString();
        } else if (!TextUtils.isEmpty(artInfo.getProbe())
                && TextUtils.isEmpty(art.getProbeInfo().getAttributesString())) {
            probe = artInfo.getProbe();
        } else {
            if (artInfo != null && !TextUtils.isEmpty(artInfo.getEffectDuration()))
                probe = artInfo.getEffectDuration();
            else
                probe = null;
        }

        holder.text3.setText(probe);
        Util.setVisibility(holder.text3, !TextUtils.isEmpty(probe), holder.text1);

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
        Util.applyRowStyle(art, holder.itemView, position);
    }

    protected void prepareView(FileListable file, int position, EventViewHolder holder) {
        String fileName = file.getFile().getName();
        long fileSize = file.getFile().length();

        holder.text1.setText(fileName);
        holder.text2.setText(null);
        holder.text3.setText(Util.readableFileSize(fileSize));
        holder.icon1.setFrontDrawable(ResourcesCompat.getDrawable(holder.icon1.getContext(), file.getIcon()));
        holder.icon2.setVisibility(View.GONE);

    }

    protected void prepareView(Talent talent, int position, TalentViewHolder holder) {


        holder.text1.setText(talent.getName());

        String be = talent.getProbeInfo().getBe();

        holder.text2.setText(be);
        Util.setVisibility(holder.text2, !TextUtils.isEmpty(be), holder.text1);

        if (talent.getComplexity() != null) {
            Util.setVisibility(holder.text2, true, holder.text1);
            if (holder.text2.length() > 0)
                holder.text2.append(" ");
            holder.text2.append(Util.toString(talent.getComplexity()));
        }

        holder.text3.setText(talent.getProbeInfo().getAttributesString());
        Util.setVisibility(holder.text3, !TextUtils.isEmpty(talent.getProbeInfo().getAttributesString()), holder.text1);

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

        Util.applyRowStyle(talent, holder.itemView, position);
    }

    protected void prepareView(EquippedItem equippedItem, int position, ViewHolder holder) {

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

        holder.icon1.setFrontDrawable(ViewUtils.circleIcon(holder.icon1.getContext(), DsaUtil.getResourceId(itemSpecification)));
        holder.icon1.setVisibility(View.VISIBLE);

        if (itemSpecification instanceof DistanceWeapon) {
            DistanceWeapon distanceWeapon = (DistanceWeapon) itemSpecification;

            holder.icon2.setImageDrawable(ViewUtils.circleIcon(holder.icon2.getContext(), R.drawable.vd_archery_target));
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
                holder.icon2.setImageDrawable(ViewUtils.circleIcon(holder.icon2.getContext(), R.drawable.vd_round_shield));

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

            holder.icon2.setImageDrawable(ViewUtils.circleIcon(holder.icon2.getContext(), R.drawable.vd_round_shield));
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
                } else if (position < getItemCount() && getItem(position + 1).equals(equippedItem.getSecondaryItem())) {
                    holder.icon_chain_top.setVisibility(View.VISIBLE);
                    holder.icon_chain_bottom.setVisibility(View.GONE);
                }
            } else {
                holder.icon_chain_top.setVisibility(View.GONE);
                holder.icon_chain_bottom.setVisibility(View.GONE);
            }
        }

        holder.text1.setText(title);


    }


    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
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
        if (attribute != null && attribute.getValue() != null) {
            attribute.setValue(seekBarEx.getValue());
        }
    }

    private static class ViewHolder extends BaseListableViewHolder {
        TextView text1, text2, text3;
        FlipCheckBox icon1;
        ImageView  icon2, icon_chain_top, icon_chain_bottom;

        public ViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
            text2 = (TextView) v.findViewById(android.R.id.text2);
            text3 = (TextView) v.findViewById(R.id.text3);
            icon1 = (FlipCheckBox) v.findViewById(android.R.id.checkbox);
            icon2 = (ImageView) v.findViewById(android.R.id.icon2);
            icon_chain_bottom = (ImageView) v.findViewById(R.id.icon_chain_bottom);
            icon_chain_top = (ImageView) v.findViewById(R.id.icon_chain_top);
        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    private static class SeekViewHolder extends BaseListableViewHolder {
        TextView text;
        Button value;
        SeekBarEx seek;

        public SeekViewHolder(View v) {
            super(v);
            seek = (SeekBarEx) v.findViewById(R.id.wheel);
            text = (TextView) v.findViewById(R.id.wheel_label);
            value = (Button) v.findViewById(R.id.wheel_value);
        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    private static class TalentViewHolder extends BaseListableViewHolder {
        TextView text1, text2, text3;
        Button text4, text5;
        ImageView indicator;

        public TalentViewHolder(View v) {
            super(v);
            // name
            text1 = (TextView) v.findViewById(R.id.talent_list_item_text1);
            // be
            text2 = (TextView) v.findViewById(R.id.talent_list_item_text2);
            // probe
            text3 = (TextView) v.findViewById(R.id.talent_list_item_text3);
            // value / at
            text4 = (Button) v.findViewById(R.id.talent_list_item_text4);
            // pa
            text5 = (Button) v.findViewById(R.id.talent_list_item_text5);
            indicator = (ImageView) v.findViewById(R.id.talent_list_item_indicator);
        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    private static class ModifierViewHolder extends BaseListableViewHolder {
        FlipCheckBox icon1;
        TextView text1, text2;
        CheckBox active;

        OnCheckedChangeListener changeListener;

        public ModifierViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
            text2 = (TextView) v.findViewById(android.R.id.text2);
            icon1 = (FlipCheckBox) v.findViewById(android.R.id.checkbox);
            active = (CheckBox) v.findViewById(R.id.active);

        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    private static class HeaderViewHolder extends BaseListableViewHolder {
        TextView text1;
        ImageButton button1;

        public HeaderViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
            button1 = (ImageButton) v.findViewById(android.R.id.button1);
        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    private static class WoundViewHolder extends BaseListableViewHolder {
        ToggleButton[] wounds;

        public WoundViewHolder(View v, ViewGroup parent, OnClickListener clickListener) {
            super(v);

            ViewGroup woundContainer = (ViewGroup) v.findViewById(R.id.container);
            int width = parent.getWidth();
            int padding = parent.getPaddingLeft() + parent.getPaddingRight() + woundContainer.getPaddingLeft() + woundContainer.getPaddingRight();
            width -= padding;
            int buttonSize = parent.getContext().getResources().getDimensionPixelSize(R.dimen.icon_button_size);
            int gap = parent.getContext().getResources().getDimensionPixelSize(R.dimen.default_gap);

            int halfgap = gap / 2;
            int buttonCount = (int) Math.floor(((float) width / (buttonSize + gap)));

            // <ToggleButton
            // android:id="@+id/wound1"
            // android:layout_width="@dimen/icon_button_size"
            // android:layout_height="@dimen/icon_button_size"
            // android:layout_marginRight="@dimen/default_gap"
            // android:textOn=""
            // android:textOff=""
            // android:background="@drawable/icon_wound_btn" />

            wounds = new ToggleButton[buttonCount];
            for (int i = 0; i < buttonCount; i++) {
                ToggleButton woundButton = new ToggleButton(parent.getContext());
                woundButton.setBackgroundResource(R.drawable.icon_wound_btn);
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
                woundButton.setOnClickListener(clickListener);
                wounds[i] = woundButton;
                woundContainer.addView(woundButton);
            }
        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }



    private static class EventViewHolder extends BaseListableViewHolder {
        TextView text1, text2, text3;
        FlipCheckBox icon1;
        ImageView icon2;

        public EventViewHolder(View v) {
            super(v);
            text1 = (TextView) v.findViewById(android.R.id.text1);
            text2 = (TextView) v.findViewById(android.R.id.text2);
            text3 = (TextView) v.findViewById(R.id.text3);
            icon1 = (FlipCheckBox) v.findViewById(android.R.id.checkbox);
            icon2 = (ImageView) v.findViewById(android.R.id.icon2);
        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    private static class PurseViewHolder extends BaseListableViewHolder {
        private TextView header;
        private Spinner currencySpinner;
        private Button[] picker;
        private TextView[] labels;

        public PurseViewHolder(View v) {
            super(v);
            currencySpinner = (Spinner) v.findViewById(R.id.sp_currency);
            header = (TextView) v.findViewById(R.id.tv_currency_header);

            picker = new Button[4];
            picker[0] = (Button) v.findViewById(R.id.popup_purse_dukat);
            picker[1] = (Button) v.findViewById(R.id.popup_purse_silver);
            picker[2] = (Button) v.findViewById(R.id.popup_purse_heller);
            picker[3] = (Button) v.findViewById(R.id.popup_purse_kreuzer);

            labels = new TextView[4];
            labels[0] = (TextView) v.findViewById(R.id.tv_currency1);
            labels[1] = (TextView) v.findViewById(R.id.tv_currency2);
            labels[2] = (TextView) v.findViewById(R.id.tv_currency3);
            labels[3] = (TextView) v.findViewById(R.id.tv_currency4);

        }

        @Override
        public View getSwipeableContainerView() {
            return itemView;
        }
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }
}
