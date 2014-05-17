package net.semanticmetadata.lire.solr;

import net.semanticmetadata.lire.imageanalysis.ColorLayout;
import org.apache.commons.codec.binary.Base64;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

/**
 * Created by ferdous on 5/17/14.
 */
class SolrResponseHandler extends DefaultHandler {
	boolean isInDocument;
	boolean isInHistogram;
	boolean isInId;
	int countResults = 0;
	StringBuilder hist = new StringBuilder(256);
	StringBuilder id = new StringBuilder(256);
	private ArrayList<ResultItem> results = new ArrayList<ResultItem>(500);

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.startsWith("doc")) {
			isInDocument = true;
		} else if (isInDocument && qName.startsWith("str")) {
			if (attributes.getValue("name") != null && attributes.getValue("name").equals("cl_hi")) {
				isInHistogram = true;
				hist.delete(0, hist.length());
			} else if (attributes.getValue("name") != null && attributes.getValue("name").equals("id")) {
				isInId = true;
				id.delete(0, id.length());
			}

		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if (qName.startsWith("doc")) {
			isInDocument = false;
			countResults++;
			System.out.println(id.toString() + ", " + hist.toString());
			ColorLayout cl = new ColorLayout();
			cl.setByteArrayRepresentation(Base64.decodeBase64(hist.toString()));
			results.add(new ResultItem(cl, id.toString()));
		} else if (qName.startsWith("str")) {
			isInHistogram = false;
			isInId = false;
		} else if (qName.startsWith("result")) {
			System.out.println(countResults + " results found");
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (isInHistogram) hist.append(ch, start, length);
		if (isInId) id.append(ch, start, length);
	}

	ArrayList<ResultItem> getResults() {
		return results;
	}
}
