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
 * StringUtil class to perform string parsing tasks
 * This is singleton class.
 * @author lcp5
 */
public class StringUtil {

    //to make this class singleton
    private static StringUtil thisInstance;
    
    static {
            thisInstance = new StringUtil();
    }
    
    public static StringUtil getInstance() {
        return thisInstance;
    }
    
    public static String getTableCd(String fullPath) {
    	if(fullPath == null)
    		return null;
    	else {
    		int end;
			try {
				end = fullPath.indexOf("\\", 3);
				return fullPath.substring(2, end).trim();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				return fullPath.substring(2).trim();
			}    		
    	}
    }
    
    public static String getPath(String fullPath) {
    	if(fullPath == null)
    		return null;
    	else {
    		int end;
			try {
				end = fullPath.indexOf("\\", 3);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				return null;
			}
    		return fullPath.substring(end).trim();
    	}
    }
    
}