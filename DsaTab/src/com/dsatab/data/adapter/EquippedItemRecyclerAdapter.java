package com.dsatab.data.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Hero;
import com.dsatab.data.enums.ItemType;
import com.dsatab.data.filter.ItemCardListFilter;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemCard;
import com.dsatab.data.items.ItemContainer;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.CardView;
import com.franlopez.flipcheckbox.FlipCheckBox;
import com.h6ah4i.android.widget.advrecyclerview.selectable.ElevatingSelectableViewHolder;

import java.util.ArrayList;
import java.util.List;

public class EquippedItemRecyclerAdapter extends ListRecyclerAdapter<RecyclerView.ViewHolder,ItemCard> implements View.OnClickListener {

	private ItemCardListFilter filter;

	private Hero hero;

	private int containerId = -1;

	public static  final int LIST=0;
	public static  final int GRID=1;

	private int displayType = LIST;

	public static class EquippedViewHolder extends ElevatingSelectableViewHolder {

        private int textColor = 0;

        private FlipCheckBox icon1;
        private CompoundButton set1, set2, set3;
        private TextView text1, text2;
        private TextView countOverlay;

		public EquippedViewHolder(View v) {
			super(v);

            text1 = (TextView) v.findViewById(android.R.id.text1);
            text2 = (TextView) v.findViewById(android.R.id.text2);
            icon1 = (FlipCheckBox) v.findViewById(android.R.id.checkbox);

            set1 = (CompoundButton) v.findViewById(R.id.set1);
            set2 = (CompoundButton) v.findViewById(R.id.set2);
            set3 = (CompoundButton) v.findViewById(R.id.set3);

            countOverlay = (TextView) v.findViewById(R.id.icon_1_overlay);

            if (text1 != null && (textColor != Color.TRANSPARENT)) {
                text1.setTextColor(textColor);
            }
            if (text2 != null && (textColor != Color.TRANSPARENT)) {
                text2.setTextColor(textColor);
            }
		}

        /**
         * @param textColor
         *            the textColor to set
         */
        public void setTextColor(int textColor) {
            this.textColor = textColor;

            if (text1 != null && (textColor != Color.TRANSPARENT)) {
                text1.setTextColor(textColor);
            }
            if (text2 != null && (textColor != Color.TRANSPARENT)) {
                text2.setTextColor(textColor);
            }
        }

        public FlipCheckBox getIcon1() {
            return icon1;
        }

        public CompoundButton getSet(int set) {
            switch (set) {
                case 0:
                    return set1;
                case 1:
                    return set2;
                case 2:
                    return set3;
                default:
                    return null;
            }
        }

        public void setItem(EquippedItem equippedItem) {
            setItem((equippedItem != null ? equippedItem.getItem() : null));
        }

        public void setItem(Item item) {
            ItemSpecification itemSpecification = null;
            if (item != null && !item.getSpecifications().isEmpty()) {
                itemSpecification = item.getSpecifications().get(0);
            }
            setItem(item, itemSpecification);
        }

        public void setItem(Item item, ItemSpecification spec) {

            if (icon1 != null) {
                if (item != null) {
                    icon1.setVisibility(View.VISIBLE);
                    icon1.setFrontDrawable(ViewUtils.circleIcon(icon1.getContext(),item.getIconUri()));
                } else {
                    icon1.setVisibility(View.INVISIBLE);
                }

            }
            // Set value for the first text field
            if (text1 != null) {
                text1.setText(item != null ? item.getTitle() : null);
                if (textColor != Color.TRANSPARENT)
                    text1.setTextColor(textColor);
            }

            // set value for the second text field
            if (text2 != null) {
                if (spec != null) {
                    text2.setText(spec.getInfo());
                    if (textColor != Color.TRANSPARENT)
                        text2.setTextColor(textColor);
                } else {
                    text2.setText(null);
                }
            }

            int visibility = (item != null && item.isEquipable()) ? View.VISIBLE : View.GONE;
            set1.setVisibility(visibility);
            set2.setVisibility(visibility);
            set3.setVisibility(visibility);

            if (item != null && item.getCount() > 1) {
                countOverlay.setText(Util.toString(item.getCount()));
                countOverlay.setVisibility(View.VISIBLE);
            } else {
                countOverlay.setVisibility(View.GONE);
            }

        }

    }

	public class ItemViewHolder extends ElevatingSelectableViewHolder {

		CardView cardView;

		public ItemViewHolder(View v) {
			super(v);
			cardView = (CardView) v;
		}
	}

	public EquippedItemRecyclerAdapter(Hero hero) {
		super(new ArrayList<ItemCard>());
		this.hero = hero;
	}

	public void filter(List<ItemType> types, String category, String constraint) {
		getFilter().setTypes(types);
		filter.setCategory(category);
		filter.filter(constraint);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getFilter()
	 */
	@Override
	public ItemCardListFilter getFilter() {
		if (filter == null)
			filter = new ItemCardListFilter(this);

		return filter;
	}

	@Override
	public int getItemViewType(int position) {
		return displayType;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (displayType) {
			case LIST:
				LayoutInflater inflater = LayoutInflater.from(parent.getContext());
				return new EquippedViewHolder(inflate(inflater,parent,R.layout.item_listitem_item,true));
			case GRID:
				CardView cardView = new CardView(parent.getContext());
				return new ItemViewHolder(cardView);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
		super.onBindViewHolder(viewholder, position);

		ItemCard itemCard = getItem(position);
		Item item = itemCard.getItem();
		if (viewholder instanceof EquippedViewHolder) {

			EquippedViewHolder holder = (EquippedViewHolder) viewholder;

			holder.setItem(item);

			if (item != null && item.isEquipable()) {
				for (int set = 0; set < Hero.INVENTORY_SET_COUNT; set++) {
                    CompoundButton setButton = holder.getSet(set);
					setButton.setChecked(false);
					setButton.setOnClickListener(this);
					setButton.setTag(item);

					for (EquippedItem equippedItem : hero.getEquippedItems(set)) {
						if (equippedItem.getItem().equals(item)) {
							setButton.setChecked(true);
							setButton.setTag(equippedItem);
							break;
						}
					}

					if (containerId >= ItemContainer.SET1 && containerId <= ItemContainer.SET3) {
						setButton.setVisibility(containerId == set ? View.VISIBLE : View.INVISIBLE);
					} else {
						setButton.setVisibility(View.VISIBLE);
					}
				}
			} else {
				for (int set = 0; set < Hero.INVENTORY_SET_COUNT; set++) {
					holder.getSet(set).setTag(null);
					holder.getSet(set).setVisibility(View.INVISIBLE);
				}
			}

			Util.applyRowStyle(holder.itemView, position);


		} else if (viewholder instanceof  ItemViewHolder) {
			ItemViewHolder itemViewHolder = (ItemViewHolder) viewholder;

			itemViewHolder.cardView.setCheckable(true);
			if (itemViewHolder.cardView.getBackground() != null)
				itemViewHolder.cardView.getBackground().mutate();

			int padding = itemViewHolder.cardView.getContext().getResources().getDimensionPixelSize(R.dimen.default_gap);
			itemViewHolder.cardView.setPadding(padding, padding, padding, padding);
			itemViewHolder.cardView.setItem(item);

		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {

		if (v.getTag() instanceof Item) {
			Item item = (Item) v.getTag();
			switch (v.getId()) {
				case R.id.set1:
					hero.addEquippedItem(v.getContext(), item, null, null, 0);
					break;
				case R.id.set2:
					hero.addEquippedItem(v.getContext(), item, null, null, 1);
					break;
				case R.id.set3:
					hero.addEquippedItem(v.getContext(), item, null, null, 2);
					break;
			}
		} else if (v.getTag() instanceof EquippedItem) {
			EquippedItem equippedItem = (EquippedItem) v.getTag();
			hero.removeEquippedItem(equippedItem);
		}

	}

	public int getContainerId() {
		return containerId;
	}

	public void setContainerId(int activeSet) {
		this.containerId = activeSet;
	}

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}
}
