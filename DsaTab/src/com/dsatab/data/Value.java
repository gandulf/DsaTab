package com.dsatab.data;

public interface Value {

	public String getName();

	public Integer getValue();

	public void setValue(Integer value);

	public void reset();

	public int getMinimum();

	public int getMaximum();

	public Integer getReferenceValue();

}
