package com.dsatab.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.Exception;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.test.AndroidTestCase;

public class ManoeverTest extends AndroidTestCase {

	public ManoeverTest() {
		super();
	}

	public void testLoadManoever() throws Exception{

		String tag = "Finte";
		String data = null;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        InputStream is = getContext().getResources().getAssets().open("data/manoever.html");
        NodeList shows = (NodeList) xPath.evaluate("//div[@id='" + tag + "']", new InputSource(is),
                XPathConstants.NODESET);

        for (int i = 0; i < shows.getLength(); i++) {
            Element show = (Element) shows.item(i);

            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(show), new StreamResult(writer));
            data = writer.toString();
        }


	}

}
