package com.jke.logic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class StockQuoteLogic {

	private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	public static String getQuote(String tickerSymbol) {
		
		String returnValue = "";
		
		try {
			URL url = new URL("http://www.google.com/ig/api?stock=" + tickerSymbol);
			URLConnection connection = url.openConnection();
			connection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)");
			
			connection.connect();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(connection.getInputStream());
			
			returnValue = parseDocument(document);
			
		} catch(MalformedURLException ex){
			returnValue = ex.getMessage();
		} catch (IOException ex) {
			returnValue = ex.getMessage();
		} catch (ParserConfigurationException ex) {
			returnValue = ex.getMessage();
		} catch (SAXException ex) {
			returnValue = ex.getMessage();
		}
		
		return returnValue;
	}
	
	private static String parseDocument(Document document) {
		Element rootElement = document.getDocumentElement();
		
		NodeList financeElements = rootElement.getElementsByTagName("finance");
		if(financeElements != null && financeElements.getLength() > 0) {
			Element stockElement = (Element) financeElements.item(0);
			NodeList lastElements = stockElement.getElementsByTagName("last");
			if(lastElements != null && lastElements.getLength() > 0) {
				Element lastElement = (Element)lastElements.item(0);
				return lastElement.getAttribute("data");
			}
		}
		
		return "Not Found";
	}
}
