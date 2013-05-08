package com.dsatab.view.listener;

import java.util.List;

import com.dsatab.data.Value;
import com.dsatab.data.modifier.Modificator;

public interface HeroChangedListener {

	void onValueChanged(Value value);

	void onModifierAdded(Modificator value);

	void onModifierRemoved(Modificator value);

	void onModifierChanged(Modificator value);

	void onModifiersChanged(List<Modificator> values);

	void onPortraitChanged();
}
