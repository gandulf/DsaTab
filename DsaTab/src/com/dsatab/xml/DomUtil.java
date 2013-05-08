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
package com.dsatab.xml;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

/**
 * 
 * 
 */
public class DomUtil {

	public static Element getChildByTagName(Element parent, String subParentTagName, String tagName) {

		if (subParentTagName != null) {
			Element subParent = parent.getChild(subParentTagName);

			if (subParent != null) {
				parent = subParent;
			}
		}

		return parent.getChild(tagName);
	}

	public static String getChildValue(Element node, String childTagName, String childParamName) {
		Element child = node.getChild(childTagName);

		if (child != null) {
			return child.getAttributeValue(childParamName);
		} else {
			return null;
		}
	}

	public static List<Element> getChildrenByTagName(Element parent, String subParentTagName, String tagName) {

		List<Element> children = new LinkedList<Element>();
		List<Element> parentList = null;

		if (subParentTagName != null) {

			List<Element> subParents = parent.getChildren(subParentTagName);

			if (!subParents.isEmpty())
				parentList = subParents;

		}

		if (parentList != null && !parentList.isEmpty()) {
			for (Element subParent : parentList) {
				children.addAll(subParent.getChildren(tagName));
			}
		} else {
			children = parent.getChildren(tagName);
		}

		return children;
	}
}
