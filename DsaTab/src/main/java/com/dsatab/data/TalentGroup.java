package com.dsatab.data;

import com.dsatab.data.Talent.Flags;
import com.dsatab.data.enums.TalentGroupType;
import com.dsatab.data.listable.Listable;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

public class TalentGroup  implements Listable {

	private List<Talent> talents = new LinkedList<Talent>();

	private TalentGroupType type;

	private EnumSet<Flags> flags = EnumSet.noneOf(Flags.class);

	public TalentGroup(TalentGroupType name) {
		this.type = name;
	}

	public void setTalents(List<Talent> talents) {
		this.talents = talents;
	}

	public boolean hasFlag(Flags flag) {
		return flags.contains(flag);
	}

	public void addFlag(Flags flag) {
		flags.add(flag);
	}

	public List<Talent> getTalents() {
		return talents;
	}

	public TalentGroupType getType() {
		return type;
	}

    @Override
    public long getId() {
        return hashCode();
    }
}
