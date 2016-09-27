package com.dsatab.data.notes;

import com.dsatab.DsaTabApplication;

import java.util.Date;

public class ChangeEvent {

	// <ereignis Abenteuerpunkte="-4" Alt="1" Info="Gegenseitiges Lehren"
	// Neu="2" obj="Wettervorhersage" text="Talent steigern"
	// time="1309649750436" version="5.1.3"/>

	private Date time;
	private Integer xps, oldValue, newValue;
	private String info, object, version, text;

	public ChangeEvent(Integer newValue, Integer oldValue, String obj, String text) {
		this();
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.object = obj;
		this.text = text;
	}

	/**
	 * 
	 */
	public ChangeEvent() {
		setTime(new Date());
		setVersion("DsaTab " + DsaTabApplication.getInstance().getPackageVersionName());
	}

	public Integer getExperiencePoints() {
		return xps;
	}

	public void setExperiencePoints(Integer xp) {
		this.xps = xp;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Integer getOldValue() {
		return oldValue;
	}

	public void setOldValue(Integer oldValue) {
		this.oldValue = oldValue;
	}

	public Integer getNewValue() {
		return newValue;
	}

	public void setNewValue(Integer newValue) {
		this.newValue = newValue;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
