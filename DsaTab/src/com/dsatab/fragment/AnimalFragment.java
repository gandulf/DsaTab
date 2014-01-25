package com.dsatab.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Animal;
import com.dsatab.data.AnimalAttack;
import com.dsatab.data.Attribute;
import com.dsatab.data.Feature;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.ClickSpan;
import com.dsatab.util.ClickSpan.OnSpanClickListener;
import com.dsatab.util.Debug;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.PortraitChooserDialog;
import com.dsatab.view.PortraitViewDialog;
import com.dsatab.view.WebInfoDialog;
import com.dsatab.view.listener.HeroChangedListener;
import com.dsatab.xml.DataManager;

public class AnimalFragment extends BaseAttributesFragment implements OnClickListener, OnLongClickListener {

	private static final String PREF_SHOW_FEATURE_COMMENTS = "SHOW_COMMENTS";

	private static final int ANIMAL_GROUP_ID = 1001;
	private static final int ANIMAL_MENU_ID = 1000;

	private static final int ACTION_PHOTO = 1;
	private static final int ACTION_GALERY = 2;

	private TextView tfSpecialFeatures, tfSpecialFeaturesTitle, tfAdvantages, tfAdvantagesTitle, tfDisadvantages,
			tfDisadvantgesTitle;
	private TextView tfTotalLe, tfTotalAu, tfTotalAe, tfTotalKe, tfINI, tfLO, tfRS, tfMR2;
	private TextView tfLabelINI, tfLabelLO, tfLabelRS;

	private View charAttributesList;

	private ImageView portraitView;

	protected ActionMode mMode;

	protected ActionMode.Callback mCallback = new PortraitActionMode();

	private int animalIndex = 0;

	private final class PortraitActionMode implements ActionMode.Callback {
		@Override
		public boolean onActionItemClicked(ActionMode mode, com.actionbarsherlock.view.MenuItem item) {
			if (getBaseActivity() != null) {
				switch (item.getItemId()) {
				case R.id.option_take_photo:
					Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					getActivity().startActivityForResult(camera, ACTION_PHOTO);
					break;
				case R.id.option_pick_image:
					File portraitsDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS);
					Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
					photoPickerIntent.setDataAndType(Uri.fromFile(portraitsDir), "image/*");
					getActivity().startActivityForResult(Intent.createChooser(photoPickerIntent, "Bild auswählen"),
							ACTION_GALERY);
					break;
				case R.id.option_view_portrait:
					showPortrait();
					break;
				case R.id.option_pick_avatar:
					pickPortrait();
					break;
				}
			}
			mode.finish();
			return true;
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			if (getAnimal() != null) {
				mode.getMenuInflater().inflate(R.menu.portrait_popupmenu, menu);
				mode.setTitle("Portrait");
				mode.setSubtitle(null);
				if (getAnimal().getPortrait() == null) {
					menu.findItem(R.id.option_view_portrait).setVisible(false);
				}
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			if (getActivity() != null) {
				resetPortaitView();
			}
			mMode = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.view.ActionMode.Callback#onPrepareActionMode
		 * (com.actionbarsherlock.view.ActionMode, com.actionbarsherlock.view.Menu)
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

	}

	/**
	 * 
	 */
	public AnimalFragment() {
		this.inverseColors = false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (getHero().getAnimals().size() > 1) {
			SubMenu subMenu = menu.addSubMenu(Menu.NONE, ANIMAL_MENU_ID, 0, "Tier auswählen");
			subMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			subMenu.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet));
			for (int i = 0; i < getHero().getAnimals().size(); i++) {
				Animal animal = getHero().getAnimals().get(i);
				MenuItem animalItem = subMenu.add(ANIMAL_GROUP_ID, i, i, animal.getTitle());
			}
			subMenu.setGroupCheckable(ANIMAL_GROUP_ID, true, true);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem animalMenu = menu.findItem(ANIMAL_MENU_ID);
		if (animalMenu != null && animalMenu.hasSubMenu()) {
			SubMenu subMenu = animalMenu.getSubMenu();
			if (animalIndex >= 0 && animalIndex < subMenu.size()) {
				subMenu.getItem(animalIndex).setChecked(true);
			}
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getGroupId() == ANIMAL_GROUP_ID) {
			animalIndex = item.getItemId();
			item.setChecked(true);
			onAnimalLoaded(getAnimal());
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup,
	 * android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = configureContainerView(inflater.inflate(R.layout.sheet_animal, container, false));

		charAttributesList = root.findViewById(R.id.gen_attributes);

		tfTotalAe = (TextView) root.findViewById(R.id.attr_total_ae);
		tfTotalKe = (TextView) root.findViewById(R.id.attr_total_ke);
		tfTotalLe = (TextView) root.findViewById(R.id.attr_total_le);
		tfTotalAu = (TextView) root.findViewById(R.id.attr_total_au);

		tfINI = (TextView) root.findViewById(R.id.attr_ini);
		tfLO = (TextView) root.findViewById(R.id.attr_lo);
		tfRS = (TextView) root.findViewById(R.id.attr_rs);
		tfMR2 = (TextView) root.findViewById(R.id.attr_mr2);

		tfLabelINI = (TextView) root.findViewById(R.id.attr_ini_label);
		tfLabelLO = (TextView) root.findViewById(R.id.attr_lo_label);
		tfLabelRS = (TextView) root.findViewById(R.id.attr_rs_label);

		tfSpecialFeatures = (TextView) root.findViewById(R.id.gen_specialfeatures);
		tfSpecialFeaturesTitle = (TextView) root.findViewById(R.id.gen_specialfeatures_title);

		tfAdvantages = (TextView) root.findViewById(R.id.gen_advantages);
		tfAdvantagesTitle = (TextView) root.findViewById(R.id.gen_advantages_title);

		tfDisadvantages = (TextView) root.findViewById(R.id.gen_disadvantages);
		tfDisadvantgesTitle = (TextView) root.findViewById(R.id.gen_disadvantages_title);

		portraitView = (ImageView) root.findViewById(R.id.gen_portrait);

		Util.applyRowStyle((TableLayout) root.findViewById(R.id.gen_attributes));

		return root;
	}

	private void pickPortrait() {
		final PortraitChooserDialog pdialog = new PortraitChooserDialog(getBaseActivity());

		File portraitDir = DsaTabApplication.getDirectory(DsaTabApplication.DIR_PORTRAITS);
		File[] files = portraitDir.listFiles();
		List<Uri> portraitPaths = null;
		if (files != null) {
			portraitPaths = new ArrayList<Uri>(files.length);

			for (File file : files) {
				if (file.isFile()) {
					portraitPaths.add(Uri.fromFile(file));
				}
			}
		}

		if (portraitPaths == null || portraitPaths.isEmpty()) {
			String path = portraitDir.getAbsolutePath();
			Toast.makeText(
					getBaseActivity(),
					"Keine Portraits gefunden. Kopiere deine eigenen auf deine SD-Karte unter \"" + path
							+ "\" oder lade die Standardportraits in den Einstellungen herunter.", Toast.LENGTH_LONG)
					.show();
		} else {
			pdialog.setImages(portraitPaths);
			pdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					if (pdialog.getImageUri() != null) {
						getAnimal().setPortraitUri(pdialog.getImageUri());
					}
				}
			});
			pdialog.show();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		tfSpecialFeatures.setOnLongClickListener(this);
		tfSpecialFeaturesTitle.setOnLongClickListener(this);
		tfAdvantages.setOnLongClickListener(this);
		tfAdvantagesTitle.setOnLongClickListener(this);
		tfDisadvantages.setOnLongClickListener(this);
		tfDisadvantgesTitle.setOnLongClickListener(this);

		portraitView.setOnLongClickListener(this);
		portraitView.setOnClickListener(this);

		fillAttributeLabel((View) tfLabelMR.getParent(), AttributeType.Magieresistenz);
		fillAttributeLabel((View) tfLabelLO.getParent(), AttributeType.Loyalität);

		fillAttributeLabel(tfLabelINI, AttributeType.ini);

		super.onActivityCreated(savedInstanceState);
	}

	protected void onAttachListener(Hero hero) {
		if (hero != null) {
			Animal animal = getAnimal();
			if (animal != null && this instanceof HeroChangedListener) {
				animal.addHeroChangedListener(this);
			}
		}
	}

	protected void onDetachListener(Hero hero) {
		if (hero != null) {
			Animal animal = getAnimal();
			if (animal != null && this instanceof HeroChangedListener) {
				animal.removeHeroChangedListener(this);
			}
		}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.activity.BaseMainActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case ACTION_GALERY:
			if (resultCode == Activity.RESULT_OK && getAnimal() != null) {

				Bitmap bitmap = Util.retrieveBitmap(getActivity(), data, 300);

				if (bitmap != null) {
					File outputfile = saveBitmap(bitmap);
					if (outputfile != null) {
						// set uri for currently selected player
						getAnimal().setPortraitUri(outputfile.toURI());
						updatePortrait(getAnimal());
					}
				} else {
					Toast.makeText(getActivity(),
							"Konnte Bild nicht öffnen. Verwende die Standard Gallerie um eine bild auszuwählen.",
							Toast.LENGTH_LONG).show();
				}
			}

			break;
		case ACTION_PHOTO:

			if (resultCode == Activity.RESULT_OK && getAnimal() != null) {

				// Retrieve image taking in camera activity
				Bundle b = data.getExtras();
				Bitmap pic = (Bitmap) b.get("data");

				if (pic != null) {

					File outputfile = saveBitmap(pic);
					if (outputfile != null) {
						// set uri for currently selected player
						getAnimal().setPortraitUri(outputfile.toURI());

						updatePortrait(getAnimal());
					}
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private File saveBitmap(Bitmap pic) {
		FileOutputStream fOut = null;
		try {

			String photoName = "photo" + Util.convertNonAscii(getHero().getName() + "_" + getAnimal().getName());
			fOut = DsaTabApplication.getInstance().openFileOutput(photoName, Context.MODE_PRIVATE);
			pic.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();

			File outputfile = DsaTabApplication.getInstance().getFileStreamPath(photoName);
			return outputfile;
		} catch (FileNotFoundException e) {
			Debug.error(e);
		} catch (IOException e) {
			Debug.error(e);
		} finally {
			if (fOut != null) {
				try {
					fOut.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	private void showPortrait() {
		Bitmap portrait = getAnimal().getPortrait();

		if (portrait != null) {
			PortraitViewDialog viewDialog = new PortraitViewDialog(getActivity(), getAnimal().getName(), portrait);
			viewDialog.show();
		} else {
			pickPortrait();
		}
	}

	protected void resizePortaiView() {
		LayoutParams layoutParams = (LayoutParams) portraitView.getLayoutParams();
		int imageWidth = portraitView.getWidth(), imageHeight = portraitView.getHeight();

		if (portraitView.getDrawable().getIntrinsicWidth() > 0)
			imageWidth = portraitView.getDrawable().getIntrinsicWidth();

		if (portraitView.getDrawable().getIntrinsicHeight() > 0)
			imageHeight = portraitView.getDrawable().getIntrinsicHeight();

		float ratio = portraitView.getHeight() / (float) imageHeight;
		layoutParams.width = (int) (imageWidth * ratio);

		portraitView.requestLayout();
	}

	protected void resetPortaitView() {
		LayoutParams layoutParams = (LayoutParams) portraitView.getLayoutParams();
		layoutParams.width = DsaTabApplication.getInstance().getResources()
				.getDimensionPixelSize(R.dimen.portrait_width_small);
		layoutParams.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
		portraitView.requestLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnLongClickListener#onLongClick(android.view.View)
	 */
	@Override
	public boolean onLongClick(View v) {
		switch (v.getId()) {
		case R.id.gen_portrait:
			if (getAnimal() == null)
				return false;

			if (mMode == null) {
				mMode = ((SherlockFragmentActivity) getActivity()).startActionMode(mCallback);
				customizeActionModeCloseButton();
				mMode.invalidate();
				resizePortaiView();
			}
			return true;
		case R.id.gen_specialfeatures:
		case R.id.gen_specialfeatures_title:
		case R.id.gen_advantages:
		case R.id.gen_advantages_title:
		case R.id.gen_disadvantages:
		case R.id.gen_disadvantages_title: {
			boolean showComments = preferences.getBoolean(PREF_SHOW_FEATURE_COMMENTS, true);

			showComments = !showComments;
			Editor edit = preferences.edit();
			edit.putBoolean(PREF_SHOW_FEATURE_COMMENTS, showComments);
			edit.commit();

			fillSpecialFeatures(getAnimal());
			break;
		}
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gen_portrait:

			if (getAnimal() == null)
				return;

			if (getAnimal().getPortrait() == null) {
				onLongClick(v);
			} else {
				showPortrait();
			}
			break;
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
			case Magieresistenz2:
				fillAttributeValue(tfMR2, attr);
				break;
			case Rüstungsschutz:
				fillAttributeValue(tfRS, attr);
				break;
			case Loyalität:
				fillAttributeValue(tfLO, attr);
				break;
			case ini:
				fillAttributeValue(tfINI, attr);
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
				fillAttribute(attr, false);
				break;
			default:
				// do nothing
				break;
			}

		}
	}

	public void onAnimalLoaded(Animal animal) {
		updateValues();

		fillAttributeValue(tfAE, AttributeType.Astralenergie_Aktuell);
		fillAttributeValue(tfAU, AttributeType.Ausdauer_Aktuell);
		fillAttributeValue(tfKE, AttributeType.Karmaenergie_Aktuell);
		fillAttributeValue(tfLE, AttributeType.Lebensenergie_Aktuell);
		fillAttributeValue(tfMR, AttributeType.Magieresistenz);
		fillAttributeValue(tfMR2, AttributeType.Magieresistenz2);
		fillAttributeValue(tfLO, AttributeType.Loyalität);
		fillAttributeValue(tfRS, AttributeType.Rüstungsschutz);

		fillAttributeValue(tfTotalLe, AttributeType.Lebensenergie);
		fillAttributeValue(tfTotalAu, AttributeType.Ausdauer);

		if (getAnimal() == null || getAnimal().getAttributeValue(AttributeType.Karmaenergie) == null
				|| getAnimal().getAttributeValue(AttributeType.Karmaenergie) == 0) {
			findViewById(R.id.row_ke).setVisibility(View.GONE);
		} else {
			fillAttributeValue(tfTotalKe, AttributeType.Karmaenergie);
			findViewById(R.id.row_ke).setVisibility(View.VISIBLE);
		}

		if (getAnimal() == null || getAnimal().getAttributeValue(AttributeType.Astralenergie) == null
				|| getAnimal().getAttributeValue(AttributeType.Astralenergie) == 0) {
			findViewById(R.id.row_ae).setVisibility(View.GONE);
		} else {
			fillAttributeValue(tfTotalAe, AttributeType.Astralenergie);
			findViewById(R.id.row_ae).setVisibility(View.VISIBLE);
		}

		// base
		if (getAnimal() != null) {
			((TextView) findViewById(R.id.gen_name)).setText(getAnimal().getTitle());
			((TextView) findViewById(R.id.gen_family)).setText(getAnimal().getFamily());
			((TextView) findViewById(R.id.gen_species)).setText(getAnimal().getSpecies());

			((TextView) findViewById(R.id.gen_groesse)).setText(getAnimal().getHeight() + " cm");
			((TextView) findViewById(R.id.gen_gewicht)).setText(Util.toString(DsaUtil.unzenToStein(getAnimal()
					.getWeight())) + " Stein");
		} else {
			((TextView) findViewById(R.id.gen_name)).setText(null);
			((TextView) findViewById(R.id.gen_family)).setText(null);
			((TextView) findViewById(R.id.gen_species)).setText(null);

			((TextView) findViewById(R.id.gen_groesse)).setText(null);
			((TextView) findViewById(R.id.gen_gewicht)).setText(null);
		}
		//

		fillSpecialFeatures(getAnimal());

		// --
		ImageView portrait = (ImageView) findViewById(R.id.gen_portrait);
		portrait.setOnClickListener(this);
		updatePortrait(getAnimal());

		TableLayout attribute2 = (TableLayout) findViewById(R.id.gen_attributes2);
		Util.applyRowStyle(attribute2);

		// -- fill attacks

		LinearLayout attackLayout = (LinearLayout) findViewById(R.id.animal_attacks);
		attackLayout.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		if (getAnimal() != null && getAnimal().getAnimalAttacks() != null) {
			int position = 0;
			for (AnimalAttack animalAttack : getAnimal().getAnimalAttacks()) {
				ItemListItem listItem = (ItemListItem) inflater.inflate(R.layout.item_listitem_equippeditem,
						attackLayout, false);

				StyleableSpannableStringBuilder title = new StyleableSpannableStringBuilder();
				if (!TextUtils.isEmpty(animalAttack.getName())) {
					title.append(animalAttack.getName());
				}
				Util.appendValue(getHero(), title, animalAttack.getAttack(), animalAttack.getDefense(), false);
				listItem.text1.setText(title);
				listItem.text2.setText(animalAttack.getTp().toString());
				listItem.text3.setText(animalAttack.getDistance());

				if (animalAttack.getAttack() != null) {
					listItem.icon1.setVisibility(View.VISIBLE);
					listItem.icon1.setImageResource(R.drawable.icon_attack);
					listItem.icon1.setTag(animalAttack.getAttack());
					listItem.icon1.setOnClickListener(getProbeListener());
				}

				if (animalAttack.getDefense() != null) {
					listItem.icon2.setVisibility(View.VISIBLE);
					listItem.icon2.setImageResource(R.drawable.icon_shield);
					listItem.icon2.setTag(animalAttack.getDefense());
					listItem.icon2.setOnClickListener(getProbeListener());
				}

				Util.applyRowStyle(listItem, position++);
				attackLayout.addView(listItem);
			}
		}
	}

	public void onHeroLoaded(Hero hero) {
		onAnimalLoaded(getAnimal());
	}

	public AbstractBeing getBeing() {
		return getAnimal();
	}

	protected Animal getAnimal() {
		if (getHero() != null && getHero().getAnimals() != null && animalIndex < getHero().getAnimals().size()) {
			return getHero().getAnimals().get(animalIndex);
		} else
			return null;
	}

	protected void updateValues() {
		fillAttributesList(charAttributesList);

		StyleableSpannableStringBuilder sb = new StyleableSpannableStringBuilder();
		if (getBeing() != null && getBeing().getAttribute(AttributeType.Geschwindigkeit) != null) {
			sb.append(Util.toString(getBeing().getAttributeValue(AttributeType.Geschwindigkeit)));
		}
		if (getBeing() != null && getBeing().getAttribute(AttributeType.Geschwindigkeit2) != null) {
			sb.append("/");
			sb.append(Util.toString(getBeing().getAttributeValue(AttributeType.Geschwindigkeit2)));
		}
		if (getBeing() != null && getBeing().getAttribute(AttributeType.Geschwindigkeit3) != null) {
			sb.append("/");
			sb.append(Util.toString(getBeing().getAttributeValue(AttributeType.Geschwindigkeit3)));
		}
		tfGS.setText(sb);

		if (getAnimal() != null && getAnimal().getIniDice() != null)
			tfINI.setText(getAnimal().getIniDice().toString());
		else
			tfINI.setText(null);
		fillAttributeValue(tfLO, AttributeType.Loyalität, false);
	}

	protected void updatePortrait(AbstractBeing being) {
		Bitmap drawable = null;
		if (being != null) {
			drawable = being.getPortrait();
		}
		if (drawable != null)
			portraitView.setImageBitmap(drawable);
		else
			portraitView.setImageResource(R.drawable.profile_picture);
	}

	protected void fillAttributesList(View view) {
		if (getView() == null)
			return;

		if (tfMR == null || tfLabelMU == null) {
			findViews(getView());
		}
		fillAttributeValue(tfMU, AttributeType.Mut);
		fillAttributeValue(tfKL, AttributeType.Klugheit);
		fillAttributeValue(tfIN, AttributeType.Intuition);
		fillAttributeValue(tfCH, AttributeType.Charisma);
		fillAttributeValue(tfFF, AttributeType.Fingerfertigkeit);
		fillAttributeValue(tfGE, AttributeType.Gewandtheit, false);
		fillAttributeValue(tfKO, AttributeType.Konstitution);
		fillAttributeValue(tfKK, AttributeType.Körperkraft);

		fillAttributeLabel((View) tfLabelMU.getParent(), AttributeType.Mut);
		fillAttributeLabel((View) tfLabelKL.getParent(), AttributeType.Klugheit);
		fillAttributeLabel((View) tfLabelIN.getParent(), AttributeType.Intuition);
		fillAttributeLabel((View) tfLabelCH.getParent(), AttributeType.Charisma);
		fillAttributeLabel((View) tfLabelFF.getParent(), AttributeType.Fingerfertigkeit);
		fillAttributeLabel((View) tfLabelGE.getParent(), AttributeType.Gewandtheit);
		fillAttributeLabel((View) tfLabelKO.getParent(), AttributeType.Konstitution);
		fillAttributeLabel((View) tfLabelKK.getParent(), AttributeType.Körperkraft);
	}

	/**
	 * @param being
	 */
	private void fillSpecialFeatures(AbstractBeing being) {

		boolean showComments = preferences.getBoolean(PREF_SHOW_FEATURE_COMMENTS, true);

		StyleableSpannableStringBuilder featureBuilder = new StyleableSpannableStringBuilder();
		StyleableSpannableStringBuilder advantageBuilder = new StyleableSpannableStringBuilder();
		StyleableSpannableStringBuilder disadvantageBuilder = new StyleableSpannableStringBuilder();

		tfSpecialFeatures.setMovementMethod(LinkMovementMethod.getInstance());
		String[] featureInfos = DataManager.getWebInfos(getActivity()).toArray(new String[0]);
		Arrays.sort(featureInfos);

		OnSpanClickListener linkClicker = new OnSpanClickListener() {

			@Override
			public void onClick(CharSequence tag, ClickSpan v) {
				if (getActivity() != null) {
					WebInfoDialog.show(getActivity(), tag);
				}
			}
		};
		StyleableSpannableStringBuilder currentBuilder = null;
		if (being != null && !being.getSpecialFeatures().isEmpty()) {
			for (Feature feature : being.getSpecialFeatures().values()) {
				switch (feature.getType().type()) {
				case Advantage:
					currentBuilder = advantageBuilder;
					break;
				case Disadvantage:
					currentBuilder = disadvantageBuilder;
					break;
				case SpecialFeature:
					currentBuilder = featureBuilder;
					break;
				}

				if (currentBuilder.length() > 0) {
					currentBuilder.append(", ");
				}
				String tag = feature.getType().xmlName();

				if (Arrays.binarySearch(featureInfos, tag) >= 0) {
					currentBuilder.appendClick(linkClicker, feature.toString(), tag);
				} else {
					currentBuilder.append(feature.toString());
				}

				if (showComments && !TextUtils.isEmpty(feature.getComment())) {
					currentBuilder.appendColor(Color.GRAY, " (");
					currentBuilder.appendColor(Color.GRAY, feature.getComment());
					currentBuilder.appendColor(Color.GRAY, ")");
				}

			}
		}

		if (featureBuilder.length() > 0) {
			tfSpecialFeaturesTitle.setVisibility(View.VISIBLE);
			tfSpecialFeatures.setVisibility(View.VISIBLE);
			tfSpecialFeatures.setText(featureBuilder);
		} else {
			tfSpecialFeatures.setVisibility(View.GONE);
			tfSpecialFeaturesTitle.setVisibility(View.GONE);
		}

		if (advantageBuilder.length() > 0) {
			tfAdvantagesTitle.setVisibility(View.VISIBLE);
			tfAdvantages.setVisibility(View.VISIBLE);
			tfAdvantages.setText(advantageBuilder);
		} else {
			tfAdvantages.setVisibility(View.GONE);
			tfAdvantagesTitle.setVisibility(View.GONE);
		}

		if (disadvantageBuilder.length() > 0) {
			tfDisadvantgesTitle.setVisibility(View.VISIBLE);
			tfDisadvantages.setVisibility(View.VISIBLE);
			tfDisadvantages.setText(disadvantageBuilder);
		} else {
			tfDisadvantages.setVisibility(View.GONE);
			tfDisadvantgesTitle.setVisibility(View.GONE);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dsatab.view.listener.HeroChangedListener#onPortraitChanged()
	 */
	@Override
	public void onPortraitChanged() {
		updatePortrait(getAnimal());
	}

}