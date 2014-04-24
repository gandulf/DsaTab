package com.dsatab.fragment;

import java.util.List;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.dsatab.R;
import com.dsatab.common.StyleableSpannableStringBuilder;
import com.dsatab.data.AbstractBeing;
import com.dsatab.data.Animal;
import com.dsatab.data.AnimalAttack;
import com.dsatab.data.Attribute;
import com.dsatab.data.Hero;
import com.dsatab.data.Value;
import com.dsatab.data.enums.AttributeType;
import com.dsatab.data.modifier.Modificator;
import com.dsatab.util.DsaUtil;
import com.dsatab.util.Util;
import com.dsatab.view.ItemListItem;
import com.dsatab.view.listener.HeroChangedListener;

public class AnimalFragment extends BaseProfileFragment {

	private static final int ANIMAL_GROUP_ID = 1001;
	private static final int ANIMAL_MENU_ID = 1000;

	private TextView tfTotalLe, tfTotalAu, tfTotalAe, tfTotalKe, tfINI, tfLO, tfRS, tfMR2;
	private TextView tfLabelINI, tfLabelLO, tfLabelRS;

	private View charAttributesList;

	private int animalIndex = 0;

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		if (getHero().getAnimals().size() > 1) {
			SubMenu subMenu = menu.addSubMenu(Menu.NONE, ANIMAL_MENU_ID, 0, R.string.choose_animal);
			MenuItemCompat.setShowAsAction(subMenu.getItem(), MenuItemCompat.SHOW_AS_ACTION_ALWAYS
					| MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
			subMenu.setIcon(Util.getThemeResourceId(getActivity(), R.attr.imgBarSet));
			for (int i = 0; i < getHero().getAnimals().size(); i++) {
				Animal animal = getHero().getAnimals().get(i);
				subMenu.add(ANIMAL_GROUP_ID, i, i, animal.getTitle());
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

		Util.applyRowStyle((TableLayout) root.findViewById(R.id.gen_attributes));

		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		fillAttributeLabel((View) tfLabelMR.getParent(), AttributeType.Magieresistenz);
		fillAttributeLabel((View) tfLabelLO.getParent(), AttributeType.Loyalität);
		fillAttributeLabel((View) tfLabelRS.getParent(), AttributeType.Rüstungsschutz);

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);
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
				fillAttribute(attr);
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

}