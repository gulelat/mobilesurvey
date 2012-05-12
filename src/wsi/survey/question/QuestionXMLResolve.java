package wsi.survey.question;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class QuestionXMLResolve {

	public static void toQuestionNaire(QuestionNaire qn, String s) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		
		try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = null;
		try {
			doc = db.parse(new ByteArrayInputStream(s.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {

		} catch (SAXException e) {

		} catch (IOException e) {

		}
		doc.getDocumentElement().normalize();
		getQuestionNaire(qn, doc);
	}

	public static void getQuestionNaire(QuestionNaire qn, Document doc) {
		NodeList nodeLst = doc.getElementsByTagName("quiz");								// quiz

		Element e = (Element) nodeLst.item(0);
		
		NodeList nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("description");		// description
		Element element = (Element) nodeList.item(0);
		if (element == null) {
		}
		NodeList nodeList2 = ((Node) element).getChildNodes();
		qn.setDescription(nodeList2.item(0).getNodeValue());

		nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("xmltype");					// xmltype
		element = (Element) nodeList.item(0);
		if (element == null) {
		}
		nodeList2 = ((Node) element).getChildNodes();
		qn.setQuestionType(nodeList2.item(0).getNodeValue());

		nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("questionnum");				// questionnum
		element = (Element) nodeList.item(0);
		if (element == null) {
		}
		nodeList2 = ((Node) element).getChildNodes();
		qn.setQuestionNum(nodeList2.item(0).getNodeValue());

		nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("question");				//question(s)
		int l = nodeList.getLength();
		for (int i = 0; i < l; i++) {
			Node n = nodeList.item(i);
			QuestionItem item = new QuestionItem();
			getQuestionItem(item, n);

			qn.setQuestionItem(i, item);
		}
	}

	public static void getQuestionItem(QuestionItem item, Node doc) {
		if (doc.getNodeType() == Node.ELEMENT_NODE) {

			Element e = (Element) doc;

			NodeList nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("number");
			Element fstNmElmnt = (Element) nodeList.item(0);
			if (fstNmElmnt == null) {
			}
			NodeList fstNm = ((Node) fstNmElmnt).getChildNodes();
			item.setId((fstNm.item(0)).getNodeValue());

			nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("content");
			fstNmElmnt = (Element) nodeList.item(0);
			if (fstNmElmnt == null) {
			}
			fstNm = ((Node) fstNmElmnt).getChildNodes();
			item.setTitle((fstNm.item(0)).getNodeValue());

			nodeList = ((org.w3c.dom.Element) e).getElementsByTagName("option");
			int l = nodeList.getLength();
			for (int i = 0; i < l; i++) {
				fstNmElmnt = (org.w3c.dom.Element) nodeList.item(i);
				if (fstNmElmnt == null) {
				}
				fstNm = ((Node) fstNmElmnt).getChildNodes();
				item.setOption((fstNm.item(0)).getNodeValue());
			}
		}
	}
}
