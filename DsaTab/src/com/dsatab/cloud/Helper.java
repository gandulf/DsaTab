package com.dsatab.cloud;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.dsatab.common.HttpRequest;
import com.gandulf.guilib.util.Debug;

public class Helper {

	static public String postRequest(String token, String... strings) throws Exception {
		StringBuilder body = new StringBuilder();
		body.append("token=");
		body.append(URLEncoder.encode(token, "UTF-8"));

		for (int i = 0; i < strings.length; i = i + 2) {
			body.append("&");
			body.append(URLEncoder.encode(strings[i], "UTF-8"));
			body.append("=");
			body.append(URLEncoder.encode(strings[i + 1], "UTF-8"));
		}
		HttpRequest httpRequest = new HttpRequest();
		String result = httpRequest.sendPost("https://online.helden-software.de/index.php", body.toString(),
				"application/x-www-form-urlencoded; charset=utf-8");

		httpRequest.close();

		if (result == null) {
			throw new IOException("Konnte keine Verbindung zum Austausch Server herstellen.");
		} else if (result.contains("Anmeldung fehlgeschlagen")) {
			throw new AuthorizationException(token);
		}

		return result;
	}

	/**
	 * Deaktiviert alle SSL Checks Nur fÃ¼r selbst-signierte Certifikate bei localhost zu nutzen! Alles andere fÃ¼r zu extremen Sicherheitsproblemen!
	 */
	public static void disableSSLCheck() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			Debug.error(e);
		}

	}

	static public NodeList getDaten(Document daten, String search) {

		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			// search = search.replace("'", "\"");
			XPathExpression expr = xpath.compile(search);
			Object result = expr.evaluate(daten, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			return nodes;
		} catch (Exception ex) {
			System.out.println("Fehlerhafter xpath-Ausdruck: " + search);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			System.out.println("====");
			return null;
			// return getDatenAsString(search);
		}
	}

	/**
	 * Wandelt einen String in ein XML-Dokument um
	 * 
	 * @param xmlstring
	 *            XML-String
	 * @return fertiges DOC
	 * @throws SAXException
	 *             Fehler
	 * @throws IOException
	 *             Fehler
	 * @throws ParserConfigurationException
	 *             Fehler
	 */
	public static Document string2Doc(String xmlstring) throws SAXException, IOException, ParserConfigurationException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xmlstring));
		return db.parse(is);

	}

	static public String getDatenAsString(org.w3c.dom.Document doc, String search) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			// search = search.replace("'", "\"");
			XPathExpression expr = xpath.compile(search);
			Object result = expr.evaluate(doc, XPathConstants.STRING);
			return (String) result;
		} catch (Exception ex) {
			System.out.println("Fehlerhafter xpath-Ausdruck: " + search);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return null;
		}

	}

	static public Long getDatenAsNumber(org.w3c.dom.Document doc, String search) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			// search = search.replace("'", "\"");
			XPathExpression expr = xpath.compile(search);
			Object result = expr.evaluate(doc, XPathConstants.NUMBER);
			return Math.round((Double) result);
		} catch (Exception ex) {
			System.out.println("Fehlerhafter xpath-Ausdruck: " + search);
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			return null;
		}

	}
}
