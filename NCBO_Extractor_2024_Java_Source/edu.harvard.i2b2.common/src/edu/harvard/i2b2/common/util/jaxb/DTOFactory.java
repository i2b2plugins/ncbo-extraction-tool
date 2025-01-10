/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Rajesh Kuttan, Lori Phillips
 */

package edu.harvard.i2b2.common.util.jaxb;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;


public class DTOFactory {
	
    public XMLGregorianCalendar getXMLGregorianCalendar(long timeInMilliSec) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timeInMilliSec);

        return getXMLGregorianCalendar(cal);
    }

    public XMLGregorianCalendar getXMLGregorianCalendar(GregorianCalendar cal) {
        DatatypeFactory dataTypeFactory;
        XMLGregorianCalendar xmlCalendar = null;

        try {
            dataTypeFactory = DatatypeFactory.newInstance();
            xmlCalendar = dataTypeFactory.newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return xmlCalendar;
    }

    public XMLGregorianCalendar getXMLGregorianCalendarDate(int year,
        int month, int day) {
        DatatypeFactory dataTypeFactory;
        XMLGregorianCalendar xmlCalendar = null;

        try {
            dataTypeFactory = DatatypeFactory.newInstance();

            xmlCalendar = dataTypeFactory.newXMLGregorianCalendarDate(year,
                    month, day, 0);
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }

        return xmlCalendar;
    }
}
