package com.dsatab.xml;

import java.util.LinkedList;
import java.util.List;

import org.jdom2.Element;

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
