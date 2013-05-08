/**
 *  This file is part of DsaTab.
 *
 *  DsaTab is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DsaTab is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with DsaTab.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dsatab.data;

import java.util.Date;

import com.dsatab.DsaTabApplication;

/**
 * @author Ganymede
 * 
 */
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
