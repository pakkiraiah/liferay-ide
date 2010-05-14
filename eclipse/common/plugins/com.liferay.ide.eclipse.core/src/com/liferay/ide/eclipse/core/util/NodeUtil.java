/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.core.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author Greg Amerson
 */
public class NodeUtil {

	public static Node getFirstNamedChildNode(Element element, String string) {
		NodeList children = element.getChildNodes();

		if (children != null && children.getLength() > 0) {
			for (int i = 0; i < children.getLength(); i++) {
				Node item = children.item(i);

				if (item.getNodeName().equals(string)) {
					return item;
				}
			}
		}

		return null;
	}

	public static Text setTextContent(Node namespaceNode, String textContent) {
		Text retval = null;

		if (namespaceNode instanceof Text) {
			namespaceNode.setNodeValue(textContent);

			retval = (Text) namespaceNode;
		}
		else if (namespaceNode instanceof Element) {
			Element namespaceElement = (Element) namespaceNode;

			removeChildren(namespaceElement);

			retval = namespaceElement.getOwnerDocument().createTextNode(textContent);

			namespaceElement.appendChild(retval);
		}
		return retval;
	}

	public static void removeChildren(Element element) {
		while (element != null && element.hasChildNodes()) {
			element.removeChild(element.getFirstChild());
		}
	}

	public static String getChildElementContent(Node parent, String childElement) {

		String retval = null;

		NodeList children = parent.getChildNodes();

		if (children != null && children.getLength() > 0) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);

				if (child instanceof Element && child.getNodeName().equals(childElement)) {
					return getTextContent((Element) child);
				}
			}
		}
		return retval;
	}

	public static String getTextContent(Element element) {

		String retval = null;

		if (element != null) {
			NodeList childNodes = element.getChildNodes();

			for (int i = 0; i < childNodes.getLength(); i++) {
				Node child = childNodes.item(i);

				if (child instanceof Text) {
					return child.getNodeValue();
				}
			}
		}
		return retval;
	}

}