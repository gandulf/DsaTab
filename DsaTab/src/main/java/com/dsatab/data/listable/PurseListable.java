package com.dsatab.data.listable;

import com.dsatab.data.Purse.Currency;

public class PurseListable implements Listable {

	private Currency currency;

	public PurseListable(Currency currency) {
		this.currency = currency;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

    @Override
    public long getId() {
        return hashCode();
    }
}
