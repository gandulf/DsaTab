/*
 * Copyright (C) 2010 Gandulf Kohlweiss
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.dsatab.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.dsatab.R;
import com.dsatab.activity.ItemEditActivity;
import com.dsatab.data.Hero;
import com.dsatab.data.adapter.GalleryImageAdapter;
import com.dsatab.data.adapter.GalleryImageCursorAdapter;
import com.dsatab.data.items.Item;
import com.dsatab.data.items.ItemSpecification;
import com.dsatab.data.items.ItemType;
import com.dsatab.util.Util;
import com.dsatab.view.CardView;
import com.dsatab.view.ItemListItem;
import com.dsatab.xml.DataManager;

public class ItemChooserFragment extends BaseFragment implements View.OnClickListener, View.OnLongClickListener,
		DialogInterface.OnMultiChoiceClickListener {

	private Gallery gallery;
	private CardView imageView;
	private ItemListItem itemView;

	private ImageButton[] categoryButtons;

	private BaseAdapter imageAdapter;

	private Item selectedCard = null;

	private ItemSpecification selectedItemSpecification = null;

	private DataSetObserver dataSetObserver;

	private Set<ItemType> categoriesSelected;
	private ItemType[] categories;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.sheet_item_chooser, container, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		if (imageAdapter != null) {
			imageAdapter.unregisterDataSetObserver(dataSetObserver);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		categories = ItemType.values();
		String itemCategory = null;

		categoriesSelected = new HashSet<ItemType>();

		gallery = (Gallery) findViewById(R.id.gal_gallery);
		gallery.setSpacing(0);
		imageView = (CardView) findViewById(R.id.gal_imageView);
		imageView.setHighQuality(true);
		itemView = (ItemListItem) findViewById(R.id.inc_gal_item_view);
		itemView.setTextColor(Color.BLACK);
		itemView.setBackgroundColor(getResources().getColor(R.color.Brighter));

		imageView.setOnClickListener(this);
		imageView.setOnLongClickListener(this);

		Cursor c = DataManager.getItemsCursor(null, categoriesSelected, itemCategory);
		imageAdapter = new GalleryImageCursorAdapter(getActivity(), c);

		gallery.setAdapter(imageAdapter);
		gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Cursor c = (Cursor) gallery.getItemAtPosition(position);
				Item card = DataManager.getItemByCursor(c);
				showCard(card, null, false);
			}
		});

		if (imageAdapter.getCount() == 0) {
			Toast.makeText(getActivity(), "Keine Einträge gefunden", Toast.LENGTH_SHORT).show();
		}

		dataSetObserver = new DataSetObserver() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see android.database.DataSetObserver#onChanged()
			 */
			@Override
			public void onChanged() {
				Item item = null;
				if (imageAdapter.getCount() > 0) {
					Cursor cursor = (Cursor) imageAdapter.getItem(0);
					item = DataManager.getItemByCursor(cursor);
				}
				if (item != null) {
					showCard(item, null, true);
				}
			}
		};

		imageAdapter.registerDataSetObserver(dataSetObserver);

		categoryButtons = new ImageButton[8];
		// imagebuttons
		ImageButton weaponButton = (ImageButton) findViewById(R.id.body_attack_button);
		ImageButton shieldButton = (ImageButton) findViewById(R.id.body_defense_button);
		ImageButton distanceButton = (ImageButton) findViewById(R.id.body_distance_button);
		ImageButton armorButton = (ImageButton) findViewById(R.id.body_armor_button);
		ImageButton itemsButton = (ImageButton) findViewById(R.id.body_items_button);
		ImageButton clothButton = (ImageButton) findViewById(R.id.body_cloth_button);
		ImageButton specialButton = (ImageButton) findViewById(R.id.body_special_button);
		ImageButton bagsButton = (ImageButton) findViewById(R.id.body_bags_button);

		categoryButtons[0] = weaponButton;
		categoryButtons[1] = shieldButton;
		categoryButtons[2] = distanceButton;
		categoryButtons[3] = armorButton;
		categoryButtons[4] = itemsButton;
		categoryButtons[5] = clothButton;
		categoryButtons[6] = specialButton;
		categoryButtons[7] = bagsButton;

		weaponButton.setTag(ItemType.Waffen);
		shieldButton.setTag(ItemType.Schilde);
		distanceButton.setTag(ItemType.Fernwaffen);
		armorButton.setTag(ItemType.Rüstung);
		itemsButton.setTag(ItemType.Sonstiges);
		clothButton.setTag(ItemType.Kleidung);
		specialButton.setTag(ItemType.Schmuck);
		bagsButton.setTag(ItemType.Behälter);

		for (ImageButton button : categoryButtons) {
			button.setOnClickListener(this);
			button.setOnLongClickListener(this);
			ItemType buttonType = (ItemType) button.getTag();
			if (categoriesSelected.contains(buttonType))
				button.setSelected(true);
		}

		if (gallery.getSelectedItem() != null) {
			if (gallery.getSelectedItem() instanceof Cursor) {
				showCard(DataManager.getItemByCursor((Cursor) gallery.getSelectedItem()), null, true);
			} else if (gallery.getSelectedItem() instanceof Item)
				showCard((Item) gallery.getSelectedItem(), null, true);
		}

		if (categoriesSelected.isEmpty()) {
			categoriesSelected.addAll(Arrays.asList(categories));
		}

		getActivity().supportInvalidateOptionsMenu();

		super.onActivityCreated(savedInstanceState);
	}

	protected void filter(ItemType cardType, String itemCategory, String constraint) {
		if (imageAdapter instanceof GalleryImageAdapter) {
			((GalleryImageAdapter) imageAdapter).filter(cardType, itemCategory, constraint);
		} else if (imageAdapter instanceof GalleryImageCursorAdapter) {
			GalleryImageCursorAdapter cursorAdapter = (GalleryImageCursorAdapter) imageAdapter;
			Cursor cursor;
			if (cardType != null) {
				cursor = DataManager.getItemsCursor(constraint, Arrays.asList(cardType), itemCategory);
			} else {
				cursor = DataManager.getItemsCursor(constraint, null, itemCategory);
			}
			cursorAdapter.changeCursor(cursor);
		}
	}

	protected void filter(Collection<ItemType> cardTypes, String itemCategory, String constraint) {
		if (imageAdapter instanceof GalleryImageAdapter) {
			((GalleryImageAdapter) imageAdapter).filter(cardTypes, itemCategory, null);
		} else if (imageAdapter instanceof GalleryImageCursorAdapter) {
			GalleryImageCursorAdapter cursorAdapter = (GalleryImageCursorAdapter) imageAdapter;
			Cursor cursor = DataManager.getItemsCursor(null, cardTypes, itemCategory);
			cursorAdapter.changeCursor(cursor);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.fragment.BaseFragment#onHeroLoaded(com.dsatab.data.Hero)
	 */
	@Override
	public void onHeroLoaded(Hero hero) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (v.getTag() instanceof ItemType) {
			ItemType cardType = (ItemType) v.getTag();
			chooseType(cardType, null, null);
		}

	}

	public boolean onLongClick(View v) {

		if (v.getTag() instanceof ItemType) {
			ItemType cardType = (ItemType) v.getTag();
			openSubCategoriesDialog(cardType);
			return true;
		}
		return false;
	}

	private void showCard(Item card, ItemSpecification itemSpecification, boolean animate) {

		selectedCard = card;
		if (itemSpecification != null)
			selectedItemSpecification = itemSpecification;
		else
			selectedItemSpecification = selectedCard.getSpecifications().get(0);

		imageView.setItem(selectedCard);
		itemView.setItem(selectedCard, selectedItemSpecification);
		itemView.setVisibility(View.VISIBLE);

		if (animate) {
			// TODO getPosition
			// int index = imageAdapter.getPosition(card);
			// if (index >= 0) {
			// gallery.setSelection(index);
			// }
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.
	 * actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		com.actionbarsherlock.view.MenuItem item = menu.add(Menu.NONE, R.id.option_edit, Menu.NONE, "Bearbeiten");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarEdit));

		item = menu.add(Menu.NONE, R.id.option_add, Menu.NONE, "Erstellen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSwordAdd));

		item = menu.add(Menu.NONE, R.id.option_delete, Menu.NONE, "Löschen");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarDelete));

		item = menu.add(Menu.NONE, R.id.option_search, Menu.NONE, "Gegenstand suchen");

		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
				| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSearch));

		final AutoCompleteTextView searchView = new AutoCompleteTextView(getSherlockActivity().getSupportActionBar()
				.getThemedContext());

		searchView.setCompoundDrawablesWithIntrinsicBounds(Util.getThemeResourceId(getActivity(), R.attr.imgSearch), 0,
				0, 0);
		searchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		final int[] to = new int[] { android.R.id.text1 };
		final String[] from = new String[] { "name" };

		// Create a SimpleCursorAdapter for the State Name field.
		final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getSherlockActivity().getSupportActionBar()
				.getThemedContext(), android.R.layout.simple_dropdown_item_1line, null, from, to, 0);

		// Set the CursorToStringConverter, to provide the labels for the
		// choices to be displayed in the AutoCompleteTextView.
		adapter.setCursorToStringConverter(new CursorToStringConverter() {
			public String convertToString(android.database.Cursor cursor) {
				// Get the label for this row out of the "state" column
				if (cursor != null) {
					final int columnIndex = cursor.getColumnIndexOrThrow("name");
					final String str = cursor.getString(columnIndex);

					return str;
				} else
					return null;
			}
		});

		// Set the FilterQueryProvider, to run queries for choices
		// that match the specified input.
		adapter.setFilterQueryProvider(new FilterQueryProvider() {
			public Cursor runQuery(CharSequence constraint) {
				// Search for states whose names begin with the specified
				// letters.
				Cursor cursor = DataManager.getItemsCursor(constraint, null, null);
				return cursor;
			}
		});
		searchView.setAdapter(adapter);
		searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// Get the cursor, positioned to the corresponding row in
				// the
				// result set
				Cursor cursor = (Cursor) adapter.getItem(position);

				// Get the state's capital from this row in the database.
				String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));

				Item item = DataManager.getItemByName(name);
				if (item != null) {
					Util.hideKeyboard(getView());
					chooseType(item.getSpecifications().get(0).getType(), item.getCategory(), item);
				}
			}
		});
		item.setActionView(searchView);
		searchView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// --
		item = menu.add(Menu.NONE, R.id.option_item_filter, Menu.NONE, "Filtern");
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		item.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarFilter));

		super.onCreateOptionsMenu(menu, inflater);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com.
	 * actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	private void chooseType(ItemType type, String category, Item item) {

		categoriesSelected.clear();
		categoriesSelected.add(type);

		for (ImageButton button : categoryButtons) {
			button.setSelected(categoriesSelected.contains(button.getTag()));
		}

		gallery.setVisibility(View.VISIBLE);
		filter(categoriesSelected, category, null);

		if (item != null)
			showCard(item, null, true);
	}

	private void openSubCategoriesDialog(final ItemType cardType) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		final String[] subCategories = cardType.getCategories().toArray(new String[0]);
		builder.setItems(subCategories, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				chooseType(cardType, subCategories[which], null);
				dialog.dismiss();
			}
		});
		builder.setTitle("Unterkategorie auswählen");
		builder.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onOptionsItemSelected(android.view.MenuItem
	 * )
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.option_item_filter) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			String[] categoryNames = new String[categories.length];
			boolean[] categoriesSet = new boolean[categories.length];

			for (int i = 0; i < categories.length; i++) {
				categoryNames[i] = categories[i].name();
				if (categoriesSelected.contains(categories[i]))
					categoriesSet[i] = true;
			}

			builder.setMultiChoiceItems(categoryNames, categoriesSet, this);
			builder.setTitle("Filtern");
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					filter(new ArrayList<ItemType>(categoriesSelected), null, null);
				}
			});
			return true;
		} else if (item.getItemId() == R.id.option_edit) {
			ItemEditActivity.edit(getActivity(), null, selectedCard);
			return true;
		} else if (item.getItemId() == R.id.option_edit) {
			DataManager.deleteItem(selectedCard);
			imageAdapter.notifyDataSetChanged();
			return true;
		} else if (item.getItemId() == R.id.option_add) {
			ItemEditActivity.create(getActivity(), null);
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.content.DialogInterface.OnMultiChoiceClickListener#onClick(android
	 * .content.DialogInterface, int, boolean)
	 */
	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		if (isChecked)
			categoriesSelected.add(categories[which]);
		else
			categoriesSelected.remove(categories[which]);
	}

}
