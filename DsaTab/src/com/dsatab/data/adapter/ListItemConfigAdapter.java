package com.dsatab.data.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.data.Art;
import com.dsatab.data.Hero;
import com.dsatab.data.Purse.Currency;
import com.dsatab.data.Spell;
import com.dsatab.data.Talent;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.enums.EventCategory;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.items.EquippedItem;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.util.ViewUtils;
import com.dsatab.view.ListSettings;
import com.dsatab.view.ListSettings.ListItem;
import com.dsatab.view.ListSettings.ListItemType;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ListItemConfigAdapter  extends OpenRecyclerAdapter<ListItemConfigAdapter.ViewHolder,ListItem> implements
		DraggableItemAdapter<ListItemConfigAdapter.ViewHolder>, OnItemSelectedListener, OnClickListener {

	public class ViewHolder extends AbstractDraggableItemViewHolder {
		ViewGroup mContainer;
		View mDragHandle;

		TextView text1, text2;
		Spinner spinner;
		ImageView icon1, icon2;

		public ViewHolder(View v) {
			super(v);
			mContainer = (ViewGroup) v.findViewById(R.id.container);
			mDragHandle = v.findViewById(R.id.drag);

			text1 = (TextView) v.findViewById(android.R.id.text1);
			text2 = (TextView) v.findViewById(android.R.id.text2);
			icon1 = (ImageView) v.findViewById(android.R.id.icon1);
			icon2 = (ImageView) v.findViewById(android.R.id.icon2);
			spinner = (Spinner) v.findViewById(R.id.spinner);
		}


	}

	private Hero hero;

	private static final String NAME_EMPTY = "Alle";

	private Map<ListItemType, SpinnerSimpleAdapter<String>> spinnerAdapters;

	private Context context;

	public ListItemConfigAdapter(Context context, Hero hero, List<ListItem> objects) {
		super(objects);
		this.hero = hero;
		this.context = context;

		spinnerAdapters = new EnumMap<ListSettings.ListItemType, SpinnerSimpleAdapter<String>>(ListItemType.class);

		setHasStableIds(true);
	}

	protected SpinnerSimpleAdapter<String> getAdapter(ListItemType type) {
		SpinnerSimpleAdapter<String> result = spinnerAdapters.get(type);
		if (result == null) {
			List<String> types = new ArrayList<String>();

			types.add(NAME_EMPTY);

			switch (type) {
			case Attribute:
				for (AttributeType attributeType : AttributeType.values()) {
					types.add(attributeType.name());
				}
				break;
			case Talent:
				for (TalentGroupType groupType : TalentGroupType.values()) {
					types.add(groupType.name());
				}
				for (Talent talent : hero.getTalents()) {
					types.add(talent.getName());
				}
				break;
			case Spell:
				for (Spell spell : hero.getSpells().values()) {
					types.add(spell.getName());
				}
				break;
			case Art:
				for (Art art : hero.getArts().values()) {
					types.add(art.getName());
				}
				break;
			case Modificator:
				for (Modificator mod : hero.getUserModificators()) {
					types.add(mod.getModificatorName());
				}
				break;
			case EquippedItem:
				for (EquippedItem item : hero.getEquippedItems()) {
					types.add(item.getName());
				}
				break;
			case Header:
				break;
			case Notes:
				for (EventCategory category : EventCategory.values()) {
					types.add(category.name());
				}
				break;
			case Purse:
				for (Currency category : Currency.values()) {
					types.add(category.name());
				}
				break;
			}

			result = new SpinnerSimpleAdapter<String>(context, types);

			spinnerAdapters.put(type, result);
		}

		return result;
	}

	@Override
	public long getItemId(int position) {
		if (position >= 0 && position < getItemCount())
			return getItem(position).hashCode();
		else
			return AdapterView.INVALID_ROW_ID;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		final View v = inflater.inflate(R.layout.item_listitem_config, parent, false);
		return new ViewHolder(v);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		super.onBindViewHolder(holder, position);

		final ListItem listItem = getItem(position);

		holder.itemView.setOnClickListener(this);
		holder.itemView.setTag(listItem);

		holder.icon2.setOnClickListener(this);

		if (listItem.getType() == ListItemType.Header) {
			holder.spinner.setVisibility(View.GONE);
			holder.text2.setVisibility(View.VISIBLE);
			holder.text2.setText(listItem.getName());
		} else {
			SpinnerSimpleAdapter<String> spinnerAdapter = getAdapter(listItem.getType());
			holder.spinner.setVisibility(View.VISIBLE);
			holder.spinner.setAdapter(spinnerAdapter);
			holder.spinner.setSelection(spinnerAdapter.getPosition(listItem.getName()));
			holder.spinner.setOnItemSelectedListener(this);
			holder.spinner.setTag(listItem);
			holder.text2.setVisibility(View.GONE);
		}
		holder.text1.setText(listItem.getType().title());
		holder.icon1.setImageResource(DsaUtil.getResourceId(listItem.getType()));
		holder.icon2.setTag(listItem);

		Util.applyRowStyle(holder.itemView, position);

	}

	@Override
	public void onMoveItem(int fromPosition, int toPosition) {
		moveItem(fromPosition, toPosition);
	}

	@Override
	public boolean onCheckCanStartDrag(ViewHolder holder, int position, int x, int y) {
		// x, y --- relative from the itemView's top-left
		final View containerView = holder.mContainer;
		final View dragHandleView = holder.mDragHandle;
		if (dragHandleView != null) {
			final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
			final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

			return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
		} else {
			return false;
		}
	}

	@Override
	public ItemDraggableRange onGetItemDraggableRange(ViewHolder holder, int position) {
		// no drag-sortable range specified
		return null;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (parent.getTag() instanceof ListItem) {
			ListItem listItem = (ListItem) parent.getTag();

			String selection = (String) parent.getItemAtPosition(position);
			if (NAME_EMPTY.equals(selection)) {
				listItem.setName(null);
			} else {
				listItem.setName(selection);
			}
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		if (parent.getTag() instanceof ListItem) {
			ListItem listItem = (ListItem) parent.getTag();
			listItem.setName(null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case android.R.id.icon2:
				if (v.getTag() instanceof ListItem) {
					remove((ListItem) v.getTag());
				}
			default:
				if (v.getTag() instanceof ListItem) {
					editListItem((ListItem) v.getTag());
				}
			break;
		}

	}

	public void editListItem(final ListItem listItem) {

		if (listItem.getType() == ListItemType.Header) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			builder.setTitle(R.string.title_insert_title);
			final EditText editText = new EditText(builder.getContext());
			editText.setText(listItem.getName());
			builder.setView(editText);

			DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:

							if (TextUtils.isEmpty(editText.getText()))
								listItem.setName(null);
							else
								listItem.setName(editText.getText().toString());

							Util.hideKeyboard(editText);
							notifyDataSetChanged();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							Util.hideKeyboard(editText);
							break;
					}
				}
			};

			builder.setPositiveButton(android.R.string.ok, clickListener);
			builder.setNegativeButton(android.R.string.cancel, clickListener);
			builder.show();
		}
	}

}
