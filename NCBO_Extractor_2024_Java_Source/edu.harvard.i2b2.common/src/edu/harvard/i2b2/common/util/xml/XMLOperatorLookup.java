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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author rkuttan
 *
 */
public class XMLOperatorLookup {
	
	static Map<String,String> operatorMap = new HashMap<String,String>();
	
	static { 
		operatorMap.put("GE", ">=");
		operatorMap.put("GT", ">");
		operatorMap.put("LT", "<");
		operatorMap.put("LE", "<=");
		operatorMap.put("EQ", "=");
		operatorMap.put("NE", "<>");
	}
	
	public static String getComparisonOperatorFromAcronum(String operatorAcronym) { 
		if (operatorAcronym == null) { 
			return null;
		}
		String comparisonOperator = operatorMap.get(operatorAcronym.toUpperCase());
		return comparisonOperator;
	}
}
