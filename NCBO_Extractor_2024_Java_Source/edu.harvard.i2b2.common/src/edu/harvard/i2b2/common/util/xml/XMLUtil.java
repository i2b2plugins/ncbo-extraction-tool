/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Rajesh Kuttan, Lori Phillips
 */

package edu.harvard.i2b2.common.util.xml;

//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
//import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import edu.harvard.i2b2.common.exception.I2B2Exception;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * XML utility class.
 * Contains functions like coverting DOM to string , string to DOM, etc.
 * @author rkuttan, smurphy
 */
public class XMLUtil {
    /**
     * Serialize given DOM document to string
     * @param element
     * @return
     * @throws I2B2Exception
     */
    public static String convertDOMToString(Document document)
        throws I2B2Exception {
        StringBuilder stringBuilder = null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputFormat outputformat = new OutputFormat();
        outputformat.setIndent(4);
        outputformat.setIndenting(true);
        outputformat.setPreserveSpace(false);

        XMLSerializer serializer = new XMLSerializer();
        serializer.setNamespaces(true);
        serializer.setOutputFormat(outputformat);
        serializer.setOutputByteStream(stream);

        try {
            serializer.asDOMSerializer();
            serializer.serialize(document.getDocumentElement());
        } catch (IOException e) {
            throw new I2B2Exception(e.getMessage(), e);
        }

        stringBuilder = new StringBuilder(stream.toString());

        return stringBuilder.toString();
    }

    /**
     * Serialize given DOM element document to string
     * @param element
     * @return
     * @throws I2B2Exception
     */
    public static String convertDOMElementToString(Element element)
        throws I2B2Exception {
        StringBuilder stringBuilder = null;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        OutputFormat outputformat = new OutputFormat();
        outputformat.setIndent(4);
        outputformat.setIndenting(true);
        outputformat.setPreserveSpace(false);

        XMLSerializer serializer = new XMLSerializer();
        serializer.setNamespaces(true);
        serializer.setOutputFormat(outputformat);
        serializer.setOutputByteStream(stream);

        try {
            serializer.asDOMSerializer();
            serializer.serialize(element);
        } catch (IOException e) {
            throw new I2B2Exception(e.getMessage(), e);
        }

        stringBuilder = new StringBuilder(stream.toString());

        return stringBuilder.toString();
    }

    /**
     * Convert string to DOM document
     * @param xmlString
     * @return
     * @throws I2B2Exception
     */
    public static Document convertStringToDOM(String xmlString)
        throws I2B2Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        Document document = null;

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(
                        new StringReader(xmlString)));
        } catch (ParserConfigurationException e) {
            throw new I2B2Exception(e.getMessage(), e);
        } catch (SAXException e) {
            throw new I2B2Exception(e.getMessage(), e);
        } catch (IOException e) {
            throw new I2B2Exception(e.getMessage(), e);
        }

        return document;
    }
    
	/**
	/* FindAndReplace - finds and replaces in StringBuffer theSBuffer,
	/*   starts at fromIndex, returns index of find.
	 * 
	 * @param findString, replaceString, sBuffer, index
     * @return int
	 * 
	 */
	public static int FindAndReplace(String find, String replace,
		StringBuffer theSBuffer, int fromIndex) {

		String interString;
		int theIndex, i, j;

		if (find == null) return -1;
		if (replace == null) return -1;
		if (theSBuffer == null) return -1;
		int theSBufferLength = theSBuffer.length();
		int findLength = find.length();		
		if (theSBufferLength == 0) return -1;
		if (findLength == 0) return -1;
		if (theSBufferLength < findLength) return -1;
		if ((fromIndex < 0)||(fromIndex > theSBufferLength)) return -1;

		interString = theSBuffer.toString();
		theIndex = interString.indexOf(find,fromIndex);
		if (theIndex == -1) return -1;
		
		//// on 9210 the following code ...
		for (i=theIndex;i<theSBufferLength-findLength;i++) {
			theSBuffer.setCharAt(i,theSBuffer.charAt(i+findLength));
		}
		for (j=theSBufferLength-1; j >= (theSBufferLength-findLength); j--) {
			theSBuffer.setCharAt(j,(char)(0));
		}
		int newLength = theSBufferLength-findLength;
		theSBuffer.setLength(newLength);
		theSBuffer.insert(theIndex,replace);
		return theIndex;
	}
	/**
	/* StrFindAndReplace - finds and replaces all in String theString.
	 * 
	 *  @param findString, replaceString, origString
     *  @return finalString
	 * 
	 */
	public static String StrFindAndReplace(String find, String replace,	String theString) {
		if (theString.length() == 0) return "";
		if (find.length() == 0) return theString;
		if (find.equals(replace)) return theString;

		StringBuffer theBuf = new StringBuffer(theString);
		int found=0;
		int iSearchFrom = 0;
 		while (found != -1) {
			found = FindAndReplace(find,replace,theBuf,iSearchFrom);
			iSearchFrom = found+replace.length();
		}
		return theBuf.toString();
	}
    
    
}
