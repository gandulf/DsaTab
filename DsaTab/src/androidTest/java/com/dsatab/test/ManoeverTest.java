package com.dsatab.test;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

@RunWith(AndroidJUnit4.class)
public class ManoeverTest extends InstrumentationTestCase {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        // Injecting the Instrumentation instance is required for your test to run with AndroidJUnitRunner.
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
    }

@Test
	public void testLoadManoever() throws Exception{

		String tag = "Finte";
		String data = null;

        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();
        InputStream is = getInstrumentation().getTargetContext().getResources().getAssets().open("data/webinfo.html");
        NodeList shows = (NodeList) xPath.evaluate("//div[@id='" + tag + "']", new InputSource(is),
                XPathConstants.NODESET);

        for (int i = 0; i < shows.getLength(); i++) {
            Element show = (Element) shows.item(i);

            StringWriter writer = new StringWriter();
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(show), new StreamResult(writer));
            data = writer.toString();
        }
        Assert.assertNotNull(data);
	}

}
