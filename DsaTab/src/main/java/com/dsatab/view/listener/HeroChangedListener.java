package com.dsatab.view.listener;

import com.dsatab.data.Value;
import com.dsatab.data.modifier.Modificator;

import java.util.List;

public interface HeroChangedListener {

	void onValueChanged(Value value);

	void onModifierAdded(Modificator value);

	void onModifierRemoved(Modificator value);

	void onModifierChanged(Modificator value);

	void onModifiersChanged(List<Modificator> values);

	void onPortraitChanged();
}
