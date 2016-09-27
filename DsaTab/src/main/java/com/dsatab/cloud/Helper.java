package com.dsatab.cloud;

import com.dsatab.DsaTabApplication;
import com.dsatab.R;
import com.gandulf.guilib.util.Debug;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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

public class Helper {



	static public String postRequest(String token, String... strings) throws Exception {

		OkHttpClient client = new OkHttpClient();

		StringBuilder body = new StringBuilder();
		body.append("token=");
		body.append(URLEncoder.encode(token, "UTF-8"));

		for (int i = 0; i < strings.length; i = i + 2) {
			body.append("&");
			body.append(URLEncoder.encode(strings[i], "UTF-8"));
			body.append("=");
			body.append(URLEncoder.encode(strings[i + 1], "UTF-8"));
		}




		RequestBody requestBody = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded; charset=utf-8"), body.toString());
		Request request = new Request.Builder()
				.url("https://online.helden-software.de/index.php")
				.post(requestBody)
				.addHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5")
				.build();

		String result=null;
		try {
			Response response = client.newCall(request).execute();
			result =  response.body().string();

			if (result.contains("Anmeldung fehlgeschlagen")) {
				throw new AuthorizationException(token);
			}
		} catch (IOException e) {
			Debug.verbose("HttpUtils: " + e);
			throw new IOException(DsaTabApplication.getInstance().getString(
					R.string.message_connection_to_server_failed));
		}

		return result;
	}

	/**
	 * Deaktiviert alle SSL Checks Nur fÃ¼r selbst-signierte Certifikate bei localhost zu nutzen! Alles andere fÃ¼r zu
	 * extremen Sicherheitsproblemen!
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
			Debug.error("Fehlerhafter xpath-Ausdruck: " + search, ex);
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
			Debug.error("Fehlerhafter xpath-Ausdruck: " + search, ex);
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
			Debug.error("Fehlerhafter xpath-Ausdruck: " + search, ex);
			return null;
		}

	}
}
