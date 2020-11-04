package org.wrkr.clb.services.util.dom;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class DOMUtils {

    public static NodeList getNodes(Document document, String expression) throws XPathExpressionException {
        XPathExpression xPathExpression = XPathFactory.newInstance().newXPath().compile(expression);
        return (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
    }
}
