/*
 * Copyright (c) 2006-2025 Massachusetts General Hospital 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the i2b2 Software License v2.1 
 * which accompanies this distribution. 
 * 
 * Contributors:
 * 		Lori Phillips
 */


package edu.harvard.i2b2.ncbo.util;


/**
 * Define JAXB constants here.
 * For dynamic configuration, move these values to property file
 * and read from it.
 *
 * @author rkuttan
 */
public class JAXBConstant {
    public static final String[] DEFAULT_PACKAGE_NAME = new String[] {
            "edu.harvard.i2b2.ncboclient.datavo.ncbo.all"
        };
    
    public static final String[] DND_PACKAGE_NAME = new String[] {
        "edu.harvard.i2b2.ncboclient.datavo.dnd",
        "edu.harvard.i2b2.ncboclient.datavo.vdo"
    };
}
