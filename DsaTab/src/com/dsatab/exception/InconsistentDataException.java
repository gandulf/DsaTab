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
package com.dsatab.exception;

import com.dsatab.common.DsaTabRuntimeException;

/**
 * @author Ganymede
 * 
 */
public class InconsistentDataException extends DsaTabRuntimeException {

	private static final long serialVersionUID = -3303083601903889609L;

	public InconsistentDataException() {
		super();

	}

	public InconsistentDataException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);

	}

	public InconsistentDataException(String detailMessage) {
		super(detailMessage);

	}

	public InconsistentDataException(Throwable throwable) {
		super(throwable);

	}

}
